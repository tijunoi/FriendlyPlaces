package com.friendlyplaces.friendlyapp.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.friendlyplaces.friendlyapp.utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private EditText et_email, et_password;
    private Button login_button, loginWithGoogleButton;

    FrameLayout loginButtonFrame;
    TextView loginFramebuttonTextview;
    ProgressBar mProgressBar;
    View reveal;


    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        loginWithGoogleButton = v.findViewById(R.id.bt_login_login_google);
        loginWithGoogleButton.setOnClickListener(this);

        et_email = v.findViewById(R.id.et_login_email);
        et_password = v.findViewById(R.id.et_login_password);

        loginButtonFrame = v.findViewById(R.id.frame_button_login_login);
        loginFramebuttonTextview = v.findViewById(R.id.login_frame_textview);
        mProgressBar = v.findViewById(R.id.login_progressbar);
        loginButtonFrame.setOnClickListener(this);
        reveal = v.findViewById(R.id.reveal_login_view);

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid(s.toString())) et_email.setError(null);
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) et_password.setError(null);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.bt_login_login_google:
                mListener.onLoginWithGoogleButtonPressed();
                break;
            case R.id.frame_button_login_login:
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                boolean requiredConditions2 = true;
                if (et_password.getText().toString().trim().isEmpty()) {
                    et_password.setError("La contraseña no puede estar vacía");
                    et_password.requestFocus();
                    requiredConditions2 = false;
                }

                if (!isEmailValid(et_email.getText().toString().trim())) {
                    et_email.setError("Introduce un email válido");
                    et_email.requestFocus();
                    requiredConditions2 = false;
                }
                if (et_email.getText().toString().trim().isEmpty()) {
                    et_email.setError("Introduce tu email");
                    et_email.requestFocus();
                    requiredConditions2 = false;
                }


                if (requiredConditions2 && mListener != null) {

                    OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                reveal.setVisibility(View.VISIBLE);
                                int cx = reveal.getWidth();
                                int cy = reveal.getHeight();

                                int x = (int) (getFabWidth() / 2 + loginButtonFrame.getX());
                                int y = (int) (getFabWidth() / 2 + loginButtonFrame.getY());

                                float finalradius = Math.max(cx, cy) * 1.2f;

                                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(reveal, x, y, getFabWidth(), finalradius);
                                revealAnimator.setDuration(250);
                                revealAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
                                    }
                                });
                                revealAnimator.start();
                            } else {

                                ViewGroup parent = (ViewGroup) loginButtonFrame.getParent();
                                Snackbar.make(parent, R.string.user_password_failed, Snackbar.LENGTH_LONG).show();
                                int index = parent.indexOfChild(loginButtonFrame);
                                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.fragment_login, null, false); //Supress lint because this is the desired behaviour
                                View nouButoLogin = v.findViewById(R.id.frame_button_login_login);
                                parent.removeView(loginButtonFrame);
                                loginButtonFrame = (FrameLayout) nouButoLogin;
                                ((ViewGroup) loginButtonFrame.getParent()).removeView(loginButtonFrame);
                                parent.addView(loginButtonFrame, index);
                                mProgressBar = loginButtonFrame.findViewById(R.id.login_progressbar);
                                loginFramebuttonTextview = loginButtonFrame.findViewById(R.id.login_frame_textview);
                                loginButtonFrame.setOnClickListener(LoginFragment.this);

                            }

                        }
                    };
                    mListener.onLoginInteraction(et_email.getText().toString().trim(), et_password.getText().toString().trim(), onCompleteListener);

                    ValueAnimator anim = ValueAnimator.ofInt(loginButtonFrame.getMeasuredWidth(), getFabWidth());

                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = loginButtonFrame.getLayoutParams();
                            layoutParams.width = val;
                            loginButtonFrame.requestLayout();
                            loginButtonFrame.setLayoutParams(layoutParams);
                        }
                    });

                    anim.setDuration(250);
                    anim.start();
                    loginFramebuttonTextview.animate()
                            .alpha(0f)
                            .setDuration(250)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                                    mProgressBar.setVisibility(View.VISIBLE);
                                }
                            })
                            .start();
                }


                break;
        }

    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragmentInteractionListener {
        void onLoginInteraction(String email, String password, OnCompleteListener<AuthResult> onCompleteListener);

        void onLoginWithGoogleButtonPressed();
    }

    public int getFabWidth() {
        return (int) getResources().getDimension(R.dimen.loadingButtonAuth);
    }
}
