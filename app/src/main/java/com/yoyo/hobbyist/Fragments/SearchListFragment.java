package com.yoyo.hobbyist.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yoyo.hobbyist.Utilis.InternetConnection;
import com.yoyo.hobbyist.ViewModel.DataViewModel;
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
    DataViewModel dataViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<UserPost> postsToShowForUser;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.dashboard_fragment, container, false );
        //DataViewModel dataViewModel;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        relativeLayout = rootView.findViewById( R.id.first );
        recyclerView = rootView.findViewById( R.id.dash_recycler );
        recyclerView.setHasFixedSize( true );
        swipeRefreshLayout=rootView.findViewById(R.id.swipeRefresh_layout);
        mAdapter = new PostsRecyclerViewAdapter( userPosts, getContext(), editMode );
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                check();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2 * 1000);

            }
        });

       check();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        postsToShowForUser=DataStore.getInstance(getContext()).getPostList();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        DataStore.getInstance( getContext() ).saveUserPostList(postsToShowForUser);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getUserProfilesByHobbies(final String[] hobbies) {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child( "appUsers" );
        Query usersQuery = mDatabaseReference.orderByKey();


        usersQuery.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<UserProfile> userProfiles = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    UserProfile profile = snapshot.getValue( UserProfile.class );

                    if (profile.getHobbyList() != null) {
                        for (String hobby : hobbies) {
                            if (profile.getHobbyList().contains( hobby )) {
                                userProfiles.add( profile );
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
        } );


    }

    public void getPostsFromUsers() {
        final UserProfile currentUser = DataStore.getInstance( getContext() ).getUser();
        if (editMode) {
            ArrayList<UserPost> postsToShowForUserEditMode = new ArrayList<>();
            postsToShowForUserEditMode = currentUser.getUserPostList();
            mAdapter.setUserPosts( postsToShowForUserEditMode );
            recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
            recyclerView.setAdapter( mAdapter );
        } else {
            final ArrayList<UserPost> tempPosts = new ArrayList<>();
            if (postsToShowForUser==null || InternetConnection.isNetworkAvailable(getContext())) {
                postsToShowForUser = new ArrayList<>();
            }
            else {
                mPostsList = postsToShowForUser;
                mAdapter.setUserPosts( postsToShowForUser );

                recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
                recyclerView.setAdapter( mAdapter );
                if (postsToShowForUser.size() == 0) {

                    relativeLayout.setVisibility( View.VISIBLE );

                }
            }
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference().child( "appUsers" );
            mDatabaseReference.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postsToShowForUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = snapshot.getValue( UserProfile.class );
                        if ((!userProfile.getUserToken().equals( currentUser.getUserToken() ) && userProfile.getUserPostList() != null)) {
                            tempPosts.addAll( userProfile.getUserPostList() );
                        }
                    }

                    for (UserPost post : tempPosts) {
                        if (currentUser.getHobbyList().contains( post.getHobby() )) {
                            postsToShowForUser.add( post );

                        }
                    }

                    // todo: set adapter

                    DataStore.getInstance( getContext() ).saveUserPostList( postsToShowForUser );
                    mPostsList = postsToShowForUser;
                    mAdapter.setUserPosts( postsToShowForUser );

                    recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
                    recyclerView.setAdapter( mAdapter );
                    if (postsToShowForUser.size() == 0) {

                        relativeLayout.setVisibility( View.VISIBLE );

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Handler().postDelayed( new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.setUserPosts( DataStore.getInstance( getContext() ).getPostList() );
//                recyclerView.setAdapter( new PostsRecyclerViewAdapter( DataStore.getInstance( getContext() ).getPostList(), getContext(), false ) );
//            }
//        }, 300 );

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    private void check() {
        getPostsFromUsers();
        dataViewModel = ViewModelProviders.of( this ).get( DataViewModel.class );
        dataViewModel.getAllPosts().observe( this, new Observer<List<UserPost>>() {
            @Override
            public void onChanged(@Nullable List<UserPost> userPosts) {
                mAdapter.notifyDataSetChanged();
            }
        } );
    }
}
