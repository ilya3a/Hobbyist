package com.yoyo.hobbyist.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yoyo.hobbyist.DataModels.Chat;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    ArrayList<UserProfile> mUserProfiles;
    Context mContext;

    public interface RecyclerCallBack {
        void onItemClicked(String userId);
    }

    UserAdapter.RecyclerCallBack recyclerCallBack;

    public UserAdapter(ArrayList<UserProfile> mUserProfiles, Context mContext) {
        this.mUserProfiles = mUserProfiles;
        this.mContext = mContext;
        if (mContext instanceof RecyclerCallBack) {
            recyclerCallBack = (RecyclerCallBack) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement UserAdapter.RecyclerCallback");
        }

    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder viewHolder, final int i) {

        final UserProfile userProfile = mUserProfiles.get(i);

        viewHolder.userName.setText(userProfile.getmName());
        if (!userProfile.getmPictureUrl().equals("")) {
            Glide.with(mContext).load(userProfile.getmPictureUrl()).thumbnail(0.4f).into(viewHolder.profileImage);
        }
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerCallBack.onItemClicked(userProfile.getmUserToken());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mUserProfiles.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView userName;
        RelativeLayout relativeLayout;

        public ViewHolder(final View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            relativeLayout = itemView.findViewById(R.id.user_item_layout);
        }
    }

    public void setMessages(ArrayList<UserProfile> mChat) {
        this.mUserProfiles = mUserProfiles;
        notifyDataSetChanged();
    }
}
