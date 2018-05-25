package com.friendlyplaces.friendlyapp.model;

public class ReviewWithUser {
    FriendlyUser friendlyUser;
    Review review;

    public ReviewWithUser(FriendlyUser friendlyUser, Review review) {
        this.friendlyUser = friendlyUser;
        this.review = review;
    }

    public ReviewWithUser() {
    }

    public FriendlyUser getFriendlyUser() {
        return friendlyUser;
    }

    public void setFriendlyUser(FriendlyUser friendlyUser) {
        this.friendlyUser = friendlyUser;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }


}
