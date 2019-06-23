package com.yoyo.hobbyist.SignFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yoyo.hobbyist.R;

import static android.content.Context.MODE_PRIVATE;

public class NewSignInScreenFragment extends Fragment {
    FirebaseAuth mFireBaseAuth;
    FragmentManager mFragmentManager;
    LoginFragmentListener mListener;
    SharedPreferences mSp;
    SharedPreferences.Editor mSpEditor;
    TextInputLayout email_et_wraper,password_et_wraper;
    TextView mSignUpTv;
    String mEmail;
    String mPassword;


    public interface LoginFragmentListener {
        void afterSignInUserUpdate(FirebaseUser user);
        void callSignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_main1, container, false);
        mSignUpTv=rootView.findViewById(R.id.signup_tv);

        mFireBaseAuth = FirebaseAuth.getInstance();
        email_et_wraper = rootView.findViewById(R.id.email_input_et_wraper);
        password_et_wraper = rootView.findViewById(R.id.password_input_et_wraper);
        Button login_fragment_btn = rootView.findViewById(R.id.login_fragment_btn);
        mFragmentManager =getFragmentManager();
        mSp = getContext().getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        mSpEditor = mSp.edit();
        mEmail = mSp.getString("userEmail", "noEmail");
        mPassword = mSp.getString("userPassword", "noPassword");
        if (!mEmail.equals("noEmail") && !mPassword.equals("noPassword")) {
            email_et_wraper.getEditText().setText(mEmail);
            password_et_wraper.getEditText().setText(mPassword);
        }
        login_fragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.clearFocus();
                final String useremail = email_et_wraper.getEditText().getText().toString();
                final String password = password_et_wraper.getEditText().getText().toString();
                if (useremail.equals("") || password.equals("")) {
                    Snackbar.make(rootView, "Email or Password are incorrect", Snackbar.LENGTH_SHORT);
                } else {
                    mFireBaseAuth.signInWithEmailAndPassword(useremail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(rootView, "you are now lobed in", Snackbar.LENGTH_SHORT);
                                FirebaseUser user= mFireBaseAuth.getCurrentUser();
                                mListener.afterSignInUserUpdate(user);
                                mSpEditor.putString("userEmail",useremail).commit();
                                mSpEditor.putString("userPassword",password).commit();
                                removePhoneKeypad(rootView);

                            } else {
                                Snackbar.make(rootView, "Email or Password are incorrect", Snackbar.LENGTH_SHORT);
                            }
                        }
                    });
                }

            }
        });
        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callSignUp();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LoginFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement the interface : loginFragmentListener");
        }
    }
    public void removePhoneKeypad(View view) {
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
