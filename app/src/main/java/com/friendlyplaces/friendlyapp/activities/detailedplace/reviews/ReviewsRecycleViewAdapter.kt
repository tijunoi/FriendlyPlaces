package com.friendlyplaces.friendlyapp.activities.detailedplace.reviews

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.Review
import com.friendlyplaces.friendlyapp.model.ReviewWithUser
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkButton
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Nil Ordoñez on 10/5/18.
 */
class ReviewsRecycleViewAdapter(private val dataSource: MutableList<ReviewWithUser>) : RecyclerView.Adapter<ReviewsRecycleViewAdapter.ReviewViewHolder>() {


    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvReview: TextView = itemView.findViewById(R.id.reviewPlace_recycler)
        var tvNameUser: TextView = itemView.findViewById(R.id.userName_recycler)
        var ivProfileImage: CircleImageView = itemView.findViewById(R.id.profileImage_recycler)
        var likeButton: SparkButton = itemView.findViewById(R.id.like_recycler)
        var dislikeButton: SparkButton = itemView.findViewById(R.id.dislike_recycler)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_list_item_layout, parent, false)

        return ReviewViewHolder(itemView = itemView)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = dataSource[position]
        holder.tvReview.text = item.review.comment
        holder.tvNameUser.text = item.friendlyUser.username
        holder.likeButton.isEnabled = false
        holder.dislikeButton.isEnabled = false
        FirebaseStorage.getInstance().getReference("profilePictures/${item.friendlyUser.uid}.jpg").downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Picasso.get().load(it.result)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.ivProfileImage)
            }
        }
        when (item.review.vote!!) {
            Review.Vote.POSITIVO -> {
                holder.likeButton.visibility = View.VISIBLE
                holder.dislikeButton.visibility = View.GONE
            }
            Review.Vote.NEGATIVO -> {
                holder.likeButton.visibility = View.GONE
                holder.dislikeButton.visibility = View.VISIBLE
            }
        }
    }


}