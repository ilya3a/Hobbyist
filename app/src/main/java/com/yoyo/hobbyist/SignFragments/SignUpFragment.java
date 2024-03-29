package com.yoyo.hobbyist.SignFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoyo.hobbyist.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SignUpFragmentListener signUpFragmentListener;

    MaterialButton register_fragment_btn;
    TextInputLayout email_et_wraper;
    TextInputLayout password_et_wraper;
    TextInputLayout password_verify_et_wraper;
    // Get a reference to our posts
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("");

    public interface SignUpFragmentListener {
        void afterSignUpUserUpdate(FirebaseUser user);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            signUpFragmentListener = (SignUpFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement the interface : SignUpFragmentListener");
        }
    }


    public SignUpFragment(){
    }

    public static SignUpFragment newInstance(){
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.sign_up_fragment,container,false);

        email_et_wraper=rootView.findViewById(R.id.email_input_et_wraper);
        password_et_wraper=rootView.findViewById(R.id.password_input_et_wraper);
        password_verify_et_wraper=rootView.findViewById(R.id.password_verify_input_et_wraper);
        EditText username=rootView.findViewById(R.id.username_ET);
        EditText password1=rootView.findViewById(R.id.password_ET);
        EditText password2=rootView.findViewById(R.id.password_verify_ET);
        register_fragment_btn=rootView.findViewById(R.id.register_btn);
        firebaseAuth= FirebaseAuth.getInstance();

        View.OnTouchListener errorclear=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email_et_wraper.setErrorEnabled(false);
                password_et_wraper.setErrorEnabled(false);
                password_verify_et_wraper.setErrorEnabled(false);
                return false;
            }
        };

        username.setOnTouchListener(errorclear);
        password1.setOnTouchListener(errorclear);
        password2.setOnTouchListener(errorclear);



        register_fragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1=password_et_wraper.getEditText().getText().toString();
                String password2=password_verify_et_wraper.getEditText().getText().toString();
                String email=email_et_wraper.getEditText().getText().toString();
                if (password1.length()<6 || password2.length()<6)
                {
                    password_et_wraper.setError(getString(R.string.password_must_be_at_least_6));
                    password_verify_et_wraper.setError(getString(R.string.password_must_be_at_least_6));
                }
                else if (!(password1.equals(password2)))
                {
                    password_et_wraper.setError(getString(R.string.password_dont_match));
                }
                if (!isEmailValid(email))
                {
                    Snackbar.make(rootView, R.string.enter_a_valit_email, Snackbar.LENGTH_SHORT).show();
                    email_et_wraper.setError(getString(R.string.invalid_email));
                }
                if(password1.equals(password2) && (password1.length())>=6 && isEmailValid(email)) {
                    firebaseAuth.createUserWithEmailAndPassword(email_et_wraper.getEditText().getText().toString(), password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Snackbar.make(rootView, R.string.registerd_successfully, Snackbar.LENGTH_SHORT).show();
                                FirebaseUser user=firebaseAuth.getCurrentUser();
                                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName("null").build());
                                signUpFragmentListener.afterSignUpUserUpdate(user);
                                removePhoneKeypad(rootView);
                            }
                            else
                                Snackbar.make(rootView, R.string.registr_faild, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
