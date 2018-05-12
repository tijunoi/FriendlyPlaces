package com.friendlyplaces.friendlyapp.activities.detailedplace;

import android.arch.lifecycle.ViewModel;

import com.friendlyplaces.friendlyapp.model.FriendlyPlace;

/**
 * Created by Nil Ordoñez on 20/4/18.
 */
public class DetailedPlaceViewModel extends ViewModel {

    private FriendlyPlace friendlyPlace = new FriendlyPlace();

    public FriendlyPlace getFriendlyPlace() {
        return friendlyPlace;
    }

    public void setFriendlyPlace(FriendlyPlace friendlyPlace) {
        this.friendlyPlace = friendlyPlace;
    }
}
