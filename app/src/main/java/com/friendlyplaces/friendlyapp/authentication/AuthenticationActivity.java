package com.friendlyplaces.friendlyapp.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.activities.JoinActivity;
import com.friendlyplaces.friendlyapp.activities.MainActivity;
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticationActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener, SignUpFragment.OnSignUpFragmentInteractionListener {

    public static final int RC_GOOGLE_SIGN_IN = 3736;
    private FirebaseAuth mAuth;
    private SpinKitView spinKit;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_google_id_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        spinKit = findViewById(R.id.spin_kit_authentication);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void onLoginInteraction(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfUserHasAlreadyCompletedProfile();
                }
            }
        }).addOnCompleteListener(onCompleteListener);
    }

    @Override
    public void onLoginWithGoogleButtonPressed() {
        @SuppressLint("RestrictedApi") Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        spinKit.setVisibility(View.VISIBLE);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_GOOGLE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {

                }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("FBaseGoogleLogin", "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkIfUserHasAlreadyCompletedProfile();

                        } else {
                            Log.w("FBaseGoogleLoginError", "signInWithCredential:failure", task.getException());
                            spinKit.setVisibility(View.GONE);
                            Snackbar.make(findViewById(R.id.main_content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onRegisterInteraction(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        final Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void checkIfUserHasAlreadyCompletedProfile() {

        FirebaseFirestore.getInstance()
                .collection(FirestoreConstants.COLLECTION_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                              if (task.isSuccessful()) {
                                                  if (task.getResult().exists()) {
                                                      spinKit.setVisibility(View.GONE);
                                                      sendToHomescreen();
                                                  } else {
                                                      spinKit.setVisibility(View.GONE);
                                                      sendToJoinActivity();
                                                  }
                                              }
                                          }
                                      }
                );
    }

    private void sendToHomescreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToJoinActivity() {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return LoginFragment.newInstance();
                case 1:
                    return SignUpFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
