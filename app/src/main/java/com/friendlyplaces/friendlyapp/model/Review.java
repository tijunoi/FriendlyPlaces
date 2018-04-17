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
    private Date date;

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


    public Review(String uid, String placeId, String comment, Vote vote, Date date) {
        this.uid = uid;
        this.placeId = placeId;
        this.comment = comment;
        mVote = vote;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
