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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yoyo.hobbyist.DataModels.Chat;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private ArrayList<UserProfile> mUserProfiles;
    private Context mContext;
    private boolean isChat;
    private String theLastMsg;

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

        } catch (ClassCastException ex) {
            Log.d("ilya", mContext.toString() + "must implement interface");
            throw new ClassCastException(mContext.toString() + "must implement interface");
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
        if (!userProfile.getmGender().equals("Male") && userProfile.getmPictureUrl().equals("")) {
            Glide.with(mContext).load(R.drawable.ic_avatar_woman).into(viewHolder.profileImage);
        }
        if (isChat){
            lastMsg(userProfile.getmUserToken(),viewHolder.lastMsg);
        }
        else {
            viewHolder.lastMsg.setVisibility(View.GONE);
        }

        if (isChat) {
            if (userProfile.getmStatus().equals("Online")) {
                viewHolder.imgOn.setVisibility(View.VISIBLE);
                viewHolder.imgOff.setVisibility(View.GONE);
            } else {
                viewHolder.imgOn.setVisibility(View.GONE);
                viewHolder.imgOff.setVisibility(View.VISIBLE);
            }
        } else {
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
        RelativeLayout unreadHolder;
        RelativeLayout relativeLayout;
        ImageView imgOn;
        ImageView imgOff;
        TextView lastMsg;
        TextView numOfUnread;
        TextView timeOfLastMsg;


        public ViewHolder(final View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            unreadHolder = itemView.findViewById(R.id.num_of_unread_holder);
            relativeLayout = itemView.findViewById(R.id.user_item_layout);
            imgOn = itemView.findViewById(R.id.img_online);
            imgOff = itemView.findViewById(R.id.img_offline);
            timeOfLastMsg = itemView.findViewById(R.id.last_msg_time);
            lastMsg = itemView.findViewById(R.id.last_msg);
            numOfUnread = itemView.findViewById(R.id.num_of_unread);

        }
    }

    public void setMessages(ArrayList<UserProfile> mChat) {
        this.mUserProfiles = mUserProfiles;
        notifyDataSetChanged();
    }

    private void lastMsg(final String userId, final TextView lastMsg) {
        theLastMsg = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReciver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMsg = chat.getMessage();
                    }
                }
                switch (theLastMsg) {
                    case "":
                        lastMsg.setText("");
                        break;
                    default:
                        lastMsg.setText(theLastMsg);
                }
                theLastMsg = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
