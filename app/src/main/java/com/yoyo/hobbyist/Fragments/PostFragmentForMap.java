package com.yoyo.hobbyist.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.yoyo.hobbyist.R;

@SuppressLint("ValidFragment")
public class PostFragmentForMap extends Fragment {

    private OnPostForMapListener mListener;
    ImageView profileImageView;
    TextView userNameTV, userCityTV, postDescriptionTv, hobbyTV, dateTV;
    AppCompatImageButton chatBtn;

    String imageProfileUrl;
    String userName;
    String cityName;
    String postDescription;
    String hobby;
    String date;
    String gender;

    public PostFragmentForMap(String imageProfileUrl, String userName, String cityName, String postDescription, String hobby, String date, String gender) {
        this.imageProfileUrl = imageProfileUrl;
        this.userName = userName;
        this.cityName = cityName;
        this.postDescription = postDescription;
        this.hobby = hobby;
        this.date = date;
        this.gender = gender;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_post_fragment_for_map, container, false);

        profileImageView = rootView.findViewById(R.id.post_profile_image_for_map);
        if (!imageProfileUrl.equals("")) {
            Glide.with(getContext()).load(imageProfileUrl).thumbnail(0.3f).into(profileImageView);
        }
        if (!gender.equals("Male") && imageProfileUrl.equals("")) {
            Glide.with(getContext()).load(R.drawable.ic_avatar_woman).into(profileImageView);
        }
        userNameTV = rootView.findViewById(R.id.post_profile_name_for_map);
        userNameTV.setText(userName);
        userCityTV = rootView.findViewById(R.id.post_city_for_map);
        userCityTV.setText(cityName);
        postDescriptionTv = rootView.findViewById(R.id.post_description_for_map);
        postDescriptionTv.setText(postDescription);
        hobbyTV = rootView.findViewById(R.id.hobby_name_for_map);
        hobbyTV.setText(hobby);
        dateTV = rootView.findViewById(R.id.create_date_for_map);
        dateTV.setText(date);
        chatBtn = rootView.findViewById(R.id.post_chat_btn_for_map);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostForMapListener) {
            mListener = (OnPostForMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostForMapListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnPostForMapListener {
        void onChatBtnClicked(String userId);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.UP, enter, 1000);
        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 1000);
        }
    }
}
