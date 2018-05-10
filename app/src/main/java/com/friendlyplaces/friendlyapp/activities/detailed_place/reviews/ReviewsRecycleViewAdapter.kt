package com.friendlyplaces.friendlyapp.activities.detailed_place.reviews

import android.opengl.Visibility
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.TextureView
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
        var tvReview = itemView.findViewById<TextView>(R.id.reviewPlace_recycler)
        var tvNameUser = itemView.findViewById<TextView>(R.id.userName_recycler)
        var ivProfileImage = itemView.findViewById<CircleImageView>(R.id.profileImage_recycler)
        var likeButton = itemView.findViewById<SparkButton>(R.id.like_recycler)
        var dislikeButton = itemView.findViewById<SparkButton>(R.id.dislike_recycler)

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
        holder.tvReview.setText(item.review.comment)
        holder.tvNameUser.setText(item.friendlyUser.username)
        holder.likeButton.isEnabled = false
        holder.dislikeButton.isEnabled = false
        FirebaseStorage.getInstance().getReference("profilePictures/${item.friendlyUser.uid}.jpg").downloadUrl.addOnCompleteListener{
            if (it.isSuccessful){
                Picasso.get().load(it.result).into(holder.ivProfileImage)
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