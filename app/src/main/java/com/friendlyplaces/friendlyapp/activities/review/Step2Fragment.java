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
import android.widget.EditText;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step2Fragment extends Fragment implements BlockingStep{

    @BindView(R.id.et_addReview_step2)
    EditText addReview;

    public Step2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step2, container, false);

        ButterKnife.bind(this, v);



        return v;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (addReview.getText().toString().trim().length() < 40){
            return new VerificationError("Debes escribir una reseña de 40 caracteres como mínimo");
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(getView(), error.getErrorMessage(), Snackbar.LENGTH_LONG).setAction("OK", null).show();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {


        AddReviewViewModel model = ViewModelProviders.of(getActivity()).get(AddReviewViewModel.class);

        model.getReview().setComment(addReview.getText().toString().trim());
        callback.goToNextStep();
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
