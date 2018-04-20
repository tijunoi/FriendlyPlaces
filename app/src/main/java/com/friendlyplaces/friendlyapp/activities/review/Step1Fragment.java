package com.friendlyplaces.friendlyapp.activities.review;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.model.Review;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step1Fragment extends Fragment implements BlockingStep{

    @BindView(R.id.like_button_step1)
    SparkButton likeButton;
    @BindView(R.id.dislike_button_step1)
    SparkButton dislikeButton;
    @BindView(R.id.tv_step1)
    TextView titleStep1;

    public Step1Fragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step1, container, false);

        ButterKnife.bind(this, v);

        likeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState){
                    dislikeButton.setChecked(false);

                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        dislikeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState){
                    likeButton.setChecked(false);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        return v;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (!likeButton.isChecked()) {
            if (!dislikeButton.isChecked()) {
                return new VerificationError("Debes votar para continuar");
            }
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(getView(), error.getErrorMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        AddReviewViewModel model = ViewModelProviders.of(getActivity()).get(AddReviewViewModel.class);
        if (likeButton.isChecked()){
            model.getReview().setVote(Review.Vote.POSITIVO);
            callback.goToNextStep();
        }else{
            model.getReview().setVote(Review.Vote.NEGATIVO);
            callback.goToNextStep();
        }

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
