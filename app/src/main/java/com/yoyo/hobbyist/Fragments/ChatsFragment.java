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
import com.yoyo.hobbyist.Adapters.UserAdapter;
import com.yoyo.hobbyist.DataModels.Chat;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {


    RecyclerView recyclerView;
    UserAdapter userAdapter;

    ArrayList<UserProfile> userProfiles;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ArrayList<String> usersList;


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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getReciver());
                    }
                    if (chat.getReciver().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                    //display 1 user from chats
                    for (String id : usersList) {
                        if (user.getmUserToken().equals(id)) {
                            if (userProfiles.size() != 0) {
                                for (UserProfile profile : userProfiles) {
                                    if (!user.getmUserToken().equals(profile.getmUserToken())) {
                                        userProfiles.add(user);
                                    }

                                }
                            } else {
                                userProfiles.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(userProfiles, getContext());
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
