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
    DashboardFragment mDashboardFragment;

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().beginTransaction().remove(mDashboardFragment).commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        mFragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
        mDashboardFragment = new DashboardFragment();
        mFragmentManager.beginTransaction().hide(mMapFragment).add(R.id.search_fragment_child_container, mDashboardFragment, LIST_FRAGMENT_TAG).commit();


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
                        mFragmentManager.beginTransaction().hide(mMapFragment).show(mDashboardFragment).commit();
                        break;
                    case 1:
                        mFragmentManager.beginTransaction().hide(mDashboardFragment).show(mMapFragment).commit();
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

        UserPost userPost = new UserPost("A", "B", "C", "D", "E", true, "F", 37.0667, 38.7667);
        UserPost userPost1 = new UserPost("AA", "B", "C", "D", "E", true, "F", 31.0667, 39.7667);
        UserPost userPost2 = new UserPost("AAA", "B", "C", "D", "E", true, "F", 32.0667, 37.7667);
        UserPost userPost3 = new UserPost("AAA", "B", "C", "D", "E", true, "F", 33.0667, 35.7667);
        UserPost userPost4 = new UserPost("AAA", "B", "C", "D", "E", true, "F", 34.0667, 33.7667);
        UserPost userPost5 = new UserPost("AAA", "B", "C", "D", "E", true, "F", 35.0667, 31.7667);

        mPostsAroundMeList.add(userPost);
        mPostsAroundMeList.add(userPost1);
        mPostsAroundMeList.add(userPost2);
        mPostsAroundMeList.add(userPost3);
        mPostsAroundMeList.add(userPost4);
        mPostsAroundMeList.add(userPost5);


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


    public void loadPostsAroundMe(List<UserPost> postsAroundMe) {

        for (int i = 0; i < postsAroundMe.size(); i++) {

        }
        for (UserPost userPost : postsAroundMe) {

            mDatabaseReference.child("usersPost").child(userPost.getUserToken()).setValue(userPost);

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
