package com.friendlyplaces.friendlyapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView profileImage;
    private TextView nameProfile, descriptionProfile;
    private Spinner sexOriProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.imageProfile);
        nameProfile = findViewById(R.id.profile_name);
        descriptionProfile = findViewById(R.id.profile_description);
        sexOriProfile = findViewById(R.id.profile_sex_orientation);


        //emailDrawerTextview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        //Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profilePictureCircleImageView);
    }
}
