package com.friendlyplaces.friendlyapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PointOfInterest;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener,
        GoogleMap.OnPoiClickListener {

    GoogleMap mMap;
    Fragment mapFragment;
    FragmentManager fmanager;
    FloatingActionButton floatingActionButton;
    public static final int PLACE_PICKER_REQUEST = 1;
    OnPlacePickedListener mOnPlacePickedListener;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);

        floatingActionButton = v.findViewById(R.id.find_my_location);
        floatingActionButton.setOnClickListener(this);
        mapFragment = getChildFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment)mapFragment;
        supportmapfragment.getMapAsync(this);
        return v;

    }

    /**
     * Referencia del marker visualizandose actualmente. Se guarda para poder borrarlo luego, ya que GoogleMap no tiene el método
     */
    Marker currentMarker;
    PointOfInterest currentPointOfInterest; //para comparar el place id

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

        }
        //getMapAsync(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlacePickedListener) {
            mOnPlacePickedListener = (OnPlacePickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnPoiClickListener(this);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            enableMyLocation();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        if (currentMarker != null) {
            if (pointOfInterest.placeId.equals(currentPointOfInterest.placeId)) {
                //do nothing
            } else {
                currentMarker.remove();
                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng));
            }
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_my_location:
                mOnPlacePickedListener.OnTryingPickingAPlace();
                break;
        }

    }
}

interface OnPlacePickedListener{
    void OnTryingPickingAPlace();
}

}
