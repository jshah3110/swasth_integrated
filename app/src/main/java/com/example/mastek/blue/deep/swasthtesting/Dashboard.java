package com.example.mastek.blue.deep.swasthtesting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    public static String JSON_ADDRESS = "";
    String TITLES[] = {"Home", "Language", "Tutorial", "About"};
    int ICONS[] = {R.drawable.rsz_2ic_home_black_24dp, R.mipmap.ic_launcher, R.drawable.rsz_ic_desktop_mac_black_24dp, R.drawable.rsz_ic_information_outline_black_24dp};
    String NAME = "";
    String CARD_NO = "";
    int PROFILE = R.drawable.ic_account_circle_black_24dp;
    LinearLayout btnFeedback;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ImageButton imageButton;
    Locale myLocale;
    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle
    TextView textView;
    private Feedback[] feedback;

    private UserLocalStore userLocalStore;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    ImageButton imageButtonCredits;

    private User user;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences editor = getSharedPreferences("lang_info", Context.MODE_PRIVATE);
        int selected = editor.getInt("key_lang", 0);
        selectLanguage(selected);
        setTitle(R.string.title_activity_dashboard);
        user = new User(getApplicationContext());

        setContentView(R.layout.activity_dashboard);

        NAME = user.getName();
        CARD_NO = Integer.toString(user.getCardNumber());
        JSON_ADDRESS = "http://swasth-india.esy.es/swasth/jsontest.php?choice=" + selected;

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(String.format("%s : %s", getResources().getString(R.string.card_no_textview), CARD_NO));
        //textView.setText(R.string.card_no_textview);
        //textView.setText(R.string.card_no_textview + ": " + CARD_NO);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        getJsonQuestions(selected);

        btnFeedback = (LinearLayout) findViewById(R.id.feedbackLinearLayout);
        btnFeedback.setOnClickListener(this);
        imageButton = (ImageButton) findViewById(R.id.barcode_image);
        imageButton.setOnClickListener(this);
        imageButtonCredits = (ImageButton) findViewById(R.id.imgCredits);
        imageButtonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = user.getCredits();
                Toast.makeText(getApplicationContext(),"Your total credits are: " + x,Toast.LENGTH_LONG).show();
            }
        });
        userLocalStore = new UserLocalStore(getApplicationContext());
        boolean status = userLocalStore.getLoginStatus();

        Intent intent = new Intent(Dashboard.this, MainActivity.class);

        if (!status) {
            Log.d("TEST", "SharedPref status" + status);
            startActivity(intent);
        } else {
            Log.d("TEST", "SharedPref Dashboard status" + status);
        }

//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);

//        ImageButton imageButton_helpline = (ImageButton) findViewById(R.id.helpline_image);
//        imageButton_helpline.setOnClickListener(this);


        ImageButton imageButton_centers = (ImageButton) findViewById(R.id.centers_image);
        imageButton_centers.setOnClickListener(this);

//        TextView textView_helpline = (TextView) findViewById(R.id.helpline_text);
//        textView_helpline.setOnClickListener(this);

        TextView textView_centers = (TextView) findViewById(R.id.centers_text);
        textView_centers.setOnClickListener(this);


