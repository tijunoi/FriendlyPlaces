package com.friendlyplaces.friendlyapp;

import android.Manifest;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("Bienvenido a Friendly Places", "Continua para aprender como funciona la app", R.drawable.ic_arrow_forward_white, getResources().getColor(R.color.colorAccent)));
        addSlide(AppIntro2Fragment.newInstance("Slide 2", "Continua para aprender como funciona la app", R.drawable.ic_arrow_forward_white, getResources().getColor(android.R.color.holo_blue_dark)));
        addSlide(AppIntro2Fragment.newInstance("Slide 3", "Awui se te pedirá tu ubicacion", R.drawable.ic_arrow_forward_white, getResources().getColor(android.R.color.holo_green_dark)));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
    }
}
