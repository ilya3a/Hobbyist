package com.yoyo.hobbyist.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.yoyo.hobbyist.Utilis.InternetConnection;
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
    ImageView mBackbtn;
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
    MessageFragmentListener messageFragmentListener;
    public MessageFragment() {
        // Required empty public constructor
    }

    public interface MessageFragmentListener{
        void onProfileClickd(String userId);
        void onBackPressed();
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
            sendNotification(reciver,userProfile.getName()+" "+userProfile.getLastName(), message);
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
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_logo_round,msg,getString(R.string.new_message_from)+ username,mUserId);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200){
                                if (response.body().success != 1){
                                    Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                    messageAdapter = new MessageAdapter(mChats, getContext(), imageUrl , mBuddyUserProfile.getGender());
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
        windowColorChnge();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        mUserNameTV = rootView.findViewById(R.id.chat_buddy_name_tv);
        mUserPicIV = rootView.findViewById(R.id.chat_buddy_iv);
        mSendBtn = rootView.findViewById(R.id.send_message_btn);
        mTextToSendEt = rootView.findViewById(R.id.chat_text_to_send);
        mBackbtn=rootView.findViewById(R.id.back_btn);
        mLastseenTV = rootView.findViewById(R.id.last_seen);

        recyclerView = rootView.findViewById(R.id.msg_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        final RelativeLayout relativeLayout=rootView.findViewById(R.id.activity_main);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("appUsers").child(mUserId);

        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageFragmentListener.onBackPressed();
            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBuddyUserProfile = dataSnapshot.getValue(UserProfile.class);
                mUserNameTV.setText(mBuddyUserProfile.getName() + " " + mBuddyUserProfile.getLastName());
                mLastseenTV.setText(mBuddyUserProfile.getStatus());
                if (!mBuddyUserProfile.getPictureUrl().equals("")) {
                    Glide.with(getContext()).load(mBuddyUserProfile.getPictureUrl()).into(mUserPicIV);
                }
                if (!mBuddyUserProfile.getGender().equalsIgnoreCase("male") && mBuddyUserProfile.getPictureUrl().equals("")){
                    Glide.with(getContext()).load(R.drawable.ic_avatar_woman).into(mUserPicIV);

                }
                readMessages(firebaseUser.getUid(), mUserId, mBuddyUserProfile.getPictureUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUserPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageFragmentListener.onProfileClickd(mUserId);
            }
        });

        seenMsg(mUserId);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.isNetworkAvailable(getContext())) {
                    notify = true;
                    String msg = mTextToSendEt.getText().toString();
                    if (!msg.equals("")) {
                        sendMessage(firebaseUser.getUid(), mUserId, msg);
                    } else {

                    }
                    mTextToSendEt.setText("");
                }
                else {
                    Snackbar.make(relativeLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            messageFragmentListener = (MessageFragment.MessageFragmentListener)context;

        }catch (ClassCastException e){
            throw new ClassCastException("Activity must implement the interface : messageFragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        messageFragmentListener=null;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, 600);
        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 600);
        }
    }

    void windowColorChnge(){
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.ic_logo_background));
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
