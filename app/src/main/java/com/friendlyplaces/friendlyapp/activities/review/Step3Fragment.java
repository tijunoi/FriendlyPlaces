package com.friendlyplaces.friendlyapp.activities.review;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.model.FriendlyPlace;
import com.friendlyplaces.friendlyapp.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.varunest.sparkbutton.SparkButton;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.friendlyplaces.friendlyapp.utilities.FirestoreConstants.COLLECTION_FRIENDLYPLACES;
import static com.friendlyplaces.friendlyapp.utilities.FirestoreConstants.COLLECTION_REVIEWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Step3Fragment extends Fragment implements BlockingStep {

    @BindView(R.id.previewReview_step3)
    TextView previewRev;
    @BindView(R.id.like_button_step3)
    SparkButton likeButton;
    @BindView(R.id.dislike_button_step3)
    SparkButton dislikeButton;
    @BindView(R.id.namePlace_step3)
    TextView namePlaceTv;

    AddReviewViewModel model;

    public Step3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step3, container, false);
        ButterKnife.bind(this, v);

        model = ViewModelProviders.of(getActivity()).get(AddReviewViewModel.class);

        if (model.getReview().getVote().getVoto() == 1) {
            likeButton.setChecked(true);
            likeButton.setVisibility(View.VISIBLE);
        } else {
            dislikeButton.setChecked(true);
            dislikeButton.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        callback.getStepperLayout().showProgress("Subiendo review...");

        final DocumentReference friendlyPlaceReference = FirebaseFirestore.getInstance().collection(COLLECTION_FRIENDLYPLACES).document(model.getPlaceId());

        final DocumentReference reviewReference = FirebaseFirestore.getInstance().collection(COLLECTION_REVIEWS).document();
        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                FriendlyPlace friendlyPlace = transaction.get(friendlyPlaceReference).toObject(FriendlyPlace.class);

                friendlyPlace.reviewCount += 1;

                if (model.getReview().getVote() == Review.Vote.POSITIVO) {
                    friendlyPlace.positiveVotes += 1;
                } else {
                    friendlyPlace.negativeVotes += 1;
                }

                model.getReview().setTimestamp(System.currentTimeMillis());

                transaction.set(friendlyPlaceReference, friendlyPlace);
                transaction.set(reviewReference, model.getReview());

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(getView(), R.string.review_thanks, Snackbar.LENGTH_LONG)
                            .setAction(R.string.back_snackbar, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goBackToDetailedActivity();
                                }
                            }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            goBackToDetailedActivity();
                        }
                    }).show();
                    callback.getStepperLayout().hideProgress();

                }
            }
        });
    }

    private void goBackToDetailedActivity() {
        getActivity().finish();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        namePlaceTv.setText(model.getPlaceName());
        previewRev.setText(model.getReview().getComment());
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
