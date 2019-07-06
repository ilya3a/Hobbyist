package com.yoyo.hobbyist.SignFragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.io.File;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;


public class UpdateUserProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    UpdateUserProfileFragmentListener updateUserProfileFragmentListener;

    public interface UpdateUserProfileFragmentListener {
        void afterUpdateUserUpdate(Boolean isUserUpdated, String[] hobbies);

        void updateImage(Intent intent, View view);
    }

    public void updateUserImage(final String filePath) {
        final StorageReference mStorageRef;
        final Date currentTime = Calendar.getInstance().getTime();
        mStorageRef = FirebaseStorage.getInstance().getReference("images/" + currentTime + ".jpg");
        isPhotoExists = true;
        //Uri uri = Uri.fromFile(mFile);
        Uri uri = Uri.fromFile(new File(filePath));
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                getString(R.string.uploading_please_wait), true);
        dialog.show();
        mStorageRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mPictureUrl = uri.toString();
                        dialog.cancel();
                        mPhotoCiv.setImageBitmap(BitmapFactory.decodeFile(filePath));
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Snackbar.make(getView(), R.string.somthing_went_wrong, Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
            }
        });
    }

    String mName, mLastName, mAge, mCityName, mGender, mPictureUrl, mUid, mDay, mMonth, mYear;
    TextInputLayout mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper, mDateOfBirthEtWrapper, mGenderEtWrapper;
    EditText mName_et, mLastNameEt, mCityNameEt, mGenderEt, mDateOfBirthEt;
    ArrayList<String> hobbyList;
    MaterialButton accept_btn, add_btn;
    FlowLayout flowLayout;
    AutoCompleteTextView mAutoCompleteTextView;
    TextView gender_click, birth_day_click;
    CircleImageView mPhotoCiv;
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private long lastTouchDown;
    private static int CLICK_ACTION_THRESHHOLD = 200;
    Boolean removeErrors_flag = false;
    File mFile;
    Uri imageUri;
    final String UPDATE_USER_FRAGMENT_TAG = "update_user_fragment";
    Boolean isPhotoExists = false;
    ArrayList<String> hobbys = new ArrayList<>();
    ArrayList<UserPost> userPosts = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_user_profile, container, false);
        mFireBaseAuth = FirebaseAuth.getInstance();

        mNameEtWrapper = rootView.findViewById(R.id.name_input_et_wrapper);
        mLastNameEtWrapper = rootView.findViewById(R.id.last_name_input_et_wrapper);
        mCityNameEtWrapper = rootView.findViewById(R.id.city_name_et_wrapper);
        mDateOfBirthEt = rootView.findViewById(R.id.date_of_birth_et);

        mName_et = rootView.findViewById(R.id.name_input_et);
        mLastNameEt = rootView.findViewById(R.id.last_name_input_et);
        mCityNameEt = rootView.findViewById(R.id.city_name_et);
        mDateOfBirthEt = rootView.findViewById(R.id.date_of_birth_et);
        mDateOfBirthEt.setInputType(InputType.TYPE_NULL);
        mAutoCompleteTextView = rootView.findViewById(R.id.auto_complete_tv);
        accept_btn = rootView.findViewById(R.id.accept_btn);
        add_btn = rootView.findViewById(R.id.add_btn);
        flowLayout = rootView.findViewById(R.id.flow_box);

        mFirebaseUser = mFireBaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mPhotoCiv = rootView.findViewById(R.id.photoCiv);

        mDatabaseReference.child("appHobbys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            String hobby = (String) snapshot.getValue();
                            if (hobby.replace(" ", "").matches("[a-zA-Z0-9-_.~%]{1,900}")) {
                                hobbys.add(hobby);
                            }
                            i++;
                        } catch (ClassCastException ex) {
                            Log.d("ilya", i + "");
                        }
                    }
//                    mDatabaseReference.child("appHobbys").setValue(hobbys);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, hobbys);
                    mAutoCompleteTextView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        hobbyList = new ArrayList<>();

        mGender = "Male";
        final Spinner spinner = rootView.findViewById(R.id.spinner);
        final String[] gender = new String[]{
                "Male", "Female", "Other"
        };

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hobby = mAutoCompleteTextView.getEditableText().toString();
                if (!hobbys.contains(hobby)) {
                    Snackbar.make(rootView, R.string.cant_find_the_hobby, Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                    mAutoCompleteTextView.setText("");
                } else {
                    if (hobbyList.contains(hobby)) {
                        Toast.makeText(getContext(), getString(R.string.you_already_choose_this_hobby), Toast.LENGTH_SHORT).show();
                        mAutoCompleteTextView.setText("");
                    } else {
                        TextView textView = new TextView(getContext());
                        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setText(hobby);
                        textView.setPadding(4, 4, 4, 4);
                        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTextSize(18);
                        textView.setBackground(getResources().getDrawable(R.drawable.label_bg2));
                        flowLayout.addView(textView);
                        mAutoCompleteTextView.setText("");
                        hobbyList.add(hobby);
                    }
                }
            }
        });

        final TextInputLayout[] allFields = {mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper};

        mPhotoCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                updateUserProfileFragmentListener.updateImage(intent, mPhotoCiv);
            }
        });
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
                mDateOfBirthEt.clearFocus();
                if (removeErrors_flag) {
                    clearErrors(allFields);
                }
            }
        };
        mNameEtWrapper.setOnTouchListener(errorCancel);
        mLastNameEtWrapper.setOnTouchListener(errorCancel);
        mCityNameEtWrapper.setOnTouchListener(errorCancel);


        mDateOfBirthEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchDown = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), UpdateUserProfileFragment.this,
                                Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH),
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                        break;

                }
                if (removeErrors_flag) {
                    clearErrors(allFields);
                }
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGender = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                        removeErrors_flag = true;
                        ErrorFields.add(edit);//add empty Edittext only in this ArayList
                        for (int i = ErrorFields.size() - 1; i >= 0; i--) {
                            TextInputLayout currentField = ErrorFields.get(i);
                            currentField.setError(getResources().getString(R.string.this_field_required));
                            currentField.requestFocus();
                        }
                    }
                }
                if (mDateOfBirthEt.getText().toString().equals(R.string.birthday)) {
                    continue_flag = false;
                    Snackbar.make(rootView, R.string.pick_a_Birthday, Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                }
                if (hobbyList.isEmpty()) {
                    continue_flag = false;
                    Toast.makeText(getContext(), getString(R.string.add_at_least_one_hobby), Toast.LENGTH_SHORT).show();
                }
                if (continue_flag) {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setmName(mNameEtWrapper.getEditText().getText().toString())
                            .setmCityName(mCityNameEtWrapper.getEditText().getText().toString())
                            .setmLastName(mLastNameEtWrapper.getEditText().getText().toString())
                            .setmAge(mAge).setmPictureUrl("")
                            .setmGender(mGender)
                            .setmHobbylist(hobbyList)
                            .setmDay(mDay).setmMonth(mMonth)
                            .setmYear(mYear)
                            .setmUserPostList(userPosts)
                            .setmUserToken(mUid);

                    if (isPhotoExists) {
                        userProfile.setmPictureUrl(mPictureUrl);
                    }
                    mDatabaseReference.child("appUsers").child(userProfile.getmUserToken()).setValue(userProfile);
                    mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mNameEtWrapper.getEditText().getText().toString() + " " + mLastNameEtWrapper.getEditText().getText().toString()).build());
                    DataStore.getInstance(getContext()).saveUser(userProfile);
                    updateUserProfileFragmentListener.afterUpdateUserUpdate(true, hobbys.toArray(new String[hobbys.size()]));
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
        Integer day = dayOfMonth;
        Integer year1 = year;
        Integer month1 = month;
        mDay = day.toString();
        mYear = year1.toString();
        mMonth = month1.toString();
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        mDateOfBirthEt.setText(date);
        Integer tempAge;
        tempAge = (getAge(year, month, dayOfMonth));
        mAge = tempAge.toString();
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

    void clearErrors(TextInputLayout[] allFields) {
        removeErrors_flag = false;
        for (TextInputLayout edit : allFields) {
            edit.setErrorEnabled(false);
        }
    }
}