package com.friendlyplaces.friendlyapp.activities.review;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class ReviewStepperAdapter extends AbstractFragmentStepAdapter {

    private static String CURRENT_STEP_POSITION_KEY = "CURRENT_STEP_POSITION_KEY";

    public ReviewStepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                final Step1Fragment step = new Step1Fragment();
                return step;
            case 1:
                final Step2Fragment step2 = new Step2Fragment();
                return step2;
            case 2:
                final Step3Fragment step3 = new Step3Fragment();
                return step3;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle(R.string.Step1_tabTitle) //can be a CharSequence instead
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle(R.string.Step2_tabTitle) //can be a CharSequence instead
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle(R.string.Step3_tabTable) //can be a CharSequence instead
                        .create();
            default:
                return new StepViewModel.Builder(context)
                        .setTitle("Titul per defecte") //can be a CharSequence instead
                        .create();

        }

    }
}