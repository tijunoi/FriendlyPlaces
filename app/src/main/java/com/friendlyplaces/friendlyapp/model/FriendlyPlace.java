package com.friendlyplaces.friendlyapp.model;

import com.google.android.gms.location.places.Place;

/**
 * Created by Nil Ordoñez on 25/1/18.
 */

public class FriendlyPlace {
    public String pid;
    public Place mPlace;
    public int numVotosPos;
    public int numVotosNeg;

    public FriendlyPlace(String pid, Place place, int numVotosPos, int numVotosNeg) {
        this.pid = pid;
        mPlace = place;
        this.numVotosPos = numVotosPos;
        this.numVotosNeg = numVotosNeg;
    }
}
