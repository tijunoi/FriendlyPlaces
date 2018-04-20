package com.friendlyplaces.friendlyapp.activities.review;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step3Fragment extends Fragment implements BlockingStep {


    public Step3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step3, container, false);
        ButterKnife.bind(this,v);

        return v;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        //todo: upload to firebase
        AddReviewViewModel model = ViewModelProviders.of(getActivity()).get(AddReviewViewModel.class);
        Snackbar.make(getView(),"Raviuw: " + String.valueOf(model.getReview().getVote().getVoto()) + " comment: " + model.getReview().getComment(),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
