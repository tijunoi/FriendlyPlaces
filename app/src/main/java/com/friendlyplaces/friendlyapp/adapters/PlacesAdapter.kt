package com.friendlyplaces.friendlyapp.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.FriendlyPlace
import com.varunest.sparkbutton.SparkButton

/**
 * Created by Nil Ordoñez on 26/4/18.
 */
class PlacesAdapter(context: Context,
                    private val dataSource: MutableList<FriendlyPlace>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val friendlyPlace: FriendlyPlace = dataSource[position]


        val miView = inflater.inflate(R.layout.rated_list_recycler_layout, parent, false)

        val cardView: CardView = miView.findViewById(R.id.place_cardview)
        val likeButton: SparkButton = miView.findViewById(R.id.like_recycler)
        val dislikeButton: SparkButton = miView.findViewById(R.id.dislike_recycler)
        val votes: TextView = miView.findViewById(R.id.votes_recycler)
        val namePlace: TextView = miView.findViewById(R.id.namePlace_recycler)

        likeButton.isEnabled = false
        dislikeButton.isEnabled = false
        //Visibility is INVISIBLE instead of GONE because textview constrain is attached to the like button
        if (friendlyPlace.negativeVotes <= friendlyPlace.positiveVotes) {
            likeButton.visibility = View.VISIBLE
            dislikeButton.visibility = View.INVISIBLE
        } else {
            likeButton.visibility = View.INVISIBLE
            dislikeButton.visibility = View.VISIBLE
        }

        val totalVotes = friendlyPlace.positiveVotes - friendlyPlace.negativeVotes
        votes.text = "$totalVotes"
        namePlace.text = friendlyPlace.name
        return miView
    }

    override fun getItem(position: Int): Any = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = dataSource.size
}