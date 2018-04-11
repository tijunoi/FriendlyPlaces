package com.friendlyplaces.friendlyapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Nil Ordoñez on 27/3/18.
 */

public class FriendlyPlaceClusterItem implements ClusterItem {

    private final FriendlyPlace mFriendlyPlace;

    public FriendlyPlaceClusterItem(FriendlyPlace friendlyPlace) {
        mFriendlyPlace = friendlyPlace;
    }


    @Override
    public LatLng getPosition() {
        return mFriendlyPlace.getLatLng();
    }

    @Override
    public String getTitle() {
        return mFriendlyPlace.name;
    }

    @Override
    public String getSnippet() {
        return "Pun. media: " + String.valueOf(mFriendlyPlace.avgRating);
    }
}
