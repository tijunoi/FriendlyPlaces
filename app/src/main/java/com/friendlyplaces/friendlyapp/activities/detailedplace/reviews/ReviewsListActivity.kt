package com.friendlyplaces.friendlyapp.activities.detailedplace.reviews

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.FriendlyUser
import com.friendlyplaces.friendlyapp.model.Review
import com.friendlyplaces.friendlyapp.model.ReviewWithUser
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reviews_list.*
import kotlin.concurrent.thread


const val PLACE_ID_KEY = "PLEISAIDI"
const val PLACE_NAME_KEY = "PLACE_NAME"

class
ReviewsListActivity : AppCompatActivity() {

    lateinit var dataSource: MutableList<ReviewWithUser>
    val reviews = mutableListOf<Review>()
    lateinit var placeId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews_list)
        setupToolbar()
        dataSource = mutableListOf()

        activity_reviews_list_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activity_reviews_list_recyclerview.adapter = ReviewsRecycleViewAdapter(dataSource)

        placeId = intent.getStringExtra(PLACE_ID_KEY)

        runBackgroundQuery()
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_reviews_list_toolbar)

        val name = intent.getStringExtra(PLACE_NAME_KEY)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.reviews_list_title).format(name)
        }
    }


    private fun runBackgroundQuery() {
        thread(true) {
            FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_REVIEWS)
                    .whereEqualTo("placeId", placeId)
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (doc in it.result) {
                                val review = doc.toObject(Review::class.java)
                                reviews.add(review)
                            }
                            reviews.forEach {
                                val rev = it
                                FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_USERS).document(it.uid).get().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val user = it.result.toObject(FriendlyUser::class.java)
                                        val reviewWithUser = ReviewWithUser(user!!, rev)
                                        dataSource.add(reviewWithUser)
                                        runOnUiThread {
                                            activity_reviews_list_recyclerview.adapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }


                        }
                    }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}
