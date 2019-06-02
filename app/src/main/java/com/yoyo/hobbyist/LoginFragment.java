package com.yoyo.hobbyist;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    FirebaseAuth mFireBaseAuth;
    FragmentManager mFragmentManager;
    LoginFragmentListener mListener;

    interface LoginFragmentListener {
        void userUpdate(FirebaseUser user);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        mFireBaseAuth = FirebaseAuth.getInstance();
        final TextInputLayout username_et_wraper = rootView.findViewById(R.id.email_input_et_wraper);
        final TextInputLayout password_et_wraper = rootView.findViewById(R.id.password_input_et_wraper);
        Button login_fragment_btn = rootView.findViewById(R.id.login_fragment_btn);
        mFragmentManager =getFragmentManager();

        login_fragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.clearFocus();
                String username = username_et_wraper.getEditText().getText().toString();
                String password = password_et_wraper.getEditText().getText().toString();
                if (username.equals("") || password.equals("")) {
                    Snackbar.make(rootView, "Email or Password are incorrect", Snackbar.LENGTH_SHORT);
                } else {
                    mFireBaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(rootView, "you are now lobed in", Snackbar.LENGTH_SHORT);
                                FirebaseUser user= mFireBaseAuth.getCurrentUser();
                                mListener.userUpdate(user);
                                removePhoneKeypad(rootView);

                            } else {
                                Snackbar.make(rootView, "Email or Password are incorrect", Snackbar.LENGTH_SHORT);
                            }
                        }
                    });
                }

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
