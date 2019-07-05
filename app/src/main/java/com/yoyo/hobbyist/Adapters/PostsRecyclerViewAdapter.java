package com.yoyo.hobbyist.Adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;
import java.util.Calendar;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {


    public interface RecyclerCallBack {
        void postOnChatItemClicked(String userId);
        void postOnPhotoItemClicked(String userId);
    }

    RecyclerCallBack recyclerCallBack;

    public void setUserPosts(ArrayList<UserPost> userPosts) {
        this.userPosts = userPosts;
        notifyDataSetChanged();
    }

    ArrayList<UserPost> userPosts;
    Context mContext;


    public PostsRecyclerViewAdapter(ArrayList<UserPost> userPosts, Context mContext) {
        this.userPosts = userPosts;
        this.mContext = mContext;
        recyclerCallBack = (RecyclerCallBack) mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final UserPost post = userPosts.get(i);

        if (!post.getUserProfilePicUrl().equals("")) {
            Glide.with(mContext).load(post.getUserProfilePicUrl()).thumbnail(0.1f).into(viewHolder.coverImageView);
        }
//        if (!post.getGender().equals("Male") && post.getImageUrl().equals("")){
//            Glide.with(mContext).load(R.drawable.ic_avatar_woman).into(viewHolder.coverImageView);
//        }
        viewHolder.userNameTV.setText(post.getUserName());
        viewHolder.postTv.setText(post.getPostDescription());
        viewHolder.userCityTV.setText(post.getCityName());
        viewHolder.hobbyTV.setText(post.getHobby());
        viewHolder.dateTV.setText(post.getDate());
        viewHolder.parentLayout.setTag(i);

        viewHolder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerCallBack.postOnChatItemClicked(post.getUserToken());
            }
        });

        viewHolder.coverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerCallBack.postOnPhotoItemClicked(post.getUserToken());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView coverImageView;
        ImageView onlineImageView;
        TextView userNameTV, userCityTV, postTv, hobbyTV, dateTV;
        CardView parentLayout;
        AppCompatImageButton chatBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.post_profile_image);
//            onlineImageView = itemView.findViewById( R.id.post_online_icon );
            userNameTV = itemView.findViewById(R.id.post_profile_name);
            postTv = itemView.findViewById(R.id.post_description);
            dateTV = itemView.findViewById(R.id.create_date);
            hobbyTV = itemView.findViewById(R.id.hobby_name);
            userCityTV = itemView.findViewById(R.id.post_city);
            chatBtn = itemView.findViewById(R.id.post_chat_btn);
            parentLayout = itemView.findViewById(R.id.parent_card);
        }
    }
}
