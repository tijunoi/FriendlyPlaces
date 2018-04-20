package com.friendlyplaces.friendlyapp.activities.detailed_place;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.activities.review.ReviewActivity;
import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants;
import com.friendlyplaces.friendlyapp.utilities.SharedPrefUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedPlaceActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    //VIEWMODEL
    DetailedPlaceViewModel model;

    //Views
    @BindView(R.id.det_ubicacion) TextView tvUbi;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.like_button) SparkButton likeButton;
    @BindView(R.id.dislike_button) SparkButton dislikeButton;
    @BindView(R.id.box_opiniones) TextView tvOpiniones;
    @BindView(R.id.voteLike) TextView numLike;
    @BindView(R.id.voteDislike) TextView numDislike;

    //------ Properties varias
    private CharSequence placeUbication;
    private GeoDataClient geoDataClient;
    private String name;
    private String id;

    //------ MAP FRAGMENT PROPERTIES
    GoogleMap mMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_detailed_place);
        Slide slideTransition = new Slide();
        slideTransition.setDuration(250);
        getWindow().setEnterTransition(slideTransition);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        model = ViewModelProviders.of(this).get(DetailedPlaceViewModel.class);

        //GET DATA FROM INTENT
        name = getIntent().getStringExtra("placeName");
        id = getIntent().getStringExtra("placeId");

        geoDataClient = Places.getGeoDataClient(this);
        getPhotos();
        checkIfPlaceExistsInFirestore();

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(name);
        getPhotos();


        mFab.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showReviewTutorial();
            }
        },3000);

    }

    private void showReviewTutorial() {
        if (!SharedPrefUtil.hasCompletedDetailedPlaceTutorial(this)) {
            TapTargetView.showFor(
                    this,
                    TapTarget.forView(mFab,"Quieres añadir tu experiencia?","Pulsa este botón para añadir una reseña!")
                            .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                            .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .icon(getDrawable(R.drawable.ic_rate_review_black_24dp))// Specify a custom drawable to draw as the target
                    ,
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            SharedPrefUtil.setDetailedPlaceTutorialCompleted(DetailedPlaceActivity.this);
                        }
                    });
        }
    }

    private void checkIfPlaceExistsInFirestore() {
        FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    FriendlyPlace aux =  documentSnapshot.toObject(FriendlyPlace.class);
                    model.getFriendlyPlace().pid = aux.pid;
                    model.getFriendlyPlace().location = aux.location;
                    model.getFriendlyPlace().name = aux.name;
                    model.getFriendlyPlace().negativeVotes = aux.negativeVotes;
                    model.getFriendlyPlace().positiveVotes = aux.positiveVotes;
                    model.getFriendlyPlace().reviewCount = aux.reviewCount;
                    model.getFriendlyPlace().address = aux.address;
                    updateUI();
                    setUpMap();
                } else {
                    //getear de google maps y subirlo a firestore
                    getPlaceFromGoogleMaps();
                }
            }
        });
    }

    private void getPlaceFromGoogleMaps() {
        //con el id hago una query a la api de google places i seteo las cosis
        Task<PlaceBufferResponse> placeById = geoDataClient.getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);

                    //friendlyPlace = new FriendlyPlace(mPlace.getId(), mPlace.getRating(), String.valueOf(mPlace.getName()), 1, mPlace.getLatLng());
                    model.getFriendlyPlace().name = String.valueOf(myPlace.getName());
                    model.getFriendlyPlace().location = new GeoPoint(myPlace.getLatLng().latitude,myPlace.getLatLng().longitude);
                    model.getFriendlyPlace().pid = myPlace.getId();
                    model.getFriendlyPlace().reviewCount = 0;
                    model.getFriendlyPlace().positiveVotes = 0;
                    model.getFriendlyPlace().negativeVotes = 0;
                    model.getFriendlyPlace().address = String.valueOf(myPlace.getAddress());

                    updateUI();
                    uploadPlaceDataToFirestore();

                    //LOGS
                    Log.i(DetailedPlaceActivity.class.getName(),"Place obtained from Google Places API");
                    Log.i(DetailedPlaceActivity.class.getName(), "Place found: " + myPlace.getName());
                } else {
                    Log.e("DetailedPlaceERROR", "Place no encontrada");
                }
                task.getResult().release();
            }
        });
    }

    private void uploadPlaceDataToFirestore() {
        FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES)
                .document(model.getFriendlyPlace().pid)
                .set(model.getFriendlyPlace())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Log.i("DetailedPlaceAcitivty","Se ha subido el Place a Firestore");
                        else Log.e("DetailedPlaceActivity","Error al subir el place a Firebase");
                    }
                });
    }

    private void updateUI() {
        System.out.println(model.getFriendlyPlace().address);
        tvUbi.setText(model.getFriendlyPlace().address);
        likeButton.setChecked(false);
        likeButton.setEnabled(false);
        dislikeButton.setChecked(false);
        dislikeButton.setEnabled(false);
        numLike.setText(String.valueOf(model.getFriendlyPlace().positiveVotes));
        numDislike.setText(String.valueOf(model.getFriendlyPlace().negativeVotes));
        tvOpiniones.setText("Hay " + String.valueOf(model.getFriendlyPlace().reviewCount) + " opiniones.");
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailed_place_map_fragment);
        mapFragment.getMapAsync(this);
    }


    private void getPhotos() {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = geoDataClient.getPlacePhotos(id);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                try {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);


                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            setBackgroundImage(bitmap);
                        }
                    });

                } catch (IllegalStateException ignore) {

                }
                photoMetadataBuffer.release();
            }
        });
    }
    public void setBackgroundImage(Bitmap bitmap){
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        appBarLayout.setBackground(drawable);
    }

    @Override
    public void onClick(View view) {
       startActivity(new Intent(DetailedPlaceActivity.this, ReviewActivity.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(model.getFriendlyPlace().getLatLng(),17.0f));
        mMap.addMarker(new MarkerOptions().position(model.getFriendlyPlace().getLatLng()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
