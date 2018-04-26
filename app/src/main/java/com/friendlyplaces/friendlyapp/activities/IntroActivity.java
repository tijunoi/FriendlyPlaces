package com.friendlyplaces.friendlyapp.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.authentication.AuthenticationActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class IntroActivity extends AppIntro  {

    private int REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide1_title), getString(R.string.slide1_description), R.mipmap.ic_launcher, getResources().getColor(R.color.colorAccent)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide2_title), getString(R.string.slide2_description), R.drawable.ff_slide2, getResources().getColor(android.R.color.holo_blue_dark)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide3_title), getString(R.string.slide3_description), R.drawable.ff_slide3, getResources().getColor(android.R.color.holo_green_dark)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide4_title), getString(R.string.slide4_description), R.mipmap.ic_launcher, getResources().getColor(android.R.color.holo_orange_dark)));

        //paso 2 captura del layout de la review
        //paso 3 vota!
        //paso 4 escribe tu reseña y ea ggwp
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {

        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        onDonePressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            finish();
            startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
        }

        //startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            finish();
            startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
        }
    }
}
