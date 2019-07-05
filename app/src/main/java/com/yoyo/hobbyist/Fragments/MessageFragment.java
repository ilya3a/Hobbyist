package com.yoyo.hobbyist.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.yoyo.hobbyist.Adapters.MessageAdapter;
import com.yoyo.hobbyist.DataModels.Chat;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.Notifications.Client;
import com.yoyo.hobbyist.Notifications.Data;
import com.yoyo.hobbyist.Notifications.MyResponse;
import com.yoyo.hobbyist.Notifications.Sender;
import com.yoyo.hobbyist.Notifications.Token;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;
import com.yoyo.hobbyist.Utilis.UtilFuncs;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageFragment extends Fragment {

    private String mUserId;
    CircleImageView mUserPicIV;
    TextView mUserNameTV;
    ImageButton mSendBtn;
    UserProfile mBuddyUserProfile;
    EditText mTextToSendEt;
    TextView mLastseenTV;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    ArrayList<Chat> mChats;
    RecyclerView recyclerView;

    boolean notify = false;

    ValueEventListener seenValueEventListener;

    APIService apiService;
    public MessageFragment() {
        // Required empty public constructor
    }

    private void seenMsg(final String userId ){
        reference = FirebaseDatabase.getInstance().getReference("chats");
        seenValueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(firebaseUser.getUid())&&chat.getSender().equals(userId)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String reciver, String message) {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciver", reciver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("timesent", UtilFuncs.getCurrentDate());

        reference.child("chats").push().setValue(hashMap);


        UserProfile userProfile = DataStore.getInstance(getContext()).getUser();
        if(notify){
            sendNotification(reciver,userProfile.getmName()+" "+userProfile.getmLastName(), message);
        }
        notify=false;

    }

    private void sendNotification(String reciver, final String username, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(reciver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_logo_round,msg,"New Message from "+ username,mUserId);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if (response.body().success != 1){
                                    Toast.makeText(getContext(), "Faild", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessages(final String myId, final String userId, final String imageUrl) {
        mChats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReciver().equals(userId) && chat.getSender().equals(myId)) {
                        mChats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(mChats, getContext(), imageUrl , mBuddyUserProfile.getmGender());
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static MessageFragment newInstance(String userId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString("userId");
        }
        Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        mUserNameTV = rootView.findViewById(R.id.chat_buddy_name_tv);
        mUserPicIV = rootView.findViewById(R.id.chat_buddy_iv);
        mSendBtn = rootView.findViewById(R.id.send_message_btn);
        mTextToSendEt = rootView.findViewById(R.id.chat_text_to_send);
        mLastseenTV = rootView.findViewById(R.id.last_seen);

        recyclerView = rootView.findViewById(R.id.msg_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("appUsers").child(mUserId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBuddyUserProfile = dataSnapshot.getValue(UserProfile.class);
                mUserNameTV.setText(mBuddyUserProfile.getmName() + " " + mBuddyUserProfile.getmLastName());
                mLastseenTV.setText(mBuddyUserProfile.getmStatus());
                if (!mBuddyUserProfile.getmPictureUrl().equals("")) {
                    Glide.with(getContext()).load(mBuddyUserProfile.getmPictureUrl()).into(mUserPicIV);
                }
                if (!mBuddyUserProfile.getmGender().equalsIgnoreCase("male") && mBuddyUserProfile.getmPictureUrl().equals("")){
                    Glide.with(getContext()).load(R.drawable.ic_avatar_woman).into(mUserPicIV);

                }
                readMessages(firebaseUser.getUid(), mUserId, mBuddyUserProfile.getmPictureUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMsg(mUserId);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String msg = mTextToSendEt.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), mUserId, msg);
                } else {

                }
                mTextToSendEt.setText("");
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, 600);
        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 600);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(seenValueEventListener);
        DataStore.getInstance(getContext()).saveCurrenttalkingUser("none");
    }

    @Override
    public void onResume() {
        super.onResume();
        DataStore.getInstance(getContext()).saveCurrenttalkingUser(mUserId);

    }
}
