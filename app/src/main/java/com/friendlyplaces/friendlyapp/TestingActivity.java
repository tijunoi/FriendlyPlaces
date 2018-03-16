package com.friendlyplaces.friendlyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.friendlyplaces.friendlyapp.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class TestingActivity extends AppCompatActivity {
    public static final String REVIEW_COLLECTION = "Reviews";

    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mButton = findViewById(R.id.button2);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview();
            }
        });
    }

    private void postReview() {
        int rating = new Random().nextInt(((5 - 1) + 1) + 1);


        Review review = new Review(FirebaseAuth.getInstance().getCurrentUser().getUid(), "ChIJkemk1oGYpBIRctBUxnisr4o", rating, "Mola mugullo");

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //Aqui puja un objecte directament a Firebase. Aixo s'hauria de fer millor dins de un metode, ferho mes ordenat, i bla bla
        db.collection("Reviews").add(review).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "S'ha pujat la review. Chequea Firebase", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
