package com.friendlyplaces.friendlyapp.activities.review;

import android.arch.lifecycle.ViewModel;

import com.friendlyplaces.friendlyapp.model.Review;

public class AddReviewViewModel extends ViewModel {

    private Review review = new Review();
    private String placeId;
    private String placeName;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
