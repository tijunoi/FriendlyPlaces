package com.friendlyplaces.friendlyapp.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth


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
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
