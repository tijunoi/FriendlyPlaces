package com.friendlyplaces.friendlyapp.activities.detailed_place.reviews

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.Review

/**
 * Created by Nil Ordoñez on 10/5/18.
 */
class ReviewsRecycleViewAdapter(private val dataSource: MutableList<Review>) : RecyclerView.Adapter<ReviewsRecycleViewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_list_item_layout, parent, false)
        //.inflate(R.layout.)
        return ReviewViewHolder(itemView = itemView)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}