package com.friendlyplaces.friendlyapp.activities.review;

import android.arch.lifecycle.ViewModel;

import com.friendlyplaces.friendlyapp.model.Review;

public class AddReviewViewModel extends ViewModel {

    private Review review = new Review();

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
