package com.yoyo.hobbyist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.himanshurawat.imageworker.Extension;
import com.himanshurawat.imageworker.ImageWorker;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.SignFragments.NewSignInScreenFragment;
import com.yoyo.hobbyist.SignFragments.SignButtonsFragment;
import com.yoyo.hobbyist.SignFragments.SignUpFragment;
import com.yoyo.hobbyist.SignFragments.SigninFragment;
import com.yoyo.hobbyist.SignFragments.UpdateUserProfileFragment;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.security.AccessController.getContext;

public class LoginSignUpActivity extends AppCompatActivity implements SignUpFragment.SignUpFragmentListener,
        NewSignInScreenFragment.LoginFragmentListener, UpdateUserProfileFragment.UpdateUserProfileFragmentListener {

    final int CAMERA_REQUEST = 1;
    final int WRITE_TO_EXTERNAL_PERMISSION_REQUEST = 2;
    private final int SELECT_IMAGE_REQ = 3;

    final String NEW_LOGIN_FRAGMENT_TAG = "new_sign_in_fragment";

    final String LOGIN_FRAGMENT_TAG = "sign_in_fragment";
    final String SIGN_UP_FRAGMENT_TAG = "sign_up_fragment";
    final String SING_BUTTONS_FRAGMENT_TAG = "sign_buttons_fragment";
    final String UPDATE_USER_FRAGMENT_TAG = "update_user_fragment";

    FirebaseAuth mFireBaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mCurrentUser;

    FragmentManager mFragmentManager;
    SigninFragment mLoginFragment;
    SignUpFragment mSignUpFragment;
    LottieAnimationView mLottieAnimationView;
    NewSignInScreenFragment mNewSignInScreenFragment;
    UpdateUserProfileFragment mUpdateUserProfileFragment;

    SharedPreferences mSp;
    SharedPreferences.Editor mSpEditor;

    String mEmail;
    String mPassword;

    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        mCoordinatorLayout = findViewById(R.id.login_signup_coordinatorLayout);
        mSp = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        mSpEditor = mSp.edit();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mEmail = mSp.getString("userEmail", "noEmail");
        mPassword = mSp.getString("userPassword", "noPassword");
        mCurrentUser = mFireBaseAuth.getCurrentUser();
        String[] hobbys ={
                "3D printing",
                "Acting",
                "Aeromodeling",
                "Air sports",
                "Airbrushing",
                "Aircraft Spotting",
                "Airsoft",
                "Airsofting",
                "Amateur astronomy",
                "Amateur geology",
                "Amateur Radio",
                "American football",
                "Animal fancy",
                "Animals/pets/dogs",
                "Antiquing",
                "Antiquities",
                "Aqua-lung",
                "Aquarium (Freshwater & Saltwater)",
                "Archery",
                "Art collecting",
                "Arts",
                "Association football",
                "Astrology",
                "Astronomy",
                "Audiophilia",
                "Australian rules football",
                "Auto audiophilia",
                "Auto racing",
                "Backgammon",
                "Backpacking",
                "Badminton",
                "Base Jumping",
                "Baseball",
                "Basketball",
                "Baton Twirling",
                "Beach Volleyball",
                "Beach/Sun tanning",
                "Beachcombing",
                "Beadwork",
                "Beatboxing",
                "Becoming A Child Advocate",
                "Beekeeping",
                "Bell Ringing",
                "Belly Dancing",
                "Bicycle Polo",
                "Bicycling",
                "Billiards",
                "Bird watching",
                "Birding",
                "Birdwatching",
                "Blacksmithing",
                "Blogging",
                "BMX",
                "Board games",
                "Board sports",
                "BoardGames",
                "Boating",
                "Body Building",
                "Bodybuilding",
                "Bonsai Tree",
                "Book collecting",
                "Bookbinding",
                "Boomerangs",
                "Bowling",
                "Boxing",
                "Brazilian jiu-jitsu",
                "Breakdancing",
                "Brewing Beer",
                "Bridge",
                "Bridge Building",
                "Bringing Food To The Disabled",
                "Building A House For Habitat For Humanity",
                "Building Dollhouses",
                "Bus spotting",
                "Butterfly Watching",
                "Button Collecting",
                "Cake Decorating",
                "Calligraphy",
                "Camping",
                "Candle making",
                "Canoeing",
                "Car Racing",
                "Card collecting",
                "Cartooning",
                "Casino Gambling",
                "Cave Diving",
                "Ceramics",
                "Cheerleading",
                "Chess",
                "Church/church activities",
                "Cigar Smoking",
                "Climbing",
                "Cloud Watching",
                "Coin Collecting",
                "Collecting",
                "Collecting Antiques",
                "Collecting Artwork",
                "Collecting Hats",
                "Collecting Music Albums",
                "Collecting RPM Records",
                "Collecting Sports Cards (Baseball, Football, Basketball, Hockey)",
                "Collecting Swords",
                "Color guard",
                "Coloring",
                "Comic book collecting",
                "Compose Music",
                "Computer activities",
                "Computer programming",
                "Conworlding",
                "Cooking",
                "Cosplay",
                "Cosplaying",
                "Couponing",
                "Crafts",
                "Crafts (unspecified)",
                "Creative writing",
                "Cricket",
                "Crochet",
                "Crocheting",
                "Cross-Stitch",
                "Crossword Puzzles",
                "Cryptography",
                "Curling",
                "Cycling",
                "Dance",
                "Dancing",
                "Darts",
                "Debate",
                "Deltiology (postcard collecting)",
                "Diecast Collectibles",
                "Digital arts",
                "Digital Photography",
                "Disc golf",
                "Do it yourself",
                "Dodgeball",
                "Dog sport",
                "Dolls",
                "Dominoes",
                "Dowsing",
                "Drama",
                "Drawing",
                "Driving",
                "Dumpster Diving",
                "Eating out",
                "Educational Courses",
                "Electronics",
                "Element collecting",
                "Embroidery",
                "Entertaining",
                "Equestrianism",
                "Exercise (aerobics, weights)",
                "Exhibition drill",
                "Falconry",
                "Fast cars",
                "Felting",
                "Fencing",
                "Field hockey",
                "Figure skating",
                "Fire Poi",
                "Fishing",
                "Fishkeeping",
                "Flag Football",
                "Floorball",
                "Floral Arrangements",
                "Flower arranging",
                "Flower collecting and pressing",
                "Fly Tying",
                "Flying",
                "Footbag",
                "Football",
                "Foraging",
                "Foreign language learning",
                "Fossil hunting",
                "Four Wheeling",
                "Freshwater Aquariums",
                "Frisbee Golf – Frolf",
                "Gambling",
                "Games",
                "Gaming (tabletop games and role-playing games)",
                "Garage Saleing",
                "Gardening",
                "Genealogy",
                "Geocaching",
                "Ghost hunting",
                "Glassblowing",
                "Glowsticking",
                "Gnoming",
                "Go",
                "Go Kart Racing",
                "Going to movies",
                "Golf",
                "Golfing",
                "Gongoozling",
                "Graffiti",
                "Grip Strength",
                "Guitar",
                "Gun Collecting",
                "Gunsmithing",
                "Gymnastics",
                "Gyotaku",
                "Handball",
                "Handwriting Analysis",
                "Hang gliding",
                "Herping",
                "Hiking",
                "Home Brewing",
                "Home Repair",
                "Home Theater",
                "Homebrewing",
                "Hooping",
                "Horse riding",
                "Hot air ballooning",
                "Hula Hooping",
                "Hunting",
                "Ice hockey",
                "Ice skating",
                "Iceskating",
                "Illusion",
                "Impersonations",
                "Inline skating",
                "Insect collecting",
                "Internet",
                "Inventing",
                "Jet Engines",
                "Jewelry Making",
                "Jigsaw Puzzles",
                "Jogging",
                "Judo",
                "Juggling",
                "Jukskei",
                "Jump Roping",
                "Kabaddi",
                "Kart racing",
                "Kayaking",
                "Keep A Journal",
                "Kitchen Chemistry",
                "Kite Boarding",
                "Kite flying",
                "Kites",
                "Kitesurfing",
                "Knapping",
                "Knife making",
                "Knife throwing",
                "Knitting",
                "Knotting",
                "Lacemaking",
                "Lacrosse",
                "Lapidary",
                "LARPing",
                "Laser tag",
                "Lasers",
                "Lawn Darts",
                "Learn to Play Poker",
                "Learning A Foreign Language",
                "Learning An Instrument",
                "Learning To Pilot A Plane",
                "Leather crafting",
                "Leathercrafting",
                "Lego building",
                "Legos",
                "Letterboxing",
                "Listening to music",
                "Locksport",
                "Machining",
                "Macramé",
                "Macrame",
                "Magic",
                "Mahjong",
                "Making Model Cars",
                "Marbles",
                "Marksmanship",
                "Martial arts",
                "Matchstick Modeling",
                "Meditation",
                "Metal detecting",
                "Meteorology",
                "Microscopy",
                "Mineral collecting",
                "Model aircraft",
                "Model building",
                "Model Railroading",
                "Model Rockets",
                "Modeling Ships",
                "Models",
                "Motor sports",
                "Motorcycles",
                "Mountain Biking",
                "Mountain Climbing",
                "Mountaineering",
                "Movie and movie memorabilia collecting",
                "Mushroom hunting/Mycology",
                "Musical Instruments",
                "Nail Art",
                "Needlepoint",
                "Netball",
                "Nordic skating",
                "Orienteering",
                "Origami",
                "Owning An Antique Car",
                "Paintball",
                "Painting",
                "Papermache",
                "Papermaking",
                "Parachuting",
                "Paragliding or Power Paragliding",
                "Parkour",
                "People Watching",
                "Photography",
                "Piano",
                "Pigeon racing",
                "Pinochle",
                "Pipe Smoking",
                "Planking",
                "Playing music",
                "Playing musical instruments",
                "Playing team sports",
                "Poker",
                "Pole Dancing",
                "Polo",
                "Pottery",
                "Powerboking",
                "Protesting",
                "Puppetry",
                "Puzzles",
                "Pyrotechnics",
                "Quilting",
                "R/C Boats",
                "R/C Cars",
                "R/C Helicopters",
                "R/C Planes",
                "Racing Pigeons",
                "Racquetball",
                "Radio-controlled car racing",
                "Rafting",
                "Railfans",
                "Rappelling",
                "Rapping",
                "Reading",
                "Reading To The Elderly",
                "Record collecting",
                "Relaxing",
                "Renaissance Faire",
                "Renting movies",
                "Rescuing Abused Or Abandoned Animals",
                "Robotics",
                "Rock balancing",
                "Rock climbing",
                "Rock Collecting",
                "Rockets",
                "Rocking AIDS Babies",
                "Roleplaying",
                "Roller derby",
                "Roller skating",
                "Rugby",
                "Rugby league football",
                "Running",
                "Sailing",
                "Saltwater Aquariums",
                "Sand art",
                "Sand Castles",
                "Scrapbooking",
                "Scuba diving",
                "Sculling or Rowing",
                "Sculpting",
                "Sea glass collecting",
                "Seashell collecting",
                "Self Defense",
                "Sewing",
                "Shark Fishing",
                "Shooting",
                "Shooting sport",
                "Shopping",
                "Shortwave listening",
                "Singing",
                "Singing In Choir",
                "Skateboarding",
                "Skeet Shooting",
                "Sketching",
                "Skiing",
                "Skimboarding",
                "Sky Diving",
                "Skydiving",
                "Slack Lining",
                "Slacklining",
                "Sleeping",
                "Slingshots",
                "Slot car racing",
                "Snorkeling",
                "Snowboarding",
                "Soap Making",
                "Soapmaking",
                "Soccer",
                "Socializing with friends/neighbors",
                "Speed Cubing (rubix cube)",
                "Speed skating",
                "Spelunkering",
                "Spending time with family/kids",
                "Sports",
                "Squash",
                "Stamp Collecting",
                "Stand-up comedy",
                "Stone collecting",
                "Stone skipping",
                "Storm Chasing",
                "Storytelling",
                "String Figures",
                "Sudoku",
                "Surf Fishing",
                "Surfing",
                "Survival",
                "Swimming",
                "Table football",
                "Table tennis",
                "Taekwondo",
                "Tai chi",
                "Tatting",
                "Taxidermy",
                "Tea Tasting",
                "Tennis",
                "Tesla Coils",
                "Tetris",
                "Textiles",
                "Texting",
                "Tombstone Rubbing",
                "Tool Collecting",
                "Tour skating",
                "Toy Collecting",
                "Train Collecting",
                "Train Spotting",
                "Trainspotting",
                "Traveling",
                "Treasure Hunting",
                "Trekkie",
                "Triathlon",
                "Tutoring Children",
                "TV watching",
                "Ultimate Frisbee",
                "Urban exploration",
                "Vehicle restoration",
                "Video game collecting",
                "Video Games",
                "Video gaming",
                "Videophilia",
                "Vintage cars",
                "Violin",
                "Volleyball",
                "Volunteer",
                "Walking",
                "Warhammer",
                "Watching movies",
                "Watching sporting events",
                "Water sports",
                "Weather Watcher",
                "Web surfing",
                "Weightlifting",
                "Windsurfing",
                "Wine Making",
                "Wingsuit Flying",
                "Wood carving",
                "Woodworking",
                "Working In A Food Pantry",
                "Working on cars",
                "World Record Breaking",
                "Worldbuilding",
                "Wrestling",
                "Writing",
                "Writing Music",
                "Writing Songs",
                "Yo-yoing",
                "Yoga",
                "YoYo",
                "Ziplining",
                "Zumba"
        };
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        if (mCurrentUser != null && !(mCurrentUser.getDisplayName().equals("null"))) {
            goToMainActivity();
        } else {
            mFireBaseAuth.signOut();
            mCurrentUser = mFireBaseAuth.getCurrentUser();
            mUpdateUserProfileFragment = new UpdateUserProfileFragment();
            mLottieAnimationView = findViewById(R.id.lottie_animation);
            mLottieAnimationView.setAnimation(R.raw.animation_test);
            mFragmentManager = getSupportFragmentManager();
            mLoginFragment = new SigninFragment();
            mSignUpFragment = new SignUpFragment();
            mNewSignInScreenFragment=new NewSignInScreenFragment();
            //add the new mainlogin
            mFragmentManager.beginTransaction().add(R.id.main_container, mNewSignInScreenFragment, NEW_LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void logIn() {
        mFireBaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mCurrentUser = mFireBaseAuth.getCurrentUser();
                } else {
                    Snackbar.make(mCoordinatorLayout, "Could not auto connect", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logIn();
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public void afterSignInUserUpdate(final FirebaseUser user) {
        if (user.getDisplayName().equals("null")) {
            callUpdateUser();
        } else {
            mCurrentUser = user;
            mLottieAnimationView.setVisibility(View.VISIBLE);
            mLottieAnimationView.playAnimation();
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    mFragmentManager.beginTransaction().commit();
                    mLottieAnimationView.setVisibility(View.GONE);
                    goToMainActivity();
                }
            };
            handler.postDelayed(runnable, 3100);
        }
    }

    @Override
    public void callSignUp() {
        mFragmentManager.beginTransaction().add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
                       .addToBackStack(null).commit();
    }

    @Override
    public void afterSignUpUserUpdate(FirebaseUser user) {
        mCurrentUser = user;
        //goToMainActivity();
        mFragmentManager.beginTransaction().add(R.id.main_container, mUpdateUserProfileFragment,
                UPDATE_USER_FRAGMENT_TAG).addToBackStack(null).commit();
        //mFragmentManager.beginTransaction().remove(mSignUpFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
    }

//    @Override
//    public void SignButtonsOnClick(int btnId) {
//        switch (btnId) {
//            case R.id.sign_in_btn: {
//                mFragmentManager.beginTransaction().remove(mSignBottnsFragment).add(R.id.main_container, mLoginFragment, LOGIN_FRAGMENT_TAG)
//                        .addToBackStack(null).commit();
//                break;
//            }
//            case R.id.sign_up_btn: {
//                mFragmentManager.beginTransaction().remove(mSignBottnsFragment).add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
//                        .addToBackStack(null).commit();
//                break;
//            }
//        }
//    }

    @Override
    public void afterUpdateUserUpdate(Boolean isUserUpdated) {
        if (isUserUpdated) {
            goToMainActivity();
        }
        if (!isUserUpdated) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }

        }
    }

    void callUpdateUser() {
        mFragmentManager.beginTransaction().add(R.id.main_container, mUpdateUserProfileFragment,
                UPDATE_USER_FRAGMENT_TAG)
                .addToBackStack(null).commit();
    }

    @Override
    public void updateImage(Intent intent, View view) {
        if(Build.VERSION.SDK_INT>=23) {
                callPermissions(intent);
        }
        else {
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment =mFragmentManager.findFragmentByTag(UPDATE_USER_FRAGMENT_TAG);
        assert fragment != null;
        ((UpdateUserProfileFragment) fragment).updateUserImage();
    }
    public void callPermissions(final Intent intent) {
        String[] string = { Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        PermissionHandler permissionHandler = new PermissionHandler() {
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions(intent);
            }

            @Override
            public void onGranted() {
                startActivityForResult(intent, 1);
            }
        };
        Permissions.check(this, string, "you must give those permissions to take a photo",options, permissionHandler);


    }

}
