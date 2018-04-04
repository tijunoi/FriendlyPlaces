package com.friendlyplaces.friendlyapp.model;

/**
 * Created by Bertiwi on 28/03/2018.
 */

public class FriendlyUser {
    public String uid;
    public String username;
    public String biografia;
    public String sexualOrientation;

    public FriendlyUser() {
    }

    public FriendlyUser(String uid, String username, String biografia, String sexualOrientation) {
        this.uid = uid;
        this.username = username;
        this.biografia = biografia;
        this.sexualOrientation = sexualOrientation;
    }
}
