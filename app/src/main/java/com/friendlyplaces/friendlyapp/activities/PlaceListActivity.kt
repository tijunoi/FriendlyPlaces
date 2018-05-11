package com.friendlyplaces.friendlyapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.activities.detailed_place.DetailedPlaceActivity
import com.friendlyplaces.friendlyapp.adapters.PlacesAdapter
import com.friendlyplaces.friendlyapp.model.FriendlyPlace
import com.friendlyplaces.friendlyapp.model.Review
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_place_list.*
import kotlin.concurrent.thread

class PlaceListActivity : AppCompatActivity() {

    companion object {
        const val QUERY_TYPE_KEY = "QueryType"
        const val TITLE_KEY = "title_key"
        const val POSITIVE_PLACES = 0
        const val NEGATIVE_PLACES = 1
        const val OWN_VOTED_PLACES = 2
    }

    lateinit var places: MutableList<FriendlyPlace>
    var queryType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_list)

        queryType = intent.getIntExtra(QUERY_TYPE_KEY, 0)

        setupToolbar()

        places = mutableListOf()

        place_listview.onItemClickListener = AdapterView.OnItemClickListener({ _, _, position, _ ->
            val intent = Intent(this, DetailedPlaceActivity::class.java)
            intent.putExtra("placeId", places[position].pid)
            intent.putExtra("placeName", places[position].name)
            startActivity(intent)
        })

        startLoadingData()
    }

    private fun setupToolbar() {
        val titleName = intent.getStringExtra(TITLE_KEY)

        setSupportActionBar(place_list_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = titleName
        }
    }

    private fun startLoadingData() {
        if (queryType == OWN_VOTED_PLACES) {
            runBackgroundQuery()
        } else {
            val query = getQuery()
            query?.addSnapshotListener { snapshots, e ->
                places.clear()
                if (e != null) {
                    Log.w("PlaceListActivity", "listen:error", e)
                } else {
                    for (doc in snapshots!!) {
                        val fplace = doc.toObject(FriendlyPlace::class.java)
                        places.add(fplace)
                    }
                }
                (place_listview.adapter as PlacesAdapter).notifyDataSetChanged()
            }

        }
        place_listview.adapter = PlacesAdapter(this, places)
    }

    private fun runBackgroundQuery() {
        thread(true) {
            FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_REVIEWS)
                    .whereEqualTo("uid", FirebaseAuth.getInstance().currentUser!!.uid)
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val reviews = mutableListOf<Review>()
                            for (doc in it.result) {
                                val review = doc.toObject(Review::class.java)
                                reviews.add(review)
                            }
                            val uniqueReviews = reviews.distinctBy { it -> it.placeId }

                            uniqueReviews.forEach {
                                queryPlace(it.placeId)
                            }


                        }
                    }

        }
    }

    private fun queryPlace(placeId: String?) {

        FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES).document(placeId!!).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val place = it.result.toObject(FriendlyPlace::class.java)!!
                        runOnUiThread {
                            places.add(place)
                            (place_listview.adapter as PlacesAdapter).notifyDataSetChanged()
                        }
                    }
                }

    }

    private fun getQuery(): Query? {
        when (queryType) {
            POSITIVE_PLACES -> {
                return FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES).whereGreaterThanOrEqualTo("puntuation", 0).orderBy("puntuation", Query.Direction.DESCENDING)
            }
            NEGATIVE_PLACES -> {
                return FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES).whereLessThan("puntuation", 0).orderBy("puntuation", Query.Direction.ASCENDING)
            }
        }
        return null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)

    }
}
