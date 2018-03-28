package com.friendlyplaces.friendlyapp.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailedPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    private CharSequence placeUbication, placePhone;
    private Float placeRate;
    private TextView tvUbi, tvPhone;
    private RatingBar rbStars;
    private GeoDataClient geoDataClient;
    private PlacePhotoMetadataResponse mPlacePhotoMetadataResponse;
    private String name, id;
    private AppBarLayout appBarLayout;
    private FloatingActionButton mFab;
    private Place mPlace;
    private FriendlyPlace friendlyPlace;

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

        name = getIntent().getStringExtra("placeName");
        id = getIntent().getStringExtra("placeId");

        Toast.makeText(this, name, Toast.LENGTH_LONG).show();

        tvUbi = findViewById(R.id.det_ubicacion);
        tvPhone = findViewById(R.id.det_num_phone);
        rbStars = findViewById(R.id.ratingBar);
        geoDataClient = Places.getGeoDataClient(this, null);
        Place place;

        //con el id hago una query a la api de google places i seteo las cosis
        Task<PlaceBufferResponse> placeById = geoDataClient.getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    @SuppressLint("RestrictedApi") Place myPlace = places.get(0);
                    mPlace = myPlace;

                    friendlyPlace = new FriendlyPlace(mPlace.getId(), mPlace.getRating(), String.valueOf(mPlace.getName()), 1, mPlace.getLatLng());


                    placeUbication = myPlace.getAddress();
                    placePhone = myPlace.getPhoneNumber();
                    placeRate = myPlace.getRating();

                    tvUbi.setText(placeUbication);
                    tvPhone.setText(placePhone);
                    rbStars.setIsIndicator(true);
                    rbStars.setRating(placeRate);

                    Log.i("TAGAGAPLACES", "Place found: " + myPlace.getName());
                } else {
                    Log.e("TAGAGAGPLACE", "Place no encontrada");
                }
                task.getResult().release();
            }
        });



        appBarLayout = findViewById(R.id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(name);
        getPhotos();

        mFab = findViewById(R.id.fab);
        FloatingActionButton fab = (FloatingActionButton) mFab;
        mFab.setOnClickListener(this);


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("FriendlyPlaces").document(friendlyPlace.pid).set(friendlyPlace).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(mFab, "Se ha subido los datos del sitio a Firebase", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
