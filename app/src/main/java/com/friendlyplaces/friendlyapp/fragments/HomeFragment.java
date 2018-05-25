package com.friendlyplaces.friendlyapp.fragments;


import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.activities.detailedplace.DetailedPlaceActivity;
import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants;
import com.friendlyplaces.friendlyapp.utilities.MarkerColorUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.friendlyplaces.friendlyapp.utilities.FirestoreConstants.COLLECTION_FRIENDLYPLACES;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<FriendlyPlace> {

    GoogleMap mMap;
    Fragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    @BindView(R.id.home_fragment_center_map_fab)
    FloatingActionButton centerLocationFab;
    public static final int PLACE_PICKER_REQUEST = 1;

    private ClusterManager<FriendlyPlace> mClusterManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        mapFragment = getChildFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment) mapFragment;
        supportmapfragment.getMapAsync(this);
        ButterKnife.bind(this, v);
        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null && mClusterManager != null) {
            reloadFriendlyPlaces();
        }
    }

    private void reloadFriendlyPlaces() {
        FirebaseFirestore.getInstance().collection(COLLECTION_FRIENDLYPLACES).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mClusterManager.clearItems();
                    for (DocumentSnapshot document :
                            task.getResult()) {
                        FriendlyPlace place = document.toObject(FriendlyPlace.class);
                        mClusterManager.addItem(place);
                    }
                    mClusterManager.cluster();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
        enableMyLocation();
        if (mMap.isMyLocationEnabled()) {

            centerMapToUserLocation();
            mClusterManager = new ClusterManager<>(getActivity(), mMap);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.setRenderer(new FriendlyPlaceClusterItemRenderer(getContext(), mMap, mClusterManager));

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {

                                    FriendlyPlace lloc = document.toObject(FriendlyPlace.class);

                                    mClusterManager.addItem(lloc);

                                }
                                mClusterManager.cluster();
                            }
                        }
                    });


        }
    }

    @OnClick(R.id.home_fragment_center_map_fab)
    public void centerMapToUserLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(centerLocationFab.getRootView(), R.string.check_location, Snackbar.LENGTH_LONG).show();
            return;
        }
        enableMyLocation();

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(16)
                            .tilt(0)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    Snackbar.make(mapFragment.getView(), R.string.check_location, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(FriendlyPlace friendlyPlace) {
        Intent intent = new Intent(getContext(), DetailedPlaceActivity.class);
        intent.putExtra("placeId", friendlyPlace.pid);
        intent.putExtra("placeName", friendlyPlace.name);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }


    public class FriendlyPlaceClusterItemRenderer extends DefaultClusterRenderer<FriendlyPlace> {
        @Override
        protected void onBeforeClusterItemRendered(FriendlyPlace item, MarkerOptions markerOptions) {
            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(MarkerColorUtil.getColor(item));
            markerOptions.icon(markerDescriptor);
        }

        public FriendlyPlaceClusterItemRenderer(Context context, GoogleMap map, ClusterManager<FriendlyPlace> clusterManager) {
            super(context, map, clusterManager);
        }
    }
}



