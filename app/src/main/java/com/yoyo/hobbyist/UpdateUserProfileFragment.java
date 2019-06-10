package com.yoyo.hobbyist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Calendar;


public class UpdateUserProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    String mName,mLastName,mAge,mCityName,mGender;
    TextInputLayout mName_et_wrapper,mLastName_et_wrapper,mCityName_et_wrapper;
    EditText mName_et,mLastName_et,mCityName_et,mGender_et,mDateOfBirth_et;
    MaterialButton accept_btn;
    TextView gender_click,birth_day_click;
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView =inflater.inflate(R.layout.fragment_update_user_profile, container, false);

        mFireBaseAuth = FirebaseAuth.getInstance();

        mName_et_wrapper=rootView.findViewById(R.id.name_input_et_wrapper);
        mLastName_et_wrapper=rootView.findViewById(R.id.last_name_input_et_wrapper);
        mCityName_et_wrapper=rootView.findViewById(R.id.city_name_et_wrapper);

        mName_et=rootView.findViewById(R.id.name_input_et);
        mLastName_et=rootView.findViewById(R.id.last_name_input_et);
        mCityName_et=rootView.findViewById(R.id.city_name_et);
        mGender_et=rootView.findViewById(R.id.gender_et);
        mDateOfBirth_et=rootView.findViewById(R.id.date_of_birth_et);

        accept_btn=rootView.findViewById(R.id.accept_btn);

        birth_day_click=rootView.findViewById(R.id.fake_date_of_birth_clicker);
        gender_click=rootView.findViewById(R.id.fake_gender_clicker);
        mUser=mFireBaseAuth.getCurrentUser();

        final TextInputLayout[] allFields = { mName_et_wrapper,mLastName_et_wrapper,mCityName_et_wrapper};

        final String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
        final int itemSelected = 0;

        View.OnTouchListener errorCancel = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for(TextInputLayout edit : allFields)
                {
                    edit.setErrorEnabled(false);
                }
                return false;
            }
        };
        mName_et_wrapper.setOnTouchListener(errorCancel);
        mLastName_et_wrapper.setOnTouchListener(errorCancel);
        mCityName_et_wrapper.setOnTouchListener(errorCancel);
        birth_day_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(rootView.getContext(), UpdateUserProfileFragment.this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        gender_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        mGender_et.setText(mGender);
                    }
                })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        mDateOfBirth_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(rootView.getContext(), UpdateUserProfileFragment.this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean continue_flag=true;
                ArrayList<TextInputLayout> ErrorFields =new ArrayList<TextInputLayout>();//empty Edit text arraylist
                for(TextInputLayout edit : allFields){
                    if(TextUtils.isEmpty(edit.getEditText().getText().toString())){
                        // EditText was empty
                        continue_flag=false;
                        ErrorFields.add(edit);//add empty Edittext only in this ArayList
                        for(int i = ErrorFields.size()-1; i >=0; i--)
                        {
                            TextInputLayout currentField = ErrorFields.get(i);
                            currentField.setError(getResources().getString(R.string.this_field_required));
                            currentField.requestFocus();
                        }
                    }
                    if (continue_flag){
                        mName=mName_et_wrapper.getEditText().getText().toString();
                        mLastName=mLastName_et_wrapper.getEditText().getText().toString();

                        mUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mName+mLastName).build());
                    }
                }
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            String date=dayOfMonth+"/"+month+"/"+year;
            mDateOfBirth_et.setText(date);
            Integer tempAge;
            tempAge=(getAge(year,month,dayOfMonth));
            mAge=tempAge.toString();
    }


    // Returns age given the date of birth
    public static int getAge(int year, int month, int dayOfMonth)  {
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
