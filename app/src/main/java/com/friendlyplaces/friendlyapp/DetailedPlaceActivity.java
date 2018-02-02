package com.friendlyplaces.friendlyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

public class DetailedPlaceActivity extends AppCompatActivity {

    public static GoogleApiClient mGoogleApiClient;
    private CharSequence placeUbication, placePhone;
    private int placeRate;
    private TextView tvUbi, tvPhone;
    private RatingBar rbStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String name = getIntent().getStringExtra("placeName");
        String id = getIntent().getStringExtra("placeId");

        Toast.makeText(this, name, Toast.LENGTH_LONG).show();

        tvUbi = findViewById(R.id.det_ubicacion);
        tvPhone = findViewById(R.id.det_num_phone);
        rbStars = findViewById(R.id.ratingBar);


        //con el id hago una query a la api de google places i seteo las cosis

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            placeUbication = myPlace.getAddress();
                            placePhone = myPlace.getPhoneNumber();
                            placeRate = ((int) myPlace.getRating());

                            tvUbi.setText(placeUbication);
                            tvPhone.setText(placePhone);
                            rbStars.setNumStars(placeRate);
                            Log.i("TAGAGAPLACES", "Place found: " + myPlace.getName());
                        }else {
                            Log.e("TAGAGAGPLACE", "Place no encontrada");
                        }
                        places.release();
                    }
                });


        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
}
