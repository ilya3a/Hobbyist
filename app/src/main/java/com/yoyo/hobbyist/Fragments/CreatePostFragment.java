package com.yoyo.hobbyist.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.Adapters.PostsRecyclerViewAdapter;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;
import com.yoyo.hobbyist.Utilis.InternetConnection;
import com.yoyo.hobbyist.Utilis.UtilFuncs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostFragment extends DialogFragment {

    private CreatePostFragmentLisener createPostFragmentLisener;

    private AutoCompleteTextView mAutoCompleteTextView;
    private EditText mPostDescriptionEt;
    private TextView mUserNameTV;
    private Button mClosePost;
    private MaterialButton mCreatePost;
    private String mUserName;
    private String mCityName;
    private CircleImageView mProfilePicture;
    String mPictureUrl;
    AutoCompleteTextView mHobby;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFireBaseAuth;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private double mLatitude;
    private double mLongitude;

    File mFile;
    Boolean isPhotoExists = false;
    PostsRecyclerViewAdapter mAdapter;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<UserPost> mPostsList = new ArrayList<>();
    ArrayList<String> hobbylist = new ArrayList<>();
    final String API_TOKEN_KEY = "AAAAPp81o9o:APA91bFIxD-hnOSdPhGzCD7wGtE-of6OWANn44pQFapd1xSccxippK9IorvoK3jsGBn9RpMv8P6W44-Mo5X9itXu7Fntu0x3o5L01tsFjRQaNAueI2QFLQPwwxz-naOyXMOYfwSbnRoy";

    public CreatePostFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
        callPermissions();
        requestLocationUpdates();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFireBaseAuth.getCurrentUser();
        mAutoCompleteTextView = rootView.findViewById(R.id.chosen_hobby_name);
        mUserNameTV = rootView.findViewById(R.id.post_profile_name);
        mProfilePicture = rootView.findViewById(R.id.post_profile_image);
        mCreatePost = rootView.findViewById(R.id.create_post_btn);
        mDatabaseReference.child("appHobbys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            String hobby = (String) snapshot.getValue();
                            if (hobby.replace(" ", "").matches("[a-zA-Z0-9-_.~%]{1,900}")) {
                                hobbylist.add(hobby);
                            }
                            i++;
                        } catch (ClassCastException ex) {
                            Log.d("ilya", i + "");
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, hobbylist);
                    mAutoCompleteTextView.setAdapter(adapter);
                    addTextChangedListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final UserProfile userProfile = DataStore.getInstance(getContext()).getUser();

        mPictureUrl = userProfile.getmPictureUrl();
        mUserName = userProfile.getmName();
        mCityName = userProfile.getmCityName();
        if (getContext() != null) {
            if (!mPictureUrl.equals("")) {
                Glide.with(getContext()).load(mPictureUrl).thumbnail(0.4f).into(mProfilePicture);
            }
            if (!userProfile.getmGender().equals("Male") && mPictureUrl.equals("")) {
                Glide.with(getContext()).load(R.drawable.ic_avatar_woman).into(mProfilePicture);
            }
        }
        mUserNameTV.setText(mUserName);

        mClosePost = rootView.findViewById(R.id.close_post_dialog);
        mClosePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreatePostFragment.this.dismiss();
            }
        });
        mPostDescriptionEt = rootView.findViewById(R.id.post_description);


        mCreatePost.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               String chosenHobby = mAutoCompleteTextView.getText().toString();

                                               if (!TextUtils.isEmpty(chosenHobby) && hobbylist.contains(chosenHobby)) {
                                                   if (InternetConnection.isNetworkAvailable(getContext())) {


                                                       String currentDate = UtilFuncs.getCurrentDate();

                                                       String userToken = mFirebaseUser.getUid();


                                                       UserPost userPost = new UserPost(mPictureUrl, mUserName, mCityName, true, currentDate, mAutoCompleteTextView.getText().toString(),
                                                               userToken, mPostDescriptionEt.getText().toString(), mLatitude, mLongitude, userProfile.getmGender());

                                                       mDatabaseReference.child("usersPost").child(userPost.getHobby()).child(userToken).push().setValue(userPost);
                                                       if (userProfile.getmUserPostList() == null) {
                                                           ArrayList<UserPost> arrayList = new ArrayList<>();
                                                           userProfile.setmUserPostList(arrayList);
                                                       }
                                                       userProfile.getmUserPostList().add(userPost);
                                                       DataStore.getInstance(getContext()).saveUser(userProfile);
                                                       updateUserProfileOnFireBase(userProfile);
                                                       DataStore.getInstance(getContext()).saveUserPost(userPost);

                                                       /*
                                                        *   post message to all subscribed users
                                                        */
                                                        /*
                                                       https://fcm.googleapis.com/fcm/send
                                                        Content-Type:application/json
                                                        Authorization:key=AIzaSyZ-1u...0GBYzPu7Udno5aA
                                                        {
                                                            "to": "/topics/foo-bar", (OR:   "condition": "'dogs' in topics || 'cats' in topics",)
                                                          "data": {
                                                              "message": "This is a Firebase Cloud Messaging Topic Message!",
                                                          }
                                                          "notification":{
                                                                   "title":"Portugal vs. Denmark",
                                                                    "body":"great match!"
                                                          }
                                                       }
                                                         */

                                                       String textToSend = getString(R.string.check_out_new_activity_on) + userPost.getHobby();
                                                       try {
                                                           final JSONObject rootObject = new JSONObject();
                                                           rootObject.put("to", "/topics/" + userPost.getHobby().replace(" ", ""));
                                                           rootObject.put("notification", new JSONObject().put("title", textToSend));
                                                           rootObject.put("notification", new JSONObject().put("body", "postlist"));


//                                                           rootObject.put("data", new JSONObject().put("message", textToSend));

                                                           String url = "https://fcm.googleapis.com/fcm/send";

                                                           RequestQueue queue = Volley.newRequestQueue(getContext());
                                                           StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                               @Override
                                                               public void onResponse(String response) {

                                                               }
                                                           }, new Response.ErrorListener() {
                                                               @Override
                                                               public void onErrorResponse(VolleyError error) {

                                                               }
                                                           }) {

                                                               @Override
                                                               public Map<String, String> getHeaders() throws AuthFailureError {
                                                                   Map<String, String> headers = new HashMap<>();
                                                                   headers.put("Content-Type", "application/json");
                                                                   headers.put("Authorization", "key=" + API_TOKEN_KEY);
                                                                   return headers;
                                                               }

                                                               @Override
                                                               public byte[] getBody() throws AuthFailureError {
                                                                   return rootObject.toString().getBytes();
                                                               }
                                                           };
                                                           queue.add(request);
                                                           queue.start();


                                                       } catch (JSONException ex) {
                                                           ex.printStackTrace();
                                                       }


                                                       CreatePostFragment.this.dismiss();
                                                   } else {

                                                       Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                                                   }

                                               } else {

                                                   Toast.makeText(getContext(), getString(R.string.invalid_input_please_choose), Toast.LENGTH_SHORT).show();

                                               }
                                           }
                                       }
        );


        return rootView;
    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    private void addTextChangedListener() {
        mPostDescriptionEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence q, int s, int c, int a) {
            }

            public void onTextChanged(CharSequence q, int s, int b, int c) {

                int numOfLines;
                numOfLines = mPostDescriptionEt.getLineCount();
                if (numOfLines > 5) {
                    mPostDescriptionEt.getText().delete(mPostDescriptionEt.getSelectionEnd() - 1, mPostDescriptionEt.getSelectionStart());
                }
            }
        });
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int numOfLines;
                numOfLines = mAutoCompleteTextView.getLineCount();
                if (numOfLines > 2) {

                    mAutoCompleteTextView.getText().delete(mAutoCompleteTextView.getSelectionEnd() - 1, mAutoCompleteTextView.getSelectionStart());
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreatePostFragmentLisener) {
            createPostFragmentLisener = (CreatePostFragmentLisener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CreatePostFragmentLisener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        createPostFragmentLisener = null;
    }

    public interface CreatePostFragmentLisener {
        void postCreated();

    }

    private void requestLocationUpdates() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {

                Location lastLocation = locationResult.getLastLocation();
//                LatLng location = new LatLng( lastLocation.getLatitude(), lastLocation.getLongitude() );
                mLatitude = lastLocation.getLatitude();
                mLongitude = lastLocation.getLongitude();
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
    }

    private void callPermissions() {
        Permissions.check(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, R.string.location_pemission_nedded, new PermissionHandler() {
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

    public void updateUserProfileOnFireBase(UserProfile userProfile) {
        UtilFuncs.saveUserToFireBase(userProfile);
        mFirebaseUser.updateProfile(new UserProfileChangeRequest.Builder().build());
        DataStore.getInstance(getContext()).saveUser(userProfile);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.UP, enter, 1000);
        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 1000);
        }
    }
}
