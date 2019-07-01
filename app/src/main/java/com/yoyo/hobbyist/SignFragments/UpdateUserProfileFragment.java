package com.yoyo.hobbyist.SignFragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.flowlayout.FlowLayout;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.R;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpdateUserProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    UpdateUserProfileFragmentListener updateUserProfileFragmentListener;

    public interface UpdateUserProfileFragmentListener {
        void afterUpdateUserUpdate(Boolean isUserUpdated, String[] hobbies);

        void updateImage(Intent intent, View view);
    }

    public void updateUserImage() {
        final StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference( "images/river.jpg" );
        isPhotoExists = true;
        mPhotoCiv.setImageBitmap( BitmapFactory.decodeFile( mFile.getAbsolutePath() ) );
        Uri uri = Uri.fromFile( mFile );
        mStorageRef.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mPictureUrl = uri.toString();
                    }
                } );
                mStorageRef.child( "images/river.jpg" ).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText( getContext(), uri.toString(), Toast.LENGTH_LONG ).show();
                        Glide.with( getContext() ).load( uri ).into( mPhotoCiv );

                    }
                } );
            }
        } ).addOnFailureListener( new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getContext(), "sdfsdfsdfsdf", Toast.LENGTH_SHORT ).show();
            }
        } );

//        mFireBaseAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build());

    }

    String[] hobbys = {
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
            "Collecting Sports Cards",
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
            "Gaming ",
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
    String mName, mLastName, mAge, mCityName, mGender, mPictureUrl, mUid;
    TextInputLayout mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper, mDateOfBirthEtWrapper, mGenderEtWrapper;
    EditText mName_et, mLastNameEt, mCityNameEt, mGenderEt, mDateOfBirthEt;
    ArrayList<String> hobbyList;
    Button accept_btn,add_btn;
    FlowLayout flowLayout;
    AutoCompleteTextView mAutoCompleteTextView;
    TextView gender_click, birth_day_click;
    CircleImageView mPhotoCiv;
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private long lastTouchDown;
    private static int CLICK_ACTION_THRESHHOLD = 200;
    Boolean removeErrors_flag = false;
    File mFile;
    Uri imageUri;
    final String UPDATE_USER_FRAGMENT_TAG = "update_user_fragment";
    Boolean isPhotoExists = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate( R.layout.fragment_update_user_profile, container, false );
        mFireBaseAuth = FirebaseAuth.getInstance();

        mNameEtWrapper = rootView.findViewById( R.id.name_input_et_wrapper );
        mLastNameEtWrapper = rootView.findViewById( R.id.last_name_input_et_wrapper );
        mCityNameEtWrapper = rootView.findViewById( R.id.city_name_et_wrapper );
        mDateOfBirthEt = rootView.findViewById( R.id.date_of_birth_et );

        mName_et = rootView.findViewById( R.id.name_input_et );
        mLastNameEt = rootView.findViewById( R.id.last_name_input_et );
        mCityNameEt = rootView.findViewById( R.id.city_name_et );
        mDateOfBirthEt = rootView.findViewById( R.id.date_of_birth_et );
        mDateOfBirthEt.setInputType( InputType.TYPE_NULL );

        accept_btn = rootView.findViewById( R.id.accept_btn );
        add_btn=rootView.findViewById(R.id.add_btn);
        flowLayout=rootView.findViewById(R.id.flow_box);

        mFirebaseUser = mFireBaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mPhotoCiv = rootView.findViewById( R.id.photoCiv );

        mAutoCompleteTextView = rootView.findViewById( R.id.auto_complete_tv );
        hobbyList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_dropdown_item_1line, hobbys );
        mAutoCompleteTextView.setAdapter( adapter );
        mGender = "Male";
        final Spinner spinner = rootView.findViewById( R.id.spinner );
        final String[] gender = new String[]{
                "Male", "Female", "Other"
        };

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hobby=mAutoCompleteTextView.getEditableText().toString();
                if (!Arrays.asList(hobbys).contains(hobby)){
                    Snackbar.make(rootView, "Cant find the hobby", Snackbar.LENGTH_SHORT ).show();
                    mAutoCompleteTextView.setText("");
                }
                else
                {
                    if (hobbyList.contains(hobby))
                    {
                        Toast.makeText(getContext(), "You already choose this hobby " , Toast.LENGTH_SHORT).show();
                        mAutoCompleteTextView.setText("");
                    }
                    else {
                        TextView textView = new TextView(getContext());
                        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setText(hobby);
                        textView.setPadding(4, 4, 4, 4);
                        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTextSize(18);
                        textView.setBackground(getResources().getDrawable(R.drawable.label_bg2));
                        flowLayout.addView(textView);
                        mAutoCompleteTextView.setText("");
                        hobbyList.add(hobby);
                    }
                }
            }
        });

        final TextInputLayout[] allFields = {mNameEtWrapper, mLastNameEtWrapper, mCityNameEtWrapper};

        mPhotoCiv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                mFile = new File( Environment.getExternalStorageDirectory(), currentTime + "Hobbyist.jpg" );

                imageUri = FileProvider.getUriForFile( getContext(),
                        getActivity().getPackageName() + ".provider", //(use your app signature + ".provider" )
                        mFile );
                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                intent.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );
                updateUserProfileFragmentListener.updateImage( intent, mPhotoCiv );
            }
        } );
        View.OnTouchListener errorCancel = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (TextInputLayout edit : allFields) {
                    edit.setErrorEnabled( false );
                }
                return false;
            }
        };
        final DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDateOfBirthEt.clearFocus();
                if (removeErrors_flag) {
                    clearErrors( allFields );
                }
            }
        };
        mNameEtWrapper.setOnTouchListener( errorCancel );
        mLastNameEtWrapper.setOnTouchListener( errorCancel );
        mCityNameEtWrapper.setOnTouchListener( errorCancel );


        mDateOfBirthEt.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchDown = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {
                        Log.w( "App", "You clicked!" );
                        DatePickerDialog datePickerDialog = new DatePickerDialog( rootView.getContext(), UpdateUserProfileFragment.this,
                                Calendar.getInstance().get( Calendar.YEAR ),
                                Calendar.getInstance().get( Calendar.MONTH ),
                                Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
                        datePickerDialog.show();
                        //}
                        break;

                }
                if (removeErrors_flag) {
                    clearErrors( allFields );
                }
                return false;
            }
        } );
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGender = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        accept_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean continue_flag = true;
                ArrayList<TextInputLayout> ErrorFields = new ArrayList<>();
                for (TextInputLayout edit : allFields) {
                    if (TextUtils.isEmpty( edit.getEditText().getText().toString() )) {
                        // EditText was empty
                        continue_flag = false;
                        removeErrors_flag = true;
                        ErrorFields.add( edit );//add empty Edittext only in this ArayList
                        for (int i = ErrorFields.size() - 1; i >= 0; i--) {
                            TextInputLayout currentField = ErrorFields.get( i );
                            currentField.setError( getResources().getString( R.string.this_field_required ) );
                            currentField.requestFocus();
                        }
                    }
                    if (mDateOfBirthEt.getText().equals( "Birthday" )) {
                        continue_flag = false;
                        Snackbar.make( rootView, "Pick a Birthday", Snackbar.LENGTH_SHORT ).show();
                    }
                    if (hobbyList.isEmpty())
                    {
                        continue_flag=false;
                        Toast.makeText(getContext(), "add at least one hobby ", Toast.LENGTH_SHORT).show();
                    }
                    if (continue_flag) {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setmName( mNameEtWrapper.getEditText().getText().toString() )
                                .setmCityName( mCityNameEtWrapper.getEditText().getText().toString() )
                                .setmLastName( mLastNameEtWrapper.getEditText().getText().toString() )
                                .setmAge( mAge ).setmPictureUrl( "" )
                                .setmGender( mGender )
                                .setmHobbylist(hobbyList)
                                .setmUserToken( mUid );

                        if (isPhotoExists) {
                            userProfile.setmPictureUrl( mPictureUrl );
                        }
                        mDatabaseReference.child( "appUsers" ).child( userProfile.getmUserToken() ).setValue( userProfile );
                        mFirebaseUser.updateProfile( new UserProfileChangeRequest.Builder().setDisplayName( mNameEtWrapper.getEditText().getText().toString() + mLastNameEtWrapper.getEditText().getText().toString() ).build() );
                        DataStore.getInstance( getContext() ).saveUser( userProfile );
                        updateUserProfileFragmentListener.afterUpdateUserUpdate( true,hobbys );
                    }
                }
            }
        } );


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        try {
            updateUserProfileFragmentListener = (UpdateUserProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException( "Activity must implement the interface : updateUserProfileFragmentListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        mDateOfBirthEt.setText( date );
        Integer tempAge;
        tempAge = (getAge( year, month, dayOfMonth ));
        mAge = tempAge.toString();
    }


    // Returns age given the date of birth
    public static Integer getAge(int year, int month, int dayOfMonth) {
        Calendar today = Calendar.getInstance();

        int curYear = today.get( Calendar.YEAR );
        int dobYear = year;

        int age = curYear - dobYear;

        // if dob is month or day is behind today's month or day
        // reduce age by 1
        int curMonth = today.get( Calendar.MONTH );
        int dobMonth = month;
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get( Calendar.DAY_OF_MONTH );
            int dobDay = dayOfMonth;
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }
        return age;
    }

    void clearErrors(TextInputLayout[] allFields) {
        removeErrors_flag = false;
        for (TextInputLayout edit : allFields) {
            edit.setErrorEnabled( false );
        }
    }
}