package com.yoyo.hobbyist.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.gson.Gson;
import com.himanshurawat.imageworker.ImageWorker;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.nex3z.flowlayout.FlowLayout;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;
import com.yoyo.hobbyist.Utilis.InternetConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import jp.wasabeef.blurry.Blurry;


public class ProfilePageFragment extends Fragment {

    ArrayList<String> hobbysFromServer = new ArrayList<>();
    SwitchCompat mNotificationSw;
    ImageView mBlurryImageView, mEditProfile, mEditPosts, mEditCity;
    MaterialButton mAddBtn;
    AutoCompleteTextView mAutoCompleteTextView;
    TextView mNameTv, mPostsCount, mHobbysCount;
    EditText mAge, mCity, mGenderEt, mName, mLastName;
    String mPictureUrl;
    FlowLayout flowLayout;
    CircleImageView mProfilePhoto;
    UserProfile mUserProfile;
    //fireBase
    FirebaseUser mFireBaseUser;
    FirebaseAuth mFireBaseAuth;
    DatabaseReference mFireBaseDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;
    StorageReference mFireBaseStorageRef;

    LinearLayout mEditHobbysLayot;
    DataStore dataStore;
    File mFile;
    String mFilePath;
    ArrayList<String> mHobbysList;
    ArrayList<TextView> mHobbysTv;
    Uri imageUri;
    FloatingActionButton mFab, mExitFab;
    Boolean editMode = false;
    private OnFragmentInteractionListener mListener;
    Boolean nameHasChanged = false;
    ProfileFragmentListener profileFragmentListener;
    String mUserId;
    private Gson mGson = new Gson();
    Boolean onlyShowProfile = false;


    public static ProfilePageFragment newInstance(String userId) {
        ProfilePageFragment fragment = new ProfilePageFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public interface ProfileFragmentListener {
        void updateImage(Intent intent, View view);

        void logOut();

        void notifCheckChange(boolean isChecked);

        void openChatFromProfile(String userId);

        void editPostsClickd();
    }

    public ProfilePageFragment() {
        // Required empty public constructor
    }


    public void updateUserImage(final String filePath) {
        final Date currentTime = Calendar.getInstance().getTime();
        mFireBaseStorageRef = FirebaseStorage.getInstance().getReference("images/" + currentTime.toString() + ".png");
        //final File file=new File(filePath);
        //Uri uri = Uri.fromFile( file );
        Uri uri = Uri.fromFile(new File(filePath));
        //mProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                getString(R.string.uploading_please_wait), true);
        dialog.show();
        mFireBaseStorageRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                mFireBaseStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dialog.cancel();
                        mPictureUrl = uri.toString();
                        mProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(filePath));
//                        mProfilePhoto.setRotation(-90);
                        Blurry.with(getContext()).capture(mProfilePhoto).into(mBlurryImageView);
                        mUserProfile.setPictureUrl(mPictureUrl);
                        updateProfileOnfireBase();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataStore = DataStore.getInstance(getContext());
        if (getArguments() != null) {
            mUserId = getArguments().getString("userId");
            mUserProfile = mGson.fromJson(mUserId, UserProfile.class);
            onlyShowProfile = true;
        } else
            mUserProfile = dataStore.getUser();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        final View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);
        mProfilePhoto = rootView.findViewById(R.id.profilePhoto_iv);
        mProfilePhoto.setEnabled(false);
        mPostsCount = rootView.findViewById(R.id.number_of_posts);
        mHobbysCount = rootView.findViewById(R.id.number_of_hobbys);
        mAutoCompleteTextView = rootView.findViewById(R.id.auto_complete_tv);
        mAge = rootView.findViewById(R.id.age_et);
        mCity = rootView.findViewById(R.id.city_et);
        mNameTv = rootView.findViewById(R.id.name_tv);
        mFab = rootView.findViewById(R.id.fab);
        flowLayout = rootView.findViewById(R.id.flow_layout);
        mEditProfile = rootView.findViewById(R.id.picture_edit_image);
        mEditPosts = rootView.findViewById(R.id.post_edit_image);
        mEditCity = rootView.findViewById(R.id.post_edit_city);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mAddBtn = rootView.findViewById(R.id.add_btn);
        mHobbysTv = new ArrayList<>();
        mEditHobbysLayot = rootView.findViewById(R.id.add_hobbys_layout);
        mNotificationSw = rootView.findViewById(R.id.notif_sw);
        mExitFab = rootView.findViewById(R.id.fab_logout);
        mGenderEt = rootView.findViewById(R.id.gender_et);
        mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        final Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        final Drawable originalDrawable = mAge.getBackground();
        mAge.setBackgroundColor(Color.TRANSPARENT);
        mBlurryImageView = rootView.findViewById(R.id.blur_test);
        mName = rootView.findViewById(R.id.name_change_edittext);
        mLastName = rootView.findViewById(R.id.last_name_change_edittext);
        mNotificationSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profileFragmentListener.notifCheckChange(isChecked);
            }
        });

        mNotificationSw.setChecked(DataStore.getInstance(getContext()).isNotifOk());
        mPostsCount.setText(DataStore.getInstance(getContext()).getUser().getUserPostList().size() + "");

        mHobbysList = mUserProfile.getHobbyList();
        mGenderEt.setText(mUserProfile.getGender());

        if (onlyShowProfile) {
            LinearLayout mNotificationLayout = rootView.findViewById(R.id.notification_layout);
            mNotificationLayout.setVisibility(View.GONE);
            mExitFab.setVisibility(View.INVISIBLE);
            //mFab.setVisibility(View.INVISIBLE);
            mFab.setImageResource(R.drawable.ic_chat_icon_selected);

        }

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfilePhoto.callOnClick();
            }
        });

        mEditPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileFragmentListener.editPostsClickd();
            }
        });
        mExitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileFragmentListener.logOut();
                DataStore.getInstance(getContext()).clearAllData();
            }
        });

        mFireBaseDatabaseReference.child("appHobbys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> hobbysFromServer = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            String hobby = (String) snapshot.getValue();
                            if (hobby.replace(" ", "").matches("[a-zA-Z0-9-_.~%]{1,900}")) {
                                hobbysFromServer.add(hobby);
                            }
                            i++;
                        } catch (ClassCastException ex) {
                            Log.d("ilya", i + "");
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, hobbysFromServer);
                    mAutoCompleteTextView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mHobbysCount.setText(String.valueOf(mHobbysList.size()));
        mAge.setText(mUserProfile.getAge());
        mPostsCount.setText(String.valueOf(mUserProfile.getUserPostList() == null ? 0 : mUserProfile.getUserPostList().size()));
        mCity.setText(mUserProfile.getCityName());
        mNameTv.setText(mUserProfile.getName() + "       " + mUserProfile.getLastName());
        updateflow();


        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hobby = mAutoCompleteTextView.getEditableText().toString();
                if (!hobbysFromServer.contains(hobby)) {
                    Snackbar.make(rootView, getString(R.string.cant_find_the_hobby), Snackbar.LENGTH_SHORT).show();
                    mAutoCompleteTextView.setText("");
                } else {
                    if (mHobbysList.contains(hobby)) {
                        Toast.makeText(getContext(), getString(R.string.you_already_choose_this_hobby), Toast.LENGTH_SHORT).show();
                        mAutoCompleteTextView.setText("");
                    } else {
                        TextView textView = new TextView(getContext());
                        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setText(hobby);
                        textView.setPadding(7, 7, 7, 7);
                        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTextSize(18);
                        textView.setBackground(getResources().getDrawable(R.drawable.label_bg2));
                        flowLayout.addView(textView);
                        mAutoCompleteTextView.setText("");
                        mHobbysList.add(hobby);
                    }
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.isNetworkAvailable(getContext())) {
                    if (onlyShowProfile) {
                        profileFragmentListener.openChatFromProfile(mUserProfile.getUserToken());
                    } else {
                        if (editMode.equals(false)) {
                            editMode = true;
                            mFab.setImageResource(R.drawable.ic_check_black_24dp);
                            mCity.setBackground(originalDrawable);
                            mCity.setEnabled(true);
                            mEditHobbysLayot.setVisibility(View.VISIBLE);
                            for (TextView tv : mHobbysTv) {
                                tv.setClickable(true);
                                tv.setAnimation(shake);
                                tv.startAnimation(shake);
                            }
                            if (!(mUserProfile.getUserPostList() == null)) {
                                mEditPosts.setVisibility(View.VISIBLE);
                            }
                            mEditHobbysLayot.setVisibility(View.VISIBLE);
                            mProfilePhoto.setEnabled(true);
                            mName.setText(mUserProfile.getName());
                            mLastName.setText(mUserProfile.getLastName());
                            mName.setVisibility(View.VISIBLE);
                            mLastName.setVisibility(View.VISIBLE);
                            mNameTv.setVisibility(View.GONE);
                            mEditProfile.setVisibility(View.VISIBLE);

                            mEditCity.setVisibility(View.VISIBLE);
                            mExitFab.setVisibility(View.GONE);
                        } else {
                            mUserProfile = DataStore.getInstance(getContext()).getUser();
                            editMode = false;
                            mFab.setImageResource(R.drawable.ic_edit_black_24dp);
                            mCity.setBackgroundColor(Color.TRANSPARENT);
                            mCity.setEnabled(false);
                            mEditHobbysLayot.setVisibility(View.GONE);
                            for (TextView tv : mHobbysTv) {
                                tv.setClickable(false);
                                tv.setAnimation(shake);
                                tv.clearAnimation();
                            }
                            mEditHobbysLayot.setVisibility(View.GONE);

                            if (!mCity.getText().toString().equals("")) {
                                mUserProfile.setCityName(mCity.getText().toString());
                            } else {
                                mCity.setText(mUserProfile.getCityName());
                            }
                            if (mHobbysList.isEmpty()) {
                                mHobbysList = mUserProfile.getHobbyList();
                            }
                            //profileFragmentListener.notifCheckChange(false);
                            mUserProfile.setHobbyList(mHobbysList);
                            Integer temp = mHobbysList.size();
                            mHobbysCount.setText(temp.toString());
                            mProfilePhoto.setEnabled(false);
                            //profileFragmentListener.notifCheckChange(true);

                            if (mName.getText().toString().equals("") || mLastName.getText().toString().equals("")) {

                            } else {
                                nameHasChanged = true;
                                mNameTv.setText(mName.getText().toString() + "   " + mLastName.getText().toString());
                                mUserProfile.setName(mName.getText().toString());
                                mUserProfile.setLastName(mLastName.getText().toString());
                            }
                            mName.setVisibility(View.GONE);
                            mLastName.setVisibility(View.GONE);
                            mNameTv.setVisibility(View.VISIBLE);
                            mEditProfile.setVisibility(View.GONE);
                            mEditPosts.setVisibility(View.GONE);
                            mEditCity.setVisibility(View.GONE);
                            if (!((mUserProfile.getUserPostList()) == null)) {
                                mPostsCount.setText(String.valueOf(mUserProfile.getUserPostList().size()));
                            } else {
                                mPostsCount.setText("0");
                            }
                            ArrayList<UserPost> test = new ArrayList<>();
                            test=mUserProfile.getmUserPostList();
                            mExitFab.setVisibility(View.VISIBLE);
                            updateflow();
                            updateProfileOnfireBase();
                        }
                    }
                }else {
                    Snackbar.make(rootView.findViewById(R.id.profile_cooardinator),R.string.no_internet_connection,Snackbar.LENGTH_SHORT).setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFab.callOnClick();
                        }
                    }).show();
                }
            }
        });


        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                profileFragmentListener.updateImage(intent, mProfilePhoto);
            }
        });

        if (mUserProfile.getPictureUrl().equals("") && !mUserProfile.getGender().equals("Male")) {

            Glide.with(getContext()).load(R.drawable.ic_avatar_woman).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Bitmap bitmap = ImageWorker.Companion.convert().drawableToBitmap(resource);
                    Blurry.with(getContext()).radius(10).from(bitmap).into(mBlurryImageView);
                    return false;
                }
            }).into(mProfilePhoto);
        }
        if (!mUserProfile.getPictureUrl().equals("")) {
            Glide.with(getContext()).load(mUserProfile.getPictureUrl()).thumbnail(0.3f).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Bitmap bitmap = ImageWorker.Companion.convert().drawableToBitmap(resource);
                    Blurry.with(getContext()).radius(10).from(bitmap).into(mBlurryImageView);
                    return false;
                }
            }).into(mProfilePhoto);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            profileFragmentListener = (ProfilePageFragment.ProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement the interface : profileFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    void updateflow() {
        flowLayout.removeAllViews();
        mHobbysTv.clear();
        for (final String hobby : mHobbysList) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(hobby);
            textView.setPadding(10, 10, 10, 10);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(18);
            textView.setTag(hobby);
            textView.setBackground(getResources().getDrawable(R.drawable.label_bg2));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHobbysList.remove(v.getTag());
                    mUserProfile.setHobbyList(mHobbysList);
                    flowLayout.removeView(v);
                }
            });
            textView.setClickable(false);
            mHobbysTv.add(textView);
            flowLayout.addView(textView);

        }
    }

    void updateProfileOnfireBase() {
        mFireBaseDatabaseReference.child("appUsers").child(mUserProfile.getUserToken()).setValue(mUserProfile);
        mFireBaseUser.updateProfile(new UserProfileChangeRequest.Builder().build());
        DataStore.getInstance(getContext()).saveUser(mUserProfile);
        if (nameHasChanged) {
            nameHasChanged = false;
            mFireBaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mName.getText().toString() + " " + mLastName.getText().toString()).build());
        }
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public void getUserProfile() {
        FirebaseDatabase mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference2 = mFirebaseDatabase2.getReference().child("appUsers").child(mUserId);
        Query usersQuery = mDatabaseReference2.orderByKey();

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, 600);
        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 1000);
        }
    }

    public void UpdateUser() {
//        mUserProfile = DataStore.getInstance(getContext()).getUser();
        //did not work properly
        //mUserProfile = DataStore.getInstance(getContext()).getUser();
    }
}


