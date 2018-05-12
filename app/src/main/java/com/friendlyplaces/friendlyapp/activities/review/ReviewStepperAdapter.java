package com.friendlyplaces.friendlyapp.activities.review;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.friendlyplaces.friendlyapp.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class ReviewStepperAdapter extends AbstractFragmentStepAdapter {

    public ReviewStepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return new Step1Fragment();
            case 1:
                return new Step2Fragment();
            case 2:
                return new Step3Fragment();
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
                        .setTitle("This will never happen but Java forces us to have a default") //can be a CharSequence instead
                        .create();

        }

    }
}