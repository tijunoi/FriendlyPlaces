package com.friendlyplaces.friendlyapp.activities.review;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    @BindView(R.id.stepperLayout) StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //aixo es com el findbyid
        ButterKnife.bind(this);
        stepperLayout.setAdapter(new ReviewStepperAdapter(getSupportFragmentManager(), this));

    }

    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {

    }

    @Override
    public void onReturn() {

    }
}
