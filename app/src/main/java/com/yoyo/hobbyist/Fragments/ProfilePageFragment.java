package com.yoyo.hobbyist.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.himanshurawat.imageworker.ImageWorker;
import com.nex3z.flowlayout.FlowLayout;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;


public class ProfilePageFragment extends Fragment {

   ArrayList<String> hobbysFromServer = new ArrayList<>();
    SwitchCompat mNotificationSw;
    ImageView mBlurryImageView;
    MaterialButton mAddBtn;
    AutoCompleteTextView mAutoCompleteTextView;
    TextView mNameTv,mPostsCount,mHobbysCount;
    EditText mAge,mCity, mGenderEt;
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
    ArrayList<String> mHobbysList;
    ArrayList<TextView> mHobbysTv;
    Uri imageUri;
    FloatingActionButton mFab,mExitFab;
    Boolean editMode=false;
    private OnFragmentInteractionListener mListener;

    ProfileFragmentListener profileFragmentListener;

    public interface ProfileFragmentListener {
        void updateImage(Intent intent, View view);
        void logOut();
    }
    public ProfilePageFragment() {
        // Required empty public constructor
    }
    public void updateUserImage(){
        final Date currentTime = Calendar.getInstance().getTime();
        mFireBaseStorageRef=FirebaseStorage.getInstance().getReference("images/"+currentTime.toString()+".jpg");
        //update photo from storeg
        Uri uri = Uri.fromFile( mFile );
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                "Uploading. Please wait...", true);
        dialog.show();
        mFireBaseStorageRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                mFireBaseStorageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dialog.cancel();
                        mPictureUrl = uri.toString();
                        mProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(mFile.getAbsolutePath()));
                        Blurry.with(getContext()).capture(mProfilePhoto).into(mBlurryImageView);
                        mUserProfile.setmPictureUrl(mPictureUrl);
                        updateProfileOnfireBase();
                    }
                } );

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "test faild", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }
//    public void updateUserImage() {
//        final Date currentTime = Calendar.getInstance().getTime();
//        mFireBaseStorageRef = FirebaseStorage.getInstance().getReference( "images/"+ currentTime.toString()+".jpg" );
//        mProfilePhoto.setImageBitmap( BitmapFactory.decodeFile( mFile.getAbsolutePath() ) );
//       
//        Blurry.with(getContext()).capture(mProfilePhoto).into(mBlurryImageView);
//        Uri uri = Uri.fromFile( mFile );
//        mFireBaseStorageRef.putFile( uri ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                mFireBaseStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        mPictureUrl = uri.toString();
//                        mUserProfile.setmPictureUrl(mPictureUrl);
//                    }
//                } );
//            }
//        } ).addOnFailureListener( new OnFailureListener() {
//
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText( getContext(), "Update Faild", Toast.LENGTH_SHORT ).show();
//            }
//        } ).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                mFireBaseStorageRef.child( "images/"+ currentTime.toString()+".jpg" ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Toast.makeText( getContext(), uri.toString(), Toast.LENGTH_LONG ).show();
//                        Glide.with( getContext() ).load( uri ).into( mProfilePhoto );
//                        mUserProfile.setmPictureUrl(mPictureUrl);
//                        mFireBaseDatabaseReference.child( "appUsers" ).child( mUserProfile.getmUserToken() ).setValue( mUserProfile );
//                        mFireBaseUser.updateProfile( new UserProfileChangeRequest.Builder().build());
//                        updateProfileOnfireBase();
//                    }
//                } );
//            }
//        });
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);
        mProfilePhoto=rootView.findViewById(R.id.profilePhoto_iv);
        mProfilePhoto.setEnabled(false);
        mPostsCount=rootView.findViewById(R.id.number_of_posts);
        mHobbysCount=rootView.findViewById(R.id.number_of_hobbys);
        mAutoCompleteTextView=rootView.findViewById(R.id.auto_complete_tv);
        mAge=rootView.findViewById(R.id.age_et);
        mCity=rootView.findViewById(R.id.city_et);
        mNameTv=rootView.findViewById(R.id.name_tv);
        mFab=rootView.findViewById(R.id.fab);
        flowLayout=rootView.findViewById(R.id.flow_layout);
        dataStore=DataStore.getInstance(getContext());
        mFireBaseAuth= FirebaseAuth.getInstance();
        mFireBaseUser =mFireBaseAuth.getCurrentUser();
        mAddBtn=rootView.findViewById(R.id.add_btn);
        mHobbysTv =new ArrayList<>();
        mEditHobbysLayot =rootView.findViewById(R.id.add_hobbys_layout);
        mNotificationSw=rootView.findViewById(R.id.notif_sw);
        mExitFab=rootView.findViewById(R.id.fab_logout);
        mGenderEt =rootView.findViewById(R.id.gender_et);
        mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        final Animation shake = AnimationUtils.loadAnimation(getContext(),R.anim.shake);
        final Drawable originalDrawable = mAge.getBackground();
        mAge.setBackgroundColor(Color.TRANSPARENT);
        mBlurryImageView =rootView.findViewById(R.id.blur_test);
        mUserProfile=dataStore.getUser();
        mHobbysList =mUserProfile.getmHobbylist();

        mGenderEt.setText(mUserProfile.getmGender());

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
                if (dataSnapshot.exists()) {
                    int i =0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            String hobby = (String) snapshot.getValue();
                            if (hobby.replace(" ","").matches("[a-zA-Z0-9-_.~%]{1,900}")){
                                hobbysFromServer.add(hobby);}
                            i++;
                        }catch (ClassCastException ex){
                            Log.d("ilya",i+"");
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_dropdown_item_1line, hobbysFromServer);
                    mAutoCompleteTextView.setAdapter( adapter );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //mPostsCount.setText(String.valueOf(mUserProfile.getmUserPostList().size()));


        mHobbysCount.setText(String.valueOf(mHobbysList.size()));
        mAge.setText(mUserProfile.getmAge());
        //mPostsCount.setText();
        mCity.setText(mUserProfile.getmCityName());
        mNameTv.setText(mUserProfile.getmName()+" "+mUserProfile.getmLastName());
        updateflow();

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hobby=mAutoCompleteTextView.getEditableText().toString();
                if (!hobbysFromServer.contains(hobby)){
                    Snackbar.make(rootView, "Cant find the hobby", Snackbar.LENGTH_SHORT ).show();
                    mAutoCompleteTextView.setText("");
                }
                else
                {
                    if (mHobbysList.contains(hobby))
                    {
                        Toast.makeText(getContext(), "You already choose this hobby " , Toast.LENGTH_SHORT).show();
                        mAutoCompleteTextView.setText("");
                    }
                    else {
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
               if (editMode.equals(false)){
                   editMode=true;
                   mFab.setImageResource(R.drawable.ic_check_black_24dp);
                   mCity.setBackground(originalDrawable);
                   mCity.setEnabled(true);
                   mEditHobbysLayot.setVisibility(View.VISIBLE);
                   for (TextView tv :mHobbysTv){
                       tv.setLongClickable(true);
                       tv.setAnimation(shake);
                       tv.startAnimation(shake);
                   }
                   mEditHobbysLayot.setVisibility(View.VISIBLE);
                   mProfilePhoto.setEnabled(true);
               }
               else{
                   editMode=false;
                   mFab.setImageResource(R.drawable.ic_edit_black_24dp);
                   mCity.setBackgroundColor(Color.TRANSPARENT);
                   mCity.setEnabled(false);
                   mEditHobbysLayot.setVisibility(View.GONE);
                   for (TextView tv :mHobbysTv){
                       tv.setLongClickable(false);
                       tv.setAnimation(shake);
                       tv.clearAnimation();
                   }
                   mEditHobbysLayot.setVisibility(View.GONE);

                   if (!mCity.getText().toString().equals("")){
                       mUserProfile.setmCityName(mCity.getText().toString());
                   }
                   else
                   {
                       mCity.setText(mUserProfile.getmCityName());
                   }
                   if (mHobbysList.isEmpty())
                   {
                       mHobbysList=mUserProfile.getmHobbylist();
                   }
                   mUserProfile.setmHobbylist(mHobbysList);
                   Integer temp= mHobbysList.size();
                   mHobbysCount.setText(temp.toString());
                   updateflow();
                   updateProfileOnfireBase();
                   mProfilePhoto.setEnabled(false);
               }
            }
        });
        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                mFile = new File( Environment.getExternalStorageDirectory(), currentTime + "Hobbyist.jpg" );

                imageUri = FileProvider.getUriForFile( getContext(),
                        "com.yoyo.hobbyist.provider",
                        mFile );
                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                intent.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );
                profileFragmentListener.updateImage( intent, mProfilePhoto );
            }
        });


        Glide.with(getContext()).load(mUserProfile.getmPictureUrl()).addListener(new RequestListener<Drawable>() {
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


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            profileFragmentListener = (ProfilePageFragment.ProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException( "Activity must implement the interface : profileFragmentListener" );
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
    void updateflow(){
        flowLayout.removeAllViews();
        mHobbysTv.clear();
        for  (final String hobby : mHobbysList){
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(hobby);
            textView.setPadding(8, 8, 8, 8);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(18);
            textView.setTag(hobby);
            textView.setBackground(getResources().getDrawable(R.drawable.label_bg2));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHobbysList.remove(v.getTag());
                    mUserProfile.setmHobbylist(mHobbysList);
                    updateflow();
                }
            });
            textView.setLongClickable(false);
            mHobbysTv.add(textView);
            flowLayout.addView(textView);

        }
    }
    void updateProfileOnfireBase(){
        mFireBaseDatabaseReference.child( "appUsers" ).child( mUserProfile.getmUserToken() ).setValue( mUserProfile );
        mFireBaseUser.updateProfile( new UserProfileChangeRequest.Builder().build() );
        DataStore.getInstance(getContext()).saveUser(mUserProfile);
    }
}
