package com.yoyo.hobbyist.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yoyo.hobbyist.Adapters.UserAdapter;
import com.yoyo.hobbyist.DataModels.Chat;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.Notifications.Token;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.UtilFuncs;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatsFragment extends Fragment {


    RecyclerView recyclerView;
    UserAdapter userAdapter;

    ArrayList<UserProfile> userProfiles;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ArrayList<String> usersList;
    HashMap<String,Integer> unreadMsgMap = new HashMap<>();


    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                ArrayList<String> temp = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        temp.add(chat.getReciver());
                    }
                    if (chat.getReciver().equals(firebaseUser.getUid())) {
                        temp.add(chat.getSender());
                        int i =0;
                        if (!chat.isIsseen()){
                            unreadMsgMap.put(chat.getSender(),++i);
                        }
                    }
                }

                usersList = UtilFuncs.removeDuplicates(temp);
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void readChats() {
        userProfiles = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("appUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfiles.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile user = snapshot.getValue(UserProfile.class);
                    if (usersList.contains(user.getUserToken())) {
                        userProfiles.add(user);
                    }
                }
                userAdapter = new UserAdapter(userProfiles, getActivity(), true,unreadMsgMap);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

}
