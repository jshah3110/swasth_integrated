package com.example.mastek.blue.deep.swasthtesting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String SERVER_ADDRESS = "http://swasth-india.esy.es/swasth/insert_medical_feedback.php";
    public static int credits = 0;
    SharedPreferences sharedPreferences;
    private int pos = 0;
    private boolean flag = false;
    private TextView progressText;
    private LinearLayout feedbackScrollViewLayout;
    private FeedbackAdapter feedbackAdapter;
    private Button previousButton;
    private RadioGroup optionsRadioGroup;
    private Map<String, String> hashMap;
    private ScrollView mainScrollView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        user = new User(getApplicationContext());
        progressText = (TextView) findViewById(R.id.progress_text);
        progressText.setText((pos + 1) + " of 8");

        Button nextButton = (Button) findViewById(R.id.nextButton);
        previousButton = (Button) findViewById(R.id.previousButton);
        //   mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);


        Bundle bundle = getIntent().getExtras();
        Parcelable[] parcelable = bundle.getParcelableArray("feedback");
        Feedback[] feedback = new Feedback[parcelable.length];
        System.arraycopy(parcelable, 0, feedback, 0, parcelable.length);
        //Log.i("TEST ", feedback[0].question);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        feedbackScrollViewLayout = (LinearLayout) findViewById(R.id.feedbackScrollViewLayout);
        feedbackAdapter = new FeedbackAdapter(this, feedback);

        feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
        previousButton.setVisibility(View.GONE);

        hashMap = new HashMap<>(feedback.length);
        optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);
        optionsRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {

//        mainScrollView.smoothScrollTo(0, 0);
//        mainScrollView.pageScroll(View.FOCUS_UP);
//
//        // Wait until my scrollView is ready
//        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Ready, move up
//                mainScrollView.fullScroll(View.FOCUS_UP);
//                mainScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });

        switch (v.getId()) {
            case R.id.nextButton:
                nextQuestion();
                break;
            case R.id.previousButton:
                previousQuestion();
                break;
        }
    }


    private void nextQuestion() {
        if (flag) {
            pos++;
            previousButton.setVisibility(View.VISIBLE);
            progressText.setText((pos + 1) + " of 8");
            if (pos >= feedbackAdapter.getCount()) {
                progressText.setText("8 of 8");

                final Map<String, String> sortedMap = new TreeMap<>(hashMap);
                //Toast.makeText(this, "Map is: " + sortedMap, Toast.LENGTH_LONG).show();
                pos = feedbackAdapter.getCount() - 1;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(getApplicationContext(), "Feedback Response......." + response, Toast.LENGTH_LONG).show();
                        Log.i("TEST", "Feedback Response......." + response);
                        if (!response.equals("Error")) {
                            credits = Integer.parseInt(response);
                            user.updateCredits(credits);
                            Log.i("TEST", "User Class Credits:" + user.getCredits());
                            Intent intent = new Intent(FeedbackActivity.this, Dashboard.class);
                            intent.putExtra("credits", response);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Thank you for the feedback.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("TEST", "Error:" + response);
                        }
//                    Intent intent = new Intent(FeedbackActivity.this, FeedbackSummary.class);
//                    intent.putExtra("summary",sortedMap.toString());
//                    startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Feedback Error......." + error, Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(),"Errror......." + error , Toast.LENGTH_LONG).show();
                        Log.i("TEST", "Feedback Error......." + error);

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return sortedMap;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

//                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra("map", sortedMap);
//                startActivity(intent);
                finish();
            } else {
                feedbackScrollViewLayout.removeViewAt(0);
                feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
                saveRadioState();
                if (!hashMap.containsKey(Integer.toString(pos + 1))) {
                    flag = false;
                }
            }
        } else {
            Toast.makeText(this, "Select a choice!", Toast.LENGTH_SHORT).show();
        }
    }

    private void previousQuestion() {
        pos--;

        if (pos == 0)
            previousButton.setVisibility(View.GONE);
        progressText.setText((pos + 1) + " of 8");
        if (pos < 0) {
            progressText.setText("1 of 8");
            Toast.makeText(this, "This is the first question!", Toast.LENGTH_SHORT).show();
            pos = 0;
        } else {
            feedbackScrollViewLayout.removeViewAt(0);
            feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));

            saveRadioState();
            flag = true;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        flag = true;
        switch (checkedId) {
            case R.id.option1:
                hashMap.put(Integer.toString(pos + 1), "1");
                //Toast.makeText(this, "Hashmap: " + hashMap, Toast.LENGTH_LONG).show();
                break;
            case R.id.option2:
                //Toast.makeText(this, "Opt 2", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "2");
                break;
            case R.id.option3:
                //Toast.makeText(this, "Opt 3", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "3");
                break;
            case R.id.option4:
                //Toast.makeText(this, "Opt 4", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "4");
                break;
            case R.id.option5:
                //Toast.makeText(this, "Opt 5", Toast.LENGTH_SHORT).show();
                hashMap.put(Integer.toString(pos + 1), "5");
                break;
        }
    }

    private void saveRadioState() {
        //loads saved value of radio button and makes it accept change in radio value
        optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);

        String val = hashMap.get(Integer.toString(pos + 1));
        if (val != null) {
            if (Integer.parseInt(val) == 1)
                optionsRadioGroup.check(R.id.option1);
            else if (Integer.parseInt(val) == 2)
                optionsRadioGroup.check(R.id.option2);
            else if (Integer.parseInt(val) == 3)
                optionsRadioGroup.check(R.id.option3);
            else if (Integer.parseInt(val) == 4)
                optionsRadioGroup.check(R.id.option4);
            else
                optionsRadioGroup.check(R.id.option5);
        }
        optionsRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        pos--;
        if (pos < 0) {
            finish();
        } else if (pos >= 0) {
            if (pos == 0)
                previousButton.setVisibility(View.GONE);

            progressText.setText((pos + 1) + " of 8");
            feedbackScrollViewLayout.removeViewAt(0);
            feedbackScrollViewLayout.addView(feedbackAdapter.getView(pos, null, feedbackScrollViewLayout));
            saveRadioState();
            flag = true;
        }
    }
}