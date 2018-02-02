package com.friendlyplaces.friendlyapp.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.friendlyplaces.friendlyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpFragment.OnSignUpFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private EditText inputEmail, inputPassword, inputUsername;
    private Button btSignUp, btResetPass;
    private String email, password;

    //Fancy button vars
    FrameLayout registerButtonFrame;
    TextView registerFramebuttonTextview;
    ProgressBar mProgressBar;
    View reveal;

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    private OnSignUpFragmentInteractionListener mListener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // TODO: HEM DE CREAR UN CAMP USERNAME, OSEA LLENARLO AQUI QUE YATA EN EL LAYOUT
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        inputEmail = (EditText) v.findViewById(R.id.et_register_email);
        inputPassword = (EditText) v.findViewById(R.id.et_register_password);
        reveal = v.findViewById(R.id.reveal_register_view);

        registerButtonFrame = v.findViewById(R.id.frame_button_register);
        registerFramebuttonTextview = v.findViewById(R.id.register_frame_textview);
        mProgressBar = v.findViewById(R.id.register_progressbar);
        registerButtonFrame.setOnClickListener(this);
        reveal = v.findViewById(R.id.reveal_login_view);
        //inputUsername = (EditText)
        // Button btSignUp = (Button) v.findViewById(R.id.bt_register_crear_cuenta);
        //btSignUp.setOnClickListener(this);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpFragmentInteractionListener) {
            mListener = (OnSignUpFragmentInteractionListener) context;
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
        switch (v.getId()){
            case R.id.frame_button_register:
                if (mListener != null){
                    email = inputEmail.getText().toString().trim();
                    password = inputPassword.getText().toString().trim();

                    boolean requiredConditions = true;
                    if (TextUtils.isEmpty(email)){
                        requiredConditions = false;
                        inputEmail.setError("Debes introducir un email");
                    }
                    if (TextUtils.isEmpty(password)){
                        requiredConditions = false;
                        inputPassword.setError("Debes introducir una contraseña");
                    }
                    if (password.length() < 6){
                        requiredConditions = false;
                        inputPassword.setError("La contraseña debe contener más de 6 caracteres");
                    }

                    if (requiredConditions && mListener != null) {
                        OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    reveal.setVisibility(View.VISIBLE);
                                    int cx = reveal.getWidth();
                                    int cy = reveal.getHeight();

                                    int x = (int) (getFabWidth() / 2 + registerButtonFrame.getX());
                                    int y = (int) (getFabWidth() / 2 + registerButtonFrame.getY());

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
                                }
                            }
                        };

                        mListener.onRegisterInteraction(email, password, onCompleteListener);

                        ValueAnimator anim = ValueAnimator.ofInt(registerButtonFrame.getMeasuredWidth(), getFabWidth());

                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = registerButtonFrame.getLayoutParams();
                                layoutParams.width = val;
                                registerButtonFrame.requestLayout();
                                registerButtonFrame.setLayoutParams(layoutParams);
                            }
                        });

                        anim.setDuration(250);
                        anim.start();

                        registerFramebuttonTextview.animate()
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


                }
                inputEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (email.length() <= 0){
                            inputEmail.setError("Debes introducir un email.");
                        }else{
                            inputEmail.setError(null);
                        }
                    }
                });
                inputPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (password.isEmpty()){
                            inputPassword.setError("Debes introducir una contraseña");
                        }else if(password.length() < 6){
                            inputPassword.setError("La contraseña debe contener más de 6 caracteres");
                        }else{
                            inputPassword.setError(null);
                        }
                    }
                });
                break;
        }
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
    public interface OnSignUpFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRegisterInteraction(String email, String password, OnCompleteListener<AuthResult> onCompleteListener);
    }

    public int getFabWidth() {
        return (int) getResources().getDimension(R.dimen.loadingButtonAuth);
    }
}
