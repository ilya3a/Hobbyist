package com.yoyo.hobbyist.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.Adapters.PostsRecyclerViewAdapter;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.MainActivity;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.SignFragments.UpdateUserProfileFragment;
import com.yoyo.hobbyist.Utilis.DataStore;
import com.yoyo.hobbyist.Utilis.InternetConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class CreatePostFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    private AutoCompleteTextView mAutoCompleteTextView;
    private EditText mPostDescriptionEt;
    private Button mClosePost, mCreatePost;
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
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("appHobbys");
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
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i =0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            String hobby = (String) snapshot.getValue();
                            hobbylist.add(hobby);
                            i++;
                        }catch (ClassCastException ex){
                            Log.d("ilya",i+"");
                        }
                    }
                    mAutoCompleteTextView = rootView.findViewById(R.id.chosen_hobby_name);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, hobbylist);
                    mAutoCompleteTextView.setAdapter(adapter);
                    addTextChangedListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        String[] hobbys = {
//                "3D printing",
//                "Acting",
//                "Aeromodeling",
//                "Air sports",
//                "Airbrushing",
//                "Aircraft Spotting",
//                "Airsoft",
//                "Airsofting",
//                "Amateur astronomy",
//                "Amateur geology",
//                "Amateur Radio",
//                "American football",
//                "Animal fancy",
//                "Animals/pets/dogs",
//                "Antiquing",
//                "Antiquities",
//                "Aqua-lung",
//                "Aquarium (Freshwater & Saltwater)",
//                "Archery",
//                "Art collecting",
//                "Arts",
//                "Association football",
//                "Astrology",
//                "Astronomy",
//                "Audiophilia",
//                "Australian rules football",
//                "Auto audiophilia",
//                "Auto racing",
//                "Backgammon",
//                "Backpacking",
//                "Badminton",
//                "Base Jumping",
//                "Baseball",
//                "Basketball",
//                "Baton Twirling",
//                "Beach Volleyball",
//                "Beach/Sun tanning",
//                "Beachcombing",
//                "Beadwork",
//                "Beatboxing",
//                "Becoming A Child Advocate",
//                "Beekeeping",
//                "Bell Ringing",
//                "Belly Dancing",
//                "Bicycle Polo",
//                "Bicycling",
//                "Billiards",
//                "Bird watching",
//                "Birding",
//                "Birdwatching",
//                "Blacksmithing",
//                "Blogging",
//                "BMX",
//                "Board games",
//                "Board sports",
//                "BoardGames",
//                "Boating",
//                "Body Building",
//                "Bodybuilding",
//                "Bonsai Tree",
//                "Book collecting",
//                "Bookbinding",
//                "Boomerangs",
//                "Bowling",
//                "Boxing",
//                "Brazilian jiu-jitsu",
//                "Breakdancing",
//                "Brewing Beer",
//                "Bridge",
//                "Bridge Building",
//                "Bringing Food To The Disabled",
//                "Building A House For Habitat For Humanity",
//                "Building Dollhouses",
//                "Bus spotting",
//                "Butterfly Watching",
//                "Button Collecting",
//                "Cake Decorating",
//                "Calligraphy",
//                "Camping",
//                "Candle making",
//                "Canoeing",
//                "Car Racing",
//                "Card collecting",
//                "Cartooning",
//                "Casino Gambling",
//                "Cave Diving",
//                "Ceramics",
//                "Cheerleading",
//                "Chess",
//                "Church/church activities",
//                "Cigar Smoking",
//                "Climbing",
//                "Cloud Watching",
//                "Coin Collecting",
//                "Collecting",
//                "Collecting Antiques",
//                "Collecting Artwork",
//                "Collecting Hats",
//                "Collecting Music Albums",
//                "Collecting RPM Records",
//                "Collecting Sports Cards (Baseball, Football, Basketball, Hockey)",
//                "Collecting Swords",
//                "Color guard",
//                "Coloring",
//                "Comic book collecting",
//                "Compose Music",
//                "Computer activities",
//                "Computer programming",
//                "Conworlding",
//                "Cooking",
//                "Cosplay",
//                "Cosplaying",
//                "Couponing",
//                "Crafts",
//                "Crafts (unspecified)",
//                "Creative writing",
//                "Cricket",
//                "Crochet",
//                "Crocheting",
//                "Cross-Stitch",
//                "Crossword Puzzles",
//                "Cryptography",
//                "Curling",
//                "Cycling",
//                "Dance",
//                "Dancing",
//                "Darts",
//                "Debate",
//                "Deltiology (postcard collecting)",
//                "Diecast Collectibles",
//                "Digital arts",
//                "Digital Photography",
//                "Disc golf",
//                "Do it yourself",
//                "Dodgeball",
//                "Dog sport",
//                "Dolls",
//                "Dominoes",
//                "Dowsing",
//                "Drama",
//                "Drawing",
//                "Driving",
//                "Dumpster Diving",
//                "Eating out",
//                "Educational Courses",
//                "Electronics",
//                "Element collecting",
//                "Embroidery",
//                "Entertaining",
//                "Equestrianism",
//                "Exercise (aerobics, weights)",
//                "Exhibition drill",
//                "Falconry",
//                "Fast cars",
//                "Felting",
//                "Fencing",
//                "Field hockey",
//                "Figure skating",
//                "Fire Poi",
//                "Fishing",
//                "Fishkeeping",
//                "Flag Football",
//                "Floorball",
//                "Floral Arrangements",
//                "Flower arranging",
//                "Flower collecting and pressing",
//                "Fly Tying",
//                "Flying",
//                "Footbag",
//                "Football",
//                "Foraging",
//                "Foreign language learning",
//                "Fossil hunting",
//                "Four Wheeling",
//                "Freshwater Aquariums",
//                "Frisbee Golf – Frolf",
//                "Gambling",
//                "Games",
//                "Gaming (tabletop games and role-playing games)",
//                "Garage Saleing",
//                "Gardening",
//                "Genealogy",
//                "Geocaching",
//                "Ghost hunting",
//                "Glassblowing",
//                "Glowsticking",
//                "Gnoming",
//                "Go",
//                "Go Kart Racing",
//                "Going to movies",
//                "Golf",
//                "Golfing",
//                "Gongoozling",
//                "Graffiti",
//                "Grip Strength",
//                "Guitar",
//                "Gun Collecting",
//                "Gunsmithing",
//                "Gymnastics",
//                "Gyotaku",
//                "Handball",
//                "Handwriting Analysis",
//                "Hang gliding",
//                "Herping",
//                "Hiking",
//                "Home Brewing",
//                "Home Repair",
//                "Home Theater",
//                "Homebrewing",
//                "Hooping",
//                "Horse riding",
//                "Hot air ballooning",
//                "Hula Hooping",
//                "Hunting",
//                "Ice hockey",
//                "Ice skating",
//                "Iceskating",
//                "Illusion",
//                "Impersonations",
//                "Inline skating",
//                "Insect collecting",
//                "Internet",
//                "Inventing",
//                "Jet Engines",
//                "Jewelry Making",
//                "Jigsaw Puzzles",
//                "Jogging",
//                "Judo",
//                "Juggling",
//                "Jukskei",
//                "Jump Roping",
//                "Kabaddi",
//                "Kart racing",
//                "Kayaking",
//                "Keep A Journal",
//                "Kitchen Chemistry",
//                "Kite Boarding",
//                "Kite flying",
//                "Kites",
//                "Kitesurfing",
//                "Knapping",
//                "Knife making",
//                "Knife throwing",
//                "Knitting",
//                "Knotting",
//                "Lacemaking",
//                "Lacrosse",
//                "Lapidary",
//                "LARPing",
//                "Laser tag",
//                "Lasers",
//                "Lawn Darts",
//                "Learn to Play Poker",
//                "Learning A Foreign Language",
//                "Learning An Instrument",
//                "Learning To Pilot A Plane",
//                "Leather crafting",
//                "Leathercrafting",
//                "Lego building",
//                "Legos",
//                "Letterboxing",
//                "Listening to music",
//                "Locksport",
//                "Machining",
//                "Macramé",
//                "Macrame",
//                "Magic",
//                "Mahjong",
//                "Making Model Cars",
//                "Marbles",
//                "Marksmanship",
//                "Martial arts",
//                "Matchstick Modeling",
//                "Meditation",
//                "Metal detecting",
//                "Meteorology",
//                "Microscopy",
//                "Mineral collecting",
//                "Model aircraft",
//                "Model building",
//                "Model Railroading",
//                "Model Rockets",
//                "Modeling Ships",
//                "Models",
//                "Motor sports",
//                "Motorcycles",
//                "Mountain Biking",
//                "Mountain Climbing",
//                "Mountaineering",
//                "Movie and movie memorabilia collecting",
//                "Mushroom hunting/Mycology",
//                "Musical Instruments",
//                "Nail Art",
//                "Needlepoint",
//                "Netball",
//                "Nordic skating",
//                "Orienteering",
//                "Origami",
//                "Owning An Antique Car",
//                "Paintball",
//                "Painting",
//                "Papermache",
//                "Papermaking",
//                "Parachuting",
//                "Paragliding or Power Paragliding",
//                "Parkour",
//                "People Watching",
//                "Photography",
//                "Piano",
//                "Pigeon racing",
//                "Pinochle",
//                "Pipe Smoking",
//                "Planking",
//                "Playing music",
//                "Playing musical instruments",
//                "Playing team sports",
//                "Poker",
//                "Pole Dancing",
//                "Polo",
//                "Pottery",
//                "Powerboking",
//                "Protesting",
//                "Puppetry",
//                "Puzzles",
//                "Pyrotechnics",
//                "Quilting",
//                "R/C Boats",
//                "R/C Cars",
//                "R/C Helicopters",
//                "R/C Planes",
//                "Racing Pigeons",
//                "Racquetball",
//                "Radio-controlled car racing",
//                "Rafting",
//                "Railfans",
//                "Rappelling",
//                "Rapping",
//                "Reading",
//                "Reading To The Elderly",
//                "Record collecting",
//                "Relaxing",
//                "Renaissance Faire",
//                "Renting movies",
//                "Rescuing Abused Or Abandoned Animals",
//                "Robotics",
//                "Rock balancing",
//                "Rock climbing",
//                "Rock Collecting",
//                "Rockets",
//                "Rocking AIDS Babies",
//                "Roleplaying",
//                "Roller derby",
//                "Roller skating",
//                "Rugby",
//                "Rugby league football",
//                "Running",
//                "Sailing",
//                "Saltwater Aquariums",
//                "Sand art",
//                "Sand Castles",
//                "Scrapbooking",
//                "Scuba diving",
//                "Sculling or Rowing",
//                "Sculpting",
//                "Sea glass collecting",
//                "Seashell collecting",
//                "Self Defense",
//                "Sewing",
//                "Shark Fishing",
//                "Shooting",
//                "Shooting sport",
//                "Shopping",
//                "Shortwave listening",
//                "Singing",
//                "Singing In Choir",
//                "Skateboarding",
//                "Skeet Shooting",
//                "Sketching",
//                "Skiing",
//                "Skimboarding",
//                "Sky Diving",
//                "Skydiving",
//                "Slack Lining",
//                "Slacklining",
//                "Sleeping",
//                "Slingshots",
//                "Slot car racing",
//                "Snorkeling",
//                "Snowboarding",
//                "Soap Making",
//                "Soapmaking",
//                "Soccer",
//                "Socializing with friends/neighbors",
//                "Speed Cubing (rubix cube)",
//                "Speed skating",
//                "Spelunkering",
//                "Spending time with family/kids",
//                "Sports",
//                "Squash",
//                "Stamp Collecting",
//                "Stand-up comedy",
//                "Stone collecting",
//                "Stone skipping",
//                "Storm Chasing",
//                "Storytelling",
//                "String Figures",
//                "Sudoku",
//                "Surf Fishing",
//                "Surfing",
//                "Survival",
//                "Swimming",
//                "Table football",
//                "Table tennis",
//                "Taekwondo",
//                "Tai chi",
//                "Tatting",
//                "Taxidermy",
//                "Tea Tasting",
//                "Tennis",
//                "Tesla Coils",
//                "Tetris",
//                "Textiles",
//                "Texting",
//                "Tombstone Rubbing",
//                "Tool Collecting",
//                "Tour skating",
//                "Toy Collecting",
//                "Train Collecting",
//                "Train Spotting",
//                "Trainspotting",
//                "Traveling",
//                "Treasure Hunting",
//                "Trekkie",
//                "Triathlon",
//                "Tutoring Children",
//                "TV watching",
//                "Ultimate Frisbee",
//                "Urban exploration",
//                "Vehicle restoration",
//                "Video game collecting",
//                "Video Games",
//                "Video gaming",
//                "Videophilia",
//                "Vintage cars",
//                "Violin",
//                "Volleyball",
//                "Volunteer",
//                "Walking",
//                "Warhammer",
//                "Watching movies",
//                "Watching sporting events",
//                "Water sports",
//                "Weather Watcher",
//                "Web surfing",
//                "Weightlifting",
//                "Windsurfing",
//                "Wine Making",
//                "Wingsuit Flying",
//                "Wood carving",
//                "Woodworking",
//                "Working In A Food Pantry",
//                "Working on cars",
//                "World Record Breaking",
//                "Worldbuilding",
//                "Wrestling",
//                "Writing",
//                "Writing Music",
//                "Writing Songs",
//                "Yo-yoing",
//                "Yoga",
//                "YoYo",
//                "Ziplining",
//                "Zumba"
//        };


        mProfilePicture = rootView.findViewById(R.id.post_profile_image);
        mClosePost = rootView.findViewById(R.id.close_post_dialog);
        mClosePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreatePostFragment.this.dismiss();
            }
        });
        mPostDescriptionEt = rootView.findViewById(R.id.post_description);

        mCreatePost = rootView.findViewById(R.id.create_post_btn);
        mCreatePost.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               if (!TextUtils.isEmpty(mAutoCompleteTextView.getText().toString())) {
                                                   if (InternetConnection.isNetworkAvailable(getContext())) {

                                                       UserProfile userProfile = DataStore.getInstance(getContext()).getUser();

                                                       mPictureUrl = userProfile.getmPictureUrl();
                                                       mUserName = userProfile.getmName();
                                                       mCityName = userProfile.getmCityName();

                                                       Calendar calendar = Calendar.getInstance();
                                                       String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                                                       String userToken = mFirebaseUser.getUid();


                                                       UserPost userPost = new UserPost(mPictureUrl, mUserName, mCityName, true, currentDate, mAutoCompleteTextView.getText().toString(),
                                                               userToken, mPostDescriptionEt.getText().toString(), mLatitude, mLongitude);

                                                       mDatabaseReference.child("usersPost").child(userPost.getHobby()).child(userToken).push().setValue(userPost);
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

                                                       String textToSend = "Check out new Activity on" + userPost.getHobby();
                                                       try {
                                                           final JSONObject rootObject = new JSONObject();
                                                           rootObject.put("to", "/topics/" + userPost.getHobby());
                                                           rootObject.put("notification",new JSONObject().put("title",textToSend));
                                                           rootObject.put("notification",new JSONObject().put("body",textToSend));


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
                                                   }

                                               } else {
//                                                    Snackbar.make( rootView, "Please check internet connection", Snackbar.LENGTH_SHORT );
                                                   Toast.makeText(getContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
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
                if (numOfLines > 1) {

                    mAutoCompleteTextView.getText().delete(mAutoCompleteTextView.getSelectionEnd() - 1, mAutoCompleteTextView.getSelectionStart());
                }
            }
        });
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
        void onFragmentInteraction(Uri uri);

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


}
