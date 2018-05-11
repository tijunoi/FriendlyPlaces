package com.friendlyplaces.friendlyapp.activities.detailed_place;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.activities.detailed_place.reviews.ReviewsListActivity;
import com.friendlyplaces.friendlyapp.activities.detailed_place.reviews.ReviewsListActivityKt;
import com.friendlyplaces.friendlyapp.activities.review.ReviewActivity;
import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants;
import com.friendlyplaces.friendlyapp.utilities.SharedPrefUtil;
import com.friendlyplaces.friendlyapp.utilities.Utils;
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
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.varunest.sparkbutton.SparkButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedPlaceActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    //VIEWMODEL
    DetailedPlaceViewModel model;

    //Views
    @BindView(R.id.det_ubicacion) TextView tvUbi;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.like_button) SparkButton likeButton;
    @BindView(R.id.dislike_button) SparkButton dislikeButton;
    @BindView(R.id.box_opiniones) TextView tvOpiniones;
    @BindView(R.id.voteLike) TextView numLike;
    @BindView(R.id.voteDislike) TextView numDislike;
    @BindView(R.id.vermas_button) Button seeMoreButton;

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
        //getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        model = ViewModelProviders.of(this).get(DetailedPlaceViewModel.class);

        //GET DATA FROM INTENT
        name = getIntent().getStringExtra("placeName");
        id = getIntent().getStringExtra("placeId");

        geoDataClient = Places.getGeoDataClient(this);
        getPhotos();

        collapsingToolbarLayout.setTitle(name);
        getPhotos();

        seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.preventTwoClick(view);
                Intent intent = new Intent(DetailedPlaceActivity.this, ReviewsListActivity.class);
                intent.putExtra(ReviewsListActivityKt.PLACE_ID_KEY, model.getFriendlyPlace().pid);
                intent.putExtra(ReviewsListActivityKt.PLACE_NAME_KEY, model.getFriendlyPlace().name);
                startActivity(intent);
            }
        });

        mFab.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showReviewTutorial();
            }
        },3000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfPlaceExistsInFirestore();
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
                    setUpMap();
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
        collapsingToolbarLayout.setTitle(model.getFriendlyPlace().name);
        tvUbi.setText(model.getFriendlyPlace().address);
        likeButton.setChecked(false);
        likeButton.setEnabled(false);
        dislikeButton.setChecked(false);
        dislikeButton.setEnabled(false);
        numLike.setText(String.valueOf(model.getFriendlyPlace().positiveVotes));
        numDislike.setText(String.valueOf(model.getFriendlyPlace().negativeVotes));
        tvOpiniones.setText(getString(R.string.detailed_place_opinions_textview_text,model.getFriendlyPlace().reviewCount));
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailed_place_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
        Utils.preventTwoClick(view);
       Intent intent =  new Intent(DetailedPlaceActivity.this, ReviewActivity.class);
       intent.putExtra("placeId",model.getFriendlyPlace().pid);
       intent.putExtra("placeName",model.getFriendlyPlace().name);
       startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_place,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishAfterTransition();
                break;
            case R.id.action_share:
                createSharingURL();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createSharingURL() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://friendly-places-3ea3c.firebaseapp.com/" + model.getFriendlyPlace().pid))
                .setDynamicLinkDomain("a7fz8.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink().addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()){
                    shareFriendlyPlace(task.getResult().getShortLink());
                } else Objects.requireNonNull(task.getException()).printStackTrace();
            }
        });

    }

    private void shareFriendlyPlace(Uri link) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Chequea " + model.getFriendlyPlace().name + " en FriendlyPlaces! " + link.toString());
        intent.setType("text/plain");
        startActivity(intent);
    }
}
