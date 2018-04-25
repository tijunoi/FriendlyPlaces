package com.friendlyplaces.friendlyapp.activities.review;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.Window;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.StepperLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity holder for the stepper layout. We post the review to Firebase in the last StepFragment of the stepper
 * to show progress while the network call is being made. Unfortunately, the library does not allow to do it
 * from the activity, so we have to do it from the fragment.
 */
public class ReviewActivity extends AppCompatActivity {

    private AddReviewViewModel model;

    @BindView(R.id.stepperLayout) StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Explode animation = new Explode();
        animation.setDuration(250);
        getWindow().setEnterTransition(animation);
        setContentView(R.layout.activity_review);

        model = ViewModelProviders.of(this).get(AddReviewViewModel.class);

        String placeId = getIntent().getStringExtra("placeId");
        String placeName = getIntent().getStringExtra("placeName");

        model.setPlaceId(placeId);
        model.setPlaceName(placeName);
        model.getReview().setPlaceId(placeId);

        ButterKnife.bind(this);
        stepperLayout.setShowErrorStateEnabled(true);
        stepperLayout.setAdapter(new ReviewStepperAdapter(getSupportFragmentManager(), this));
    }

}
