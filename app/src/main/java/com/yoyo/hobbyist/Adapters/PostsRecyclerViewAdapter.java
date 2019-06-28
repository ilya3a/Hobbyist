package com.yoyo.hobbyist.Adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {


    interface RecyclerCallBack {
        void onItemClicked(UserPost article);
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
//        recyclerCallBack = (RecyclerCallBack) mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.post_layout, viewGroup, false );

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final UserPost post = userPosts.get( i );

        //Glide.with(mContext).load(post.getUserProfilePicUrl()).thumbnail(0.1f).into(viewHolder.coverImageView);
//        viewHolder.userNameTV.setText(post.getUserName());
//        viewHolder.postTv.setText(post.getAge());


        viewHolder.parentLayout.setTag( i );
        viewHolder.parentLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerCallBack.onItemClicked(post);
            }
        } );
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView coverImageView;
        ImageView onlineImageView;
        //        TextView userNameTV, userCityTV, postTv, postHeadlineTv;
        CardView parentLayout;

        public ViewHolder(final View itemView) {
            super( itemView );
            coverImageView = itemView.findViewById( R.id.post_profile_image );
            onlineImageView = itemView.findViewById( R.id.post_online_icon );
//            userNameTV = itemView.findViewById( R.id.post_profile_name );
//            postHeadlineTv = itemView.findViewById( R.id.headline_tv );
//            postTv = itemView.findViewById( R.id.post_tv );
//            userCityTV = itemView.findViewById( R.id.post_city );
            parentLayout = itemView.findViewById( R.id.parent_card );
        }
    }
}
