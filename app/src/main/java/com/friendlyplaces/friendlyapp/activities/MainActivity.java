package com.friendlyplaces.friendlyapp.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.activities.detailedplace.DetailedPlaceActivity;
import com.friendlyplaces.friendlyapp.authentication.AuthenticationActivity;
import com.friendlyplaces.friendlyapp.fragments.HomeFragment;
import com.friendlyplaces.friendlyapp.utilities.Utils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private TextView emailDrawerTextview;
    private CircleImageView profilePictureCircleImageView;
    private TextView tv_appbar;
    private LinearLayout linearProfile;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.85F);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_floating_search_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                OnTryingPickingAPlace();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new HomeFragment());
        tx.commit();

        mFirebaseAuth = FirebaseAuth.getInstance();

        final android.support.v7.widget.Toolbar appbar = findViewById(R.id.appbar_main);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tv_appbar = findViewById(R.id.clickable_appbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.navview);
        tv_appbar.setOnClickListener(this);

        View headerView = navView.getHeaderView(0);
        emailDrawerTextview = headerView.findViewById(R.id.user_email_drawer_textview);
        profilePictureCircleImageView = headerView.findViewById(R.id.profile_picture_navigation_drawer);
        linearProfile = headerView.findViewById(R.id.linearProfile);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        android.support.v4.app.Fragment fragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.op_home:
                                fragment = new HomeFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.op_pos_rated:
                                Intent intent = new Intent(MainActivity.this, PlaceListActivity.class);
                                intent.putExtra(PlaceListActivity.QUERY_TYPE_KEY, PlaceListActivity.POSITIVE_PLACES);
                                intent.putExtra(PlaceListActivity.TITLE_KEY, menuItem.getTitle());
                                startActivity(intent);

                                break;
                            case R.id.op_neg_rated:
                                Intent intent2 = new Intent(MainActivity.this, PlaceListActivity.class);
                                intent2.putExtra(PlaceListActivity.QUERY_TYPE_KEY, PlaceListActivity.NEGATIVE_PLACES);
                                intent2.putExtra(PlaceListActivity.TITLE_KEY, menuItem.getTitle());
                                startActivity(intent2);
                                break;
                            case R.id.op_rated_places:
                                Intent intent3 = new Intent(MainActivity.this, PlaceListActivity.class);
                                intent3.putExtra(PlaceListActivity.QUERY_TYPE_KEY, PlaceListActivity.OWN_VOTED_PLACES);
                                intent3.putExtra(PlaceListActivity.TITLE_KEY, menuItem.getTitle());
                                startActivity(intent3);
                                break;
                            case R.id.op_logoff:
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                break;
                        }
                        menuItem.setChecked(false);

                        if (fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }


                        drawerLayout.closeDrawers();

                        return true;
                    }
                }
        );

        linearProfile.setOnClickListener(this);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //signed in

                    emailDrawerTextview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profilePictureCircleImageView);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HomeFragment.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(this, data);
                Intent intent = new Intent(this, DetailedPlaceActivity.class);
                intent.putExtra("placeId", place.getId());
                intent.putExtra("placeName", place.getName());
                startActivity(intent);
            }
        }
    }

    public void OnTryingPickingAPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), HomeFragment.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.clickable_appbar:
                v.setAnimation(buttonClick);
                OnTryingPickingAPlace();
                break;
            case R.id.linearProfile:
                startActivity(new Intent(this, EditProfileActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
        }
    }
}
