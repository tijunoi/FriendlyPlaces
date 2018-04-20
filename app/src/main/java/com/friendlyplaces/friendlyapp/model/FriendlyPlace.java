package com.friendlyplaces.friendlyapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Nil Ordoñez on 25/1/18.
 */

@IgnoreExtraProperties
public class FriendlyPlace implements ClusterItem {
    public String pid;
    public String name;
    public int positiveVotes;
    public int negativeVotes;
    public int reviewCount;


    public GeoPoint location;

    public FriendlyPlace() {
    }

    public FriendlyPlace(String pid, String name, int positiveVotes, int negativeVotes, int reviewCount, double lat, double lng) {
        this.pid = pid;
        this.name = name;
        this.positiveVotes = positiveVotes;
        this.negativeVotes = negativeVotes;
        this.reviewCount = reviewCount;
        this.location = new GeoPoint(lat, lng);
    }

    public FriendlyPlace(String pid, String name, int positiveVotes, int negativeVotes, int reviewCount, LatLng latLng) {
        this.pid = pid;
        this.name = name;
        this.positiveVotes = positiveVotes;
        this.negativeVotes = negativeVotes;
        this.reviewCount = reviewCount;
        this.location = new GeoPoint(latLng.latitude, latLng.longitude);
    }

    public FriendlyPlace(String pid, String name, int positiveVotes, int negativeVotes, int reviewCount, GeoPoint location) {
        this.pid = pid;
        this.name = name;
        this.positiveVotes = positiveVotes;
        this.negativeVotes = negativeVotes;
        this.reviewCount = reviewCount;
        this.location = location;
    }

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
        return "Puntuación: " + String.valueOf(getPuntuation());
    }

    public int getPuntuation(){
        return positiveVotes - negativeVotes;
    }
}

