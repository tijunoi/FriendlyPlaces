package com.friendlyplaces.friendlyapp.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.friendlyplaces.friendlyapp.R;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private EditText et_email, et_password;
    private Button login_button, loginWithGoogleButton;

    // TODO: Rename and change types of parameters
    // private String mParam1;
    //private String mParam2;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        login_button = (Button) v.findViewById(R.id.bt_login_iniciar_sesion);
        login_button.setOnClickListener(this);

        loginWithGoogleButton = v.findViewById(R.id.bt_login_login_google);
        loginWithGoogleButton.setOnClickListener(this);

        et_email = (EditText) v.findViewById(R.id.et_login_email);
        et_password = (EditText) v.findViewById(R.id.et_login_password);

        //Inicialitzo TextWatchers per clear errors quan usuari escrigui bé
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        switch (v.getId()){
            case R.id.bt_login_iniciar_sesion:
                //todo: optimize, check conditions in a method

                boolean requiredConditions = true;
                if (et_password.getText().toString().equals("")) {
                    et_password.setError("La contraseña no puede estar vacía");
                    et_password.requestFocus();
                    requiredConditions = false;
                }

                if (!isEmailValid(et_email.getText().toString())) {
                    et_email.setError("Introduce un email válido");
                    et_email.requestFocus();
                    requiredConditions = false;
                }
                if (et_email.getText().toString().equals("")) {
                    et_email.setError("Introduce tu email");
                    et_email.requestFocus();
                    requiredConditions = false;
                }

                if (requiredConditions && mListener != null) {
                    mListener.onLoginInteraction(et_email.getText().toString(), et_password.getText().toString());
                }
                break;
            case R.id.bt_login_login_google:
                mListener.onLoginWithGoogleButtonPressed();
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
        void onLoginInteraction(String email, String password);

        void onLoginWithGoogleButtonPressed();
    }
}
