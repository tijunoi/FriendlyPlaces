package com.friendlyplaces.friendlyapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Nil Ordoñez on 25/1/18.
 */

public class FriendlyPlace implements ClusterItem {
    public String pid;
    public String name;
    public float avgRating;
    public int reviewCount;


    public GeoPoint location;

    public FriendlyPlace() {
    }

    public FriendlyPlace(String pid, float avgRating, String name, int reviewCount, double lat, double lng) {
        this.pid = pid;
        this.avgRating = avgRating;
        this.name = name;
        this.reviewCount = reviewCount;
        this.location = new GeoPoint(lat, lng);
    }

    public FriendlyPlace(String pid, float avgRating, String name, int reviewCount, LatLng latLng) {
        this.pid = pid;
        this.avgRating = avgRating;
        this.name = name;
        this.reviewCount = reviewCount;
        this.location = new GeoPoint(latLng.latitude, latLng.longitude);
    }

    public FriendlyPlace(String pid, float avgRating, String name, int reviewCount, GeoPoint location) {
        this.pid = pid;
        this.avgRating = avgRating;
        this.name = name;
        this.reviewCount = reviewCount;
        this.location = location;
    }

   /* public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }


    //Reconverteixo a LatLng


    public GeoPoint getGeoPoint(){
        return location;
    }*/

    public LatLng getLatLng() {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public LatLng getPosition() {
        return getLatLng();
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return "Pun. media: " + String.valueOf(avgRating);
    }
}

