package com.friendlyplaces.friendlyapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.activities.detailedplace.DetailedPlaceActivity
import com.friendlyplaces.friendlyapp.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


/**
 * This class is the main entry point to the app. It decides where should it redirect the user based on the providence.
 * If it's the first run, show AppIntro
 * If it's a DeepLink, show the deeplink.
 * If the user is not logged in, AuthenticationActivity
 * If the user is logged in, take it to Home.
 */
class LaunchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchscreen)

        val splash_timeout = 1500
        Handler().postDelayed({
            val thread = Thread {
                val firstRun = checkIfItsFirstRun()
                if (firstRun) {
                    handleFirstRun()
                } else {
                    handleDeepLinking()
                    handleNormalRun()
                }
            }

            thread.start()
        }, splash_timeout.toLong())


    }

    private fun checkIfItsFirstRun(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        return sharedPreferences.getBoolean("firstStart", true)
    }

    private fun handleFirstRun() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()

        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
    }

    private fun handleNormalRun() {
        val intent: Intent = if (FirebaseAuth.getInstance().currentUser != null) Intent(this, MainActivity::class.java)
        else Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleDeepLinking() {
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this) { pendingDynamicLinkData ->
                        if (pendingDynamicLinkData != null) {
                            val deepLink: Uri = pendingDynamicLinkData.link
                            val placeId = deepLink.lastPathSegment
                            val intent = Intent(this, DetailedPlaceActivity::class.java)
                            intent.putExtra("placeId", placeId)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener(this) { e -> Log.w("LaunchActivity", "getDynamicLink:onFailure", e) }
        }
    }
}
