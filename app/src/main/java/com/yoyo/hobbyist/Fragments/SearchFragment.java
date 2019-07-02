package com.yoyo.hobbyist.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.R;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_FRAGMENT_TAG = "list";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap mMap;
    private SearchView searchView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private List<UserPost> mPostsAroundMeList = new ArrayList<>();
    TabItem tabItem1;
    TabItem tabItem2;
    TabLayout mTabLayout;

    FragmentManager mFragmentManager;
    SupportMapFragment mMapFragment;
    SearchListFragment mSearchListFragment;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

//        getChildFragmentManager().beginTransaction().add( R.id.search_fragment_child_container, mDashboardFragment, LIST_FRAGMENT_TAG ).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().beginTransaction().remove(mSearchListFragment).commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        mFragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
        mSearchListFragment = new SearchListFragment();
        mFragmentManager.beginTransaction().hide(mMapFragment).add(R.id.search_fragment_child_container, mSearchListFragment, LIST_FRAGMENT_TAG).commit();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mTabLayout = rootView.findViewById(R.id.search_tab_layout);
        tabItem1 = rootView.findViewById(R.id.dashboard);
        tabItem2 = rootView.findViewById(R.id.search);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mFragmentManager.beginTransaction().hide(mMapFragment).show(mSearchListFragment).commit();
                        break;
                    case 1:
                        mFragmentManager.beginTransaction().hide(mSearchListFragment).show(mMapFragment).commit();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        getPostsForUser();

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                    requestLocationUpdates();
                } else {
                    callPermissions();
                }
                loadPostsAroundMe(mPostsAroundMeList);
            }
        });
        mListener.onMapCreate();
        return rootView;

    }

    private void callPermissions() {
        Permissions.check(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, "Location permissions are required to get the Weather", new PermissionHandler() {
            @Override
            public void onGranted() {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                requestLocationUpdates();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions();
            }

        });
    }

    private void requestLocationUpdates() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {

                Location lastLocation = locationResult.getLastLocation();
                LatLng location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(location).title("current location"));
            }
        };

        LocationRequest request = LocationRequest.create();
        // get accuracy level
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // get update every...
        request.setInterval(1000);
        // the fastest update...
        request.setFastestInterval(500);
        request.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callPermissions();
        }
        mFusedLocationProviderClient.requestLocationUpdates(request, mLocationCallback, null);
        loadPostsAroundMe(mPostsAroundMeList);
    }

//    public void getPostsForUser() {
//
//        final ArrayList<UserPost> userPosts = new ArrayList<>();
//        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
//        ArrayList<DatabaseReference> dataBases = new ArrayList<>();
//
//
//        for (String hobby : DataStore.getInstance(getContext()).getUser().getmHobbylist()) {
//            dataBases.add(mFirebaseDatabase.getReference().child("usersPost").child(hobby));
//        }
//
//        for (DatabaseReference databaseReference : dataBases) {
//            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    ArrayList<DataSnapshot> snapshotArrayList = new ArrayList<>();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        if (!snapshot.getKey().equals(DataStore.getInstance(getContext()).getUser().getmUserToken())) {
//                            snapshotArrayList.add(snapshot);
//                        }
//                    }
//                    for (DataSnapshot snapshot : snapshotArrayList) {
//                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                            UserPost userPost =(UserPost)snapshot1.getValue();
//                            userPosts.add(userPost);
//                            //todo: to deal with the showing the posts
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//    }


    public void loadPostsAroundMe(List<UserPost> postsAroundMe) {

        for (int i = 0; i < postsAroundMe.size(); i++) {

        }
        for (UserPost userPost : postsAroundMe) {

//            mDatabaseReference.child( "usersPost" ).child( userPost.getUserToken() ).setValue( userPost );

            mMap.addMarker(new MarkerOptions().position(new LatLng(userPost.getLatitude(), userPost.getLongitude())).title("Marker in Sydney"));
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMapCreate();
    }
}
