package com.friendlyplaces.friendlyapp.model;

/**
 * Created by Nil Ordoñez on 8/3/18.
 */

import java.util.Date;

/**
 * Clase que guarda els datos de una review. Model no definitiu.
 * De moment guardo ejemplo perque ho vegis
 */
public class Review {

    private String uid;
    private String placeId;
    private String comment;
    private Vote mVote;
    private long timestamp;

    public enum Vote {
        POSITIVO(1), NEGATIVO(-1);

        int voto;

        Vote(int voto) {
            this.voto = voto;
        }

        public int getVoto() {
            return voto;
        }
    }


    public Review() {
    }

    public Review(String uid, String placeId, String comment, Vote vote, long timestamp) {
        this.uid = uid;
        this.placeId = placeId;
        this.comment = comment;
        mVote = vote;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Vote getVote() {
        return mVote;
    }

    public void setVote(Vote vote) {
        this.mVote = vote;
    }
}
