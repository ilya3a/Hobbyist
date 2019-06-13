package com.yoyo.hobbyist.SignFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;
import java.util.Calendar;


public class UpdateUserProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    UpdateUserProfileFragmentListener updateUserProfileFragmentListener;
    public interface UpdateUserProfileFragmentListener{
        void afterUpdateUserUpdate(Boolean isUserUpdated);
    }
    String mName, mLastName, mAge, mCityName, mGender;
    TextInputLayout mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper, mDateOfBirthEtWrapper,mGenderEtWrapper;
    EditText mName_et, mLastNameEt, mCityNameEt, mGenderEt, mDateOfBirthEt;
    MaterialButton accept_btn;
    TextView gender_click, birth_day_click;
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private long lastTouchDown;
    private static int CLICK_ACTION_THRESHHOLD = 200;
    Boolean removeErrors_flag=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_user_profile, container, false);

        mFireBaseAuth = FirebaseAuth.getInstance();

        mNameEtWrapper = rootView.findViewById(R.id.name_input_et_wrapper);
        mLastNameEtWrapper = rootView.findViewById(R.id.last_name_input_et_wrapper);
        mCityNameEtWrapper = rootView.findViewById(R.id.city_name_et_wrapper);
        mGenderEtWrapper=rootView.findViewById(R.id.gender_et_wrapper);
        mDateOfBirthEtWrapper =rootView.findViewById(R.id.date_of_birth_et_wrapper);

        mName_et = rootView.findViewById(R.id.name_input_et);
        mLastNameEt = rootView.findViewById(R.id.last_name_input_et);
        mCityNameEt = rootView.findViewById(R.id.city_name_et);
        mGenderEt = rootView.findViewById(R.id.gender_et);
        mDateOfBirthEt = rootView.findViewById(R.id.date_of_birth_et);

        accept_btn = rootView.findViewById(R.id.accept_btn);

        mFirebaseUser = mFireBaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        final String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
        final TextInputLayout[] allFields = {mGenderEtWrapper,mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper, mDateOfBirthEtWrapper};
        final int itemSelected=-1 ;

        View.OnTouchListener errorCancel = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (TextInputLayout edit : allFields) {
                    edit.setErrorEnabled(false);
                }
                return false;
            }
        };
        final DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDateOfBirthEtWrapper.clearFocus();
                mDateOfBirthEt.clearFocus();
                if (removeErrors_flag)
                {
                   clearErrors(allFields);
                }
            }
        };
        mNameEtWrapper.setOnTouchListener(errorCancel);
        mLastNameEtWrapper.setOnTouchListener(errorCancel);
        mCityNameEtWrapper.setOnTouchListener(errorCancel);
        mDateOfBirthEtWrapper.setOnTouchListener(errorCancel);
        mGenderEtWrapper.setOnTouchListener(errorCancel);
        mDateOfBirthEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchDown = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {
                            Log.w("App", "You clicked!");
                            DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), UpdateUserProfileFragment.this,
                                    Calendar.getInstance().get(Calendar.YEAR),
                                    Calendar.getInstance().get(Calendar.MONTH),
                                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.setOnDismissListener(onDismissListener);
                            datePickerDialog.show();
                        //}
                        break;

                }
                if (removeErrors_flag)
                {
                    clearErrors(allFields);
                }
                return false;
            }
        });
        mGenderEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        lastTouchDown = System.currentTimeMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        new AlertDialog.Builder(rootView.getContext())
                                .setTitle("Select your gender")
                                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                        mGender = singleChoiceItems[selectedIndex];
                                    }
                                })
                                .setPositiveButton("Ok", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mGenderEtWrapper.getEditText().setText(mGender);
                                mGenderEtWrapper.clearFocus();
                                mGenderEt.clearFocus();
                            }
                        })
                                .setNegativeButton("Cancel", null)
                                .show();
                                break;
                    }
                }
                if (removeErrors_flag)
                {
                    clearErrors(allFields);
                }
                return false;
            }
        });
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean continue_flag = true;
                ArrayList<TextInputLayout> ErrorFields = new ArrayList<>();
                for (TextInputLayout edit : allFields) {
                    if (TextUtils.isEmpty(edit.getEditText().getText().toString())) {
                        // EditText was empty
                        continue_flag = false;
                        removeErrors_flag=true;
                        ErrorFields.add(edit);//add empty Edittext only in this ArayList
                        for (int i = ErrorFields.size() - 1; i >= 0; i--) {
                            TextInputLayout currentField = ErrorFields.get(i);
                            currentField.setError(getResources().getString(R.string.this_field_required));
                            currentField.requestFocus();
                        }
                    }
                    if (continue_flag) {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setmName(mNameEtWrapper.getEditText().getText().toString())
                                .setmCityName(mCityNameEtWrapper.getEditText().getText().toString())
                                .setmLastName(mLastNameEtWrapper.getEditText().getText().toString())
                                .setmAge(mAge).setmGender(mGenderEtWrapper.getEditText().getText().toString()).setmUserToken(mFirebaseUser.getUid());
                        mDatabaseReference.child("appUsers").child(userProfile.getmUserToken()).setValue(userProfile);
                        mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mNameEtWrapper.getEditText().getText().toString() + mLastNameEtWrapper.getEditText().getText().toString()).build());
                        updateUserProfileFragmentListener.afterUpdateUserUpdate(true);
                    }
                }
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateUserProfileFragmentListener = (UpdateUserProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement the interface : updateUserProfileFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        mDateOfBirthEtWrapper.getEditText().setText(date);
        Integer tempAge;
        tempAge = (getAge(year, month, dayOfMonth));
        mAge = tempAge.toString();
        //mDateOfBirthEtWrapper.clearFocus();
        //mDateOfBirthEt.clearFocus();
    }


    // Returns age given the date of birth
    public static Integer getAge(int year, int month, int dayOfMonth) {
        Calendar today = Calendar.getInstance();

        int curYear = today.get(Calendar.YEAR);
        int dobYear = year;

        int age = curYear - dobYear;

        // if dob is month or day is behind today's month or day
        // reduce age by 1
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = month;
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dayOfMonth;
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }
        return age;
    }
    void clearErrors (TextInputLayout[] allFields){
        removeErrors_flag=false;
        for (TextInputLayout edit : allFields) {
            edit.setErrorEnabled(false);
        }
    }
//    public int calculateAge(
//////            LocalDate birthDate,
//////            LocalDate currentDate) {
//////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//////            return Period.between(birthDate, currentDate).getYears();
//////        }
//////        else
//////            return 2019-birthDate.getYear();
//////    }
}
