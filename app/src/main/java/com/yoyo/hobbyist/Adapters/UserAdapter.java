package com.yoyo.hobbyist.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private ArrayList<UserProfile> mUserProfiles;
    private Context mContext;
    private boolean isChat;

    public interface RecyclerCallBack {
        void ChatFragmentOnItemClicked(String userId);
    }

    private UserAdapter.RecyclerCallBack recyclerCallBack;

    public UserAdapter(ArrayList<UserProfile> mUserProfiles, Context mContext, boolean isChat) {
        this.mUserProfiles = mUserProfiles;
        this.mContext = mContext;
        this.isChat = isChat;
try {
    recyclerCallBack = (RecyclerCallBack) mContext;

}catch (ClassCastException ex){
    Log.d("ilya",mContext.toString()+ "must implement interface" );
    throw new ClassCastException(mContext.toString()+ "must implement interface");
}



    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder viewHolder, final int i) {

        final UserProfile userProfile = mUserProfiles.get(i);

        viewHolder.userName.setText(userProfile.getmName());
        if (!userProfile.getmPictureUrl().equals("")) {
            Glide.with(mContext).load(userProfile.getmPictureUrl()).thumbnail(0.4f).into(viewHolder.profileImage);
        }
        if (!userProfile.getmGender().equals("Male") && userProfile.getmPictureUrl().equals("")){
            Glide.with(mContext).load(R.drawable.ic_avatar_woman).into(viewHolder.profileImage);
        }

        if (isChat){
            if (userProfile.getmStatus().equals("Online")){
                viewHolder.imgOn.setVisibility(View.VISIBLE);
                viewHolder.imgOff.setVisibility(View.GONE);
            }else {
                viewHolder.imgOn.setVisibility(View.GONE);
                viewHolder.imgOff.setVisibility(View.VISIBLE);
            }
        }else {
            viewHolder.imgOn.setVisibility(View.GONE);
            viewHolder.imgOff.setVisibility(View.GONE);
        }
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerCallBack.ChatFragmentOnItemClicked(userProfile.getmUserToken());
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
        ImageView imgOn;
        ImageView imgOff;

        public ViewHolder(final View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            relativeLayout = itemView.findViewById(R.id.user_item_layout);
            imgOn =itemView.findViewById(R.id.img_online);
            imgOff =itemView.findViewById(R.id.img_offline);

        }
    }

    public void setMessages(ArrayList<UserProfile> mChat) {
        this.mUserProfiles = mUserProfiles;
        notifyDataSetChanged();
    }
}
