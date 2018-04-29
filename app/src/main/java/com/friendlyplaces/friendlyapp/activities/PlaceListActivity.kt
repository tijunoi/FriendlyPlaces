package com.friendlyplaces.friendlyapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.activities.detailed_place.DetailedPlaceActivity
import com.friendlyplaces.friendlyapp.adapters.PlacesAdapter
import com.friendlyplaces.friendlyapp.model.FriendlyPlace
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {

    companion object {
        const val QUERY_TYPE_KEY = "QueryType"
        const val POSITIVE_PLACES = 0
        const val NEGATIVE_PLACES = 1
        const val OWN_VOTED_PLACES = 2
    }

    lateinit var places: MutableList<FriendlyPlace>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_list)

        setSupportActionBar(place_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        places = mutableListOf()

        place_listview.onItemClickListener = AdapterView.OnItemClickListener({ _, _, position, _ ->
            val intent = Intent(this, DetailedPlaceActivity::class.java)
            intent.putExtra("placeId", places[position].pid)
            intent.putExtra("placeName", places[position].name)
            startActivity(intent)
        })
    }

    override fun onResume() {
        super.onResume()
        FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_FRIENDLYPLACES).addSnapshotListener { snapshots, e ->
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

        place_listview.adapter = PlacesAdapter(this, places)
    }
}
