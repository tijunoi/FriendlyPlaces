package com.friendlyplaces.friendlyapp.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.authentication.AuthenticationActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class IntroActivity extends AppIntro {

    private final int REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide1_title), getString(R.string.slide1_description), R.mipmap.ic_launcher, getResources().getColor(R.color.colorAccent, null)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide2_title), getString(R.string.slide2_description), R.drawable.ff_slide2, getResources().getColor(android.R.color.holo_blue_dark, null)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide3_title), getString(R.string.slide3_description), R.drawable.ff_slide3, getResources().getColor(android.R.color.holo_green_dark, null)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.slide4_title), getString(R.string.slide4_description), R.drawable.valoracion_fp, getResources().getColor(android.R.color.holo_orange_dark, null)));

    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {

        super.onSkipPressed(currentFragment);
        onDonePressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            finish();
            startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            finish();
            startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
        }
    }
}