//        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
//                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION);
//        }

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES, ICONS, NAME, CARD_NO, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(Dashboard.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                int i = recyclerView.getChildAdapterPosition(child);     //handling nav drawer onclick events
                switch (i) {
                    case 1:
                        Drawer.closeDrawers();
                        break;
                }

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    Toast.makeText(Dashboard.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    switch (recyclerView.getChildAdapterPosition(child)) {
                        case 2:
//                            Toast.makeText(Dashboard.this, "The Item Clicked is: language!", Toast.LENGTH_SHORT).show();
                            showLanguageDialogFragment();
                            break;
                        case 1:
                            break;
                        default:
                            break;
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

    }

    private void showLanguageDialogFragment() {
        new LanguageDialogFragment().show(getSupportFragmentManager(), "custom dialog");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
//            case R.id.helpline_image:
//                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9833151491"));
//                startActivity(intent1);
//                break;
//
//            case R.id.helpline_text:
//                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9833151491"));
//                startActivity(intent2);
//                break;

            case R.id.barcode_text:
            case R.id.barcode_image:
                Intent intent5 = new Intent(Dashboard.this, Barcode.class);
                startActivity(intent5);
                break;

            case R.id.centers_text:
            case R.id.centers_image:
                String map1 = "http://maps.google.co.in/maps?q=" + "Swasth Foundation";
                Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(map1));
                startActivity(intent3);
                break;

            case R.id.feedbackLinearLayout:
                Intent intent = new Intent(this, FeedbackActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArray("feedback", feedback);
                intent.putExtras(bundle);
                startActivity(intent);
//              finish();
                break;

        }

    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.sos:
//                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9833151491"));
//                startActivity(intent1);
//                break;
//
//        }
//        return false;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLocalStore = new UserLocalStore(getApplicationContext());
        boolean status = userLocalStore.getLoginStatus();

        Intent intent = new Intent(Dashboard.this, MainActivity.class);

        if (!status) {
            Log.d("TEST", "SharedPref status" + status);
            startActivity(intent);
        } else {
            Log.d("TEST", "SharedPref Dashboard status" + status);
        }
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        userLocalStore = new UserLocalStore(getApplicationContext());
//        boolean status = userLocalStore.getLoginStatus();
//
//        Intent intent = new Intent(Dashboard.this, MainActivity.class);
//
//        if (!status) {
//            Log.d("TEST", "SharedPref status" + status);
//            startActivity(intent);
//        } else {
//            Log.d("TEST", "SharedPref Dashboard status" + status);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        userLocalStore = new UserLocalStore(getApplicationContext());
        boolean status = userLocalStore.getLoginStatus();

        Intent intent = new Intent(Dashboard.this, MainActivity.class);

        user = new User(getApplicationContext());

        int credits;
        if (getIntent().getStringExtra("credits") != null) {
            credits = Integer.parseInt(getIntent().getStringExtra("credits"));
            Toast.makeText(getApplicationContext(), "Your Credits after feedback: " + credits, Toast.LENGTH_LONG).show();
        } else {
            //credits = user.getCredits();
            //Toast.makeText(getApplicationContext(), "Your Credits: OnStart " + credits, Toast.LENGTH_LONG).show();
        }


        if (!status) {
            Log.d("TEST", "SharedPref status" + status);
            startActivity(intent);
        } else {
            Log.d("TEST", "SharedPref Dashboard status" + status);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.sos:
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9833151491"));
                startActivity(intent1);
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
//            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            Log.i("TEST", "Logout Test");
            userLocalStore.clearUserData();
            userLocalStore.setLoggedInUser(false);
//          startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void selectLanguage(int position) {
        switch (position) {
            case 1:
                setLocale("hi");
                break;
            default:
                setLocale("en");
                break;
        }
    }

    public void setLocale(String lang) {
//        Configuration config = getBaseContext().getResources().getConfiguration();
//
//        myLocale = new Locale(lang);
//        Locale.setDefault(myLocale);
//        config.locale = myLocale;
//        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        Configuration config = getBaseContext().getResources().getConfiguration();

        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //onConfigurationChanged(config);  //not getting overridden
    }

    private void getJsonQuestions(final int choice) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_ADDRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("TEST", "Response..." + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error......." + error, Toast.LENGTH_LONG).show();
            }
        });

        FeedbackDownloadService service = FeedbackDownloadAdapter.getRetrofitBuilder();
        final Call<Feedback[]> fetchFeedback = service.fetchFeedback(choice);
        fetchFeedback.enqueue(new Callback<Feedback[]>() {
            @Override
            public void onResponse(retrofit.Response<Feedback[]> response, Retrofit retrofit) {
                feedback = response.body();
                //Log.i("mits ", feedback[1].question);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("choice", Integer.toString(choice));
//                return params;
//            }
//        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}