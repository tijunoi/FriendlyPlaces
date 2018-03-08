package com.friendlyplaces.friendlyapp.model;

/**
 * Created by Nil Ordoñez on 8/3/18.
 */

/**
 * Clase que guarda els datos de una review. Model no definitiu.
 * De moment guardo ejemplo perque ho vegis
 */
public class Review {

    private String uid;
    private String placeId;

    private double rating;
    private String comment;

    public Review(String uid, String placeId, double rating, String comment) {
        this.uid = uid;
        this.placeId = placeId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
