package com.yoyo.hobbyist.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yoyo.hobbyist.Adapters.PostsRecyclerViewAdapter;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;
//import com.yoyo.hobbyist.ViewModel.DataViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends Fragment {

    static String EDIT_MODE = "edit_mode_bollean";
    Context context;
    PostsRecyclerViewAdapter mAdapter;
    RecyclerView recyclerView;
    ArrayList<UserPost> userPosts = new ArrayList<>();
    ArrayList<UserPost> mPostsList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    Boolean editMode = false;
    RelativeLayout relativeLayout;

    public SearchListFragment() {
        // Required empty public constructor
    }

    public static SearchListFragment newInstance(Boolean editmode) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putBoolean( EDIT_MODE, editmode );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            editMode = getArguments().getBoolean( EDIT_MODE );
//            DataViewModel postViewModel = ViewModelProviders.of( this ).get( DataViewModel.class );
//            postViewModel.getPostsList().observe( this, new Observer<List<UserPost>>() {
//                @Override
//                public void onChanged(@Nullable List<UserPost> postsList) {
//                    mAdapter = new PostsRecyclerViewAdapter( (ArrayList<UserPost>) postsList, getContext(),editMode );
//                    recyclerView.setAdapter( mAdapter );
//                }
//            } );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.dashboard_fragment, container, false );

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        relativeLayout = rootView.findViewById(R.id.first);
        recyclerView = rootView.findViewById( R.id.dash_recycler );
        recyclerView.setHasFixedSize( true );

        mAdapter = new PostsRecyclerViewAdapter( userPosts, getContext(),editMode );



        getPostsFromUsers();
//        mPostsList = DataStore.getInstance(getContext()).getPostList();
//        recyclerView.setAdapter(new PostsRecyclerViewAdapter(DataStore.getInstance(getContext()).getPostList(),getContext(),false));

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getUserProfilesByHobbies(final String[] hobbies) {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("appUsers");
        Query usersQuery = mDatabaseReference.orderByKey();


        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<UserProfile> userProfiles = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    UserProfile profile = snapshot.getValue(UserProfile.class);

                    if (profile.getmHobbylist() != null) {
                        for (String hobby : hobbies) {
                            if (profile.getmHobbylist().contains(hobby)) {
                                userProfiles.add(profile);
                                break;
                            }
                        }
                    }
                }

                //update the adapter with relevant list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getPostsFromUsers() {
        final UserProfile currentUser = DataStore.getInstance(getContext()).getUser();
        if (editMode) {
            ArrayList<UserPost> postsToShowForUser = new ArrayList<>();
            postsToShowForUser=currentUser.getmUserPostList();
            mAdapter.setUserPosts(postsToShowForUser);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mAdapter);
        } else {
            final ArrayList<UserPost> tempPosts = new ArrayList<>();
            final ArrayList<UserPost> postsToShowForUser = new ArrayList<>();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference().child("appUsers");
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = snapshot.getValue(UserProfile.class);
                        if ((!userProfile.getmUserToken().equals(currentUser.getmUserToken()) && userProfile.getmUserPostList() != null)) {
                            tempPosts.addAll(userProfile.getmUserPostList());
                        }
                    }

                    for (UserPost post : tempPosts) {
                        if (currentUser.getmHobbylist().contains(post.getHobby())) {
                            postsToShowForUser.add(post);

                        }
                    }

                    // todo: set adapter

                    DataStore.getInstance(getContext()).saveUserPostList(postsToShowForUser);
                    mPostsList = postsToShowForUser;
                    mAdapter.setUserPosts(postsToShowForUser);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(mAdapter);
                    if (postsToShowForUser.size()==0){

                        relativeLayout.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.setUserPosts(DataStore.getInstance(getContext()).getPostList());
//                recyclerView.setAdapter(new PostsRecyclerViewAdapter(DataStore.getInstance(getContext()).getPostList(),getContext(),false));
//            }
//        },300);

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
