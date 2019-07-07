package com.yoyo.hobbyist.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_FRAGMENT_TAG = "list";

    private static final String ARG_PARAM2 = "param2";
    private static final float DEFAULT_ZOOM = 12f;


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    TabItem tabItem1;
    TabItem tabItem2;
    TabLayout mTabLayout;

    ArrayList<UserPost> userPosts = new ArrayList<>();
    FragmentManager mFragmentManager;
    SupportMapFragment mMapFragment;
    SearchListFragment mSearchListFragment;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private onSearchFragmentListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu( menu, inflater );
        inflater.inflate( R.menu.main, menu );
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchListFragment = new SearchListFragment();
        getChildFragmentManager().beginTransaction().add( R.id.search_fragment_child_container, mSearchListFragment, LIST_FRAGMENT_TAG ).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().beginTransaction().remove( mSearchListFragment ).commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.search_fragment, container, false );
        mFragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById( R.id.map );

        mFragmentManager.beginTransaction().hide( mMapFragment ).commit();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mTabLayout = rootView.findViewById( R.id.search_tab_layout );
        tabItem1 = rootView.findViewById( R.id.search_list_of_posts_tab );
        tabItem2 = rootView.findViewById( R.id.search_map_of_posts_tab );
        mTabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_list_posts_selected);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_map_not_selected);
                        mFragmentManager.beginTransaction().hide( mMapFragment ).show( mSearchListFragment ).commit();
                        break;
                    case 1:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_list_posts_not_selected);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_map_selected);
                        mFragmentManager.beginTransaction().hide( mSearchListFragment ).show( mMapFragment ).commit();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (tab.getPosition() == 1) {
                    mListener.onSwishedTab();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );

        mMapFragment.getMapAsync( new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                getDeviseLocation();
                if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled( true );
                mMap.setOnInfoWindowClickListener( SearchFragment.this );
                if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PermissionChecker.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
                    requestLocationUpdates();
                } else {
                    callPermissions();
                }
            }
        } );

        getPostsFromUsers();

        return rootView;
    }

    private void callPermissions() {
        Permissions.check( getContext(), Manifest.permission.ACCESS_FINE_LOCATION, "Location permissions are required to get the location", new PermissionHandler() {
            @Override
            public void onGranted() {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
                requestLocationUpdates();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied( context, deniedPermissions );
                callPermissions();
            }

        } );
    }

    private void requestLocationUpdates() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {

            }
        };

        LocationRequest request = LocationRequest.create();
        // get accuracy level
        request.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        // get update every...
        request.setInterval( 1000 );
        // the fastest update...
        request.setFastestInterval( 500 );
        request.setNumUpdates( 1 );

        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            callPermissions();
        }
        mFusedLocationProviderClient.requestLocationUpdates( request, mLocationCallback, null );

    }

    public void getPostsFromUsers() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child( "appUsers" );
        final ArrayList<UserPost> tempPosts = new ArrayList<>();
        final ArrayList<UserPost> postsToShowForUser = new ArrayList<>();
        final UserProfile currentUser = DataStore.getInstance( getContext() ).getUser();
        mDatabaseReference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = snapshot.getValue( UserProfile.class );
                    if ((!userProfile.getmUserToken().equals( currentUser.getmUserToken() ) && userProfile.getmUserPostList() != null)) {
                        tempPosts.addAll( userProfile.getmUserPostList() );
                    }
                }

                for (UserPost post : tempPosts) {
                    if (currentUser.getmHobbylist().contains( post.getHobby() )) {
                        postsToShowForUser.add( post );
                        LatLng location = new LatLng( post.getLatitude(), post.getLongitude() );
                        mMap.addMarker( new MarkerOptions().position( location ).title( post.getHobby() ) );

                    }
                }
                userPosts.addAll( postsToShowForUser );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int markerIndex = Integer.parseInt( marker.getId().replace( "m", "" ) );
        mListener.onMarkerInfoClicked( userPosts.get( markerIndex ) );

    }


    public interface onSearchFragmentListener {
        void onMarkerInfoClicked(UserPost userPost);

        void onSwishedTab();

    }

    private void getDeviseLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener( new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    moveCamera( new LatLng( currentLocation.getLatitude(),currentLocation.getLongitude()), DEFAULT_ZOOM );
                } else {
                    Toast.makeText( getContext(), "Unable to get location", Toast.LENGTH_SHORT ).show();
                }

            }
        } );
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, zoom ) );
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof onSearchFragmentListener) {
            mListener = (onSearchFragmentListener) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnPostForMapListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
