package com.friendlyplaces.friendlyapp.model;

/**
 * Created by Bertiwi on 28/03/2018.
 */

public class FriendlyUser {
    public String username;
    public String biografia;
    public String sexualOrientation;
    public String photo;

    public FriendlyUser() {
    }

    public FriendlyUser(String username, String biografia, String sexualOrientation, String photo) {
        this.username = username;
        this.biografia = biografia;
        this.sexualOrientation = sexualOrientation;
        this.photo = photo;
    }

}
