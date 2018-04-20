package com.friendlyplaces.friendlyapp.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.model.FriendlyUser;
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView profileImage;
    private TextView nameProfile, descriptionProfile, sexOriProfile;
    private CollectionReference usersCollection;
    private FriendlyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_profile);
        Slide slideTransition = new Slide(Gravity.START);
        slideTransition.setDuration(250);
        getWindow().setEnterTransition(slideTransition);

        android.support.v7.widget.Toolbar appbar = (android.support.v7.widget.Toolbar) findViewById(R.id.appbarProfile);
        setSupportActionBar(appbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar perfil");
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setHomeButtonEnabled(true);

        usersCollection = FirebaseFirestore.getInstance().collection(FirestoreConstants.COLLECTION_USERS);

        profileImage = findViewById(R.id.imageProfile);
        nameProfile = findViewById(R.id.profile_name);
        descriptionProfile = findViewById(R.id.profile_description);
        sexOriProfile = findViewById(R.id.profile_sex_orientation);


        usersCollection.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            user = task.getResult().toObject(FriendlyUser.class);
                            //afegir spinkit
                            descriptionProfile.setText(user.biografia);
                            sexOriProfile.setText(user.sexualOrientation);
                            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profileImage);
                            nameProfile.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        }
                    }
                }
        );


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishAfterTransition();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
