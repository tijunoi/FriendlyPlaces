package com.friendlyplaces.friendlyapp.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.transition.Transition
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.FriendlyUser
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.concurrent.thread

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        spin_kit_profile.visibility = View.VISIBLE
        setValuesToUser()
    }

    private fun setValuesToUser() {
        val name = profile_name.text.toString()
        var requiredConditions = true
        var shouldChangeName = false
        if (name.isNotBlank()) {
            friendlyUser.username = name
            shouldChangeName = true
        } else requiredConditions = false

        val biografia = profile_description.text.toString()
        if (biografia.isNotBlank()) {
            friendlyUser.biografia = biografia
        } else requiredConditions = false

        val sexOrientation = profile_sex_orientation.text.toString()
        if (sexOrientation.isNotBlank()) {
            friendlyUser.sexualOrientation = sexOrientation
        } else requiredConditions

        if (requiredConditions)
            postDataToFirebase(shouldChangeName = shouldChangeName)
        else Snackbar.make(profileSave_button, "Los campos no pueden estar vacios", Snackbar.LENGTH_LONG).show()
    }

    private fun postDataToFirebase(shouldChangeName: Boolean) {

        val user = friendlyUser

        thread(true) {
            val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(user.username)
                    .build()

            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileChangeRequest)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            usersCollection.document(FirebaseAuth.getInstance().currentUser!!.uid).set(user)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            runOnUiThread {
                                                spin_kit_profile.visibility = View.GONE
                                                Snackbar.make(profileSave_button, "Se ha guardado el perfil", Snackbar.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                        }
                    }
        }
    }


    lateinit var usersCollection: CollectionReference
    lateinit var friendlyUser: FriendlyUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initToolbar()
        getProfileData()
        profileSave_button.setOnClickListener(this)
    }

    private fun getProfileData() {
        usersCollection = FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_USERS)

        usersCollection.document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        friendlyUser = it.result.toObject(FriendlyUser::class.java)!!

                        profile_description.setText(friendlyUser.biografia)
                        profile_sex_orientation.setText(friendlyUser.sexualOrientation)
                        Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(imageProfile)
                        profile_name.setText(FirebaseAuth.getInstance().currentUser!!.displayName)
                    }
                }
    }

    private fun initWindow() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_profile)
        val transition: Transition = Slide(Gravity.START)
        transition.duration = 250
        window.enterTransition = transition
    }

    private fun initToolbar() {
        setSupportActionBar(appbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Editar perfil"
        //val drawable
        //supportActionBar?
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }


        return super.onOptionsItemSelected(item)
    }
}
