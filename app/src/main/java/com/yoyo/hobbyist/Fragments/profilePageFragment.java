package com.yoyo.hobbyist.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class profilePageFragment extends Fragment {

    TextView mNameTv;
    CircleImageView mProfilePhoto;
    FirebaseUser mUser;
    FirebaseAuth mFireBaseAuth;
    UserProfile mUserProfile;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;
    DataStore dataStore;
    private OnFragmentInteractionListener mListener;


    public profilePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);
        mProfilePhoto=rootView.findViewById(R.id.profilePhoto_iv);
        mNameTv=rootView.findViewById(R.id.name_tv);
        dataStore=DataStore.getInstance(getContext());
        mUserProfile=dataStore.getUser();




        mNameTv.setText(mUserProfile.getmName()+" "+mUserProfile.getmLastName());
        Glide.with(getContext()).load(mUserProfile.getmPictureUrl()).into(mProfilePhoto);



        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
