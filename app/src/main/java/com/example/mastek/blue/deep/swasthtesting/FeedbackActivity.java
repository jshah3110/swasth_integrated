package com.example.mastek.blue.deep.swasthtesting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private int pos = 0;
    private boolean flag = false;
    private TextView progressText;
    private LinearLayout feedbackLinearLayout;
    private LinearLayout answerLinearLayout;
    private QuestionsAdapter questionsAdapter;
    private AnswersAdapter answersAdapter;
    private RadioGroup optionsRadioGroup;
    private Map<String, String> hashMap;
    private ScrollView mainScrollView;
    public static int credits = 0;
    private User user;
    SharedPreferences sharedPreferences;
    public static final String SERVER_ADDRESS = "http://swasth-india.esy.es/swasth/insert_medical_feedback.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        user = new User(getApplicationContext());
        progressText = (TextView)findViewById(R.id.progress_text);
        progressText.setText((pos+1)+" of 8");

        Button nextButton = (Button) findViewById(R.id.nextButton);
        Button previousButton = (Button) findViewById(R.id.previousButton);
//        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);

        if(pos == 0){
            previousButton.setVisibility(View.GONE);
        }

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        try {
            InputStream inputStream1 = getAssets().open("feedback.json");
            String response1 = IOUtils.toString(inputStream1);

            InputStream inputStream2 = getAssets().open("feedbackanswers.json");
            String response2 = IOUtils.toString(inputStream2);

            Gson gson1 = new GsonBuilder().create();
            Questions feedbackCollection = gson1.fromJson(response1, Questions.class);

            Answers answersCollection = gson1.fromJson(response2, Answers.class);

//            Toast.makeText(this, feedbackCollection.questions[0].question, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, String.valueOf(answersCollection.answers.length), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, answersCollection.answers[0].option1, Toast.LENGTH_SHORT).show();

            feedbackLinearLayout = (LinearLayout) findViewById(R.id.feedbackLinearLayout);
            questionsAdapter = new QuestionsAdapter(this, feedbackCollection);

            answerLinearLayout = (LinearLayout) findViewById(R.id.answerScrollViewLayout);
            answersAdapter = new AnswersAdapter(this, answersCollection);
            feedbackLinearLayout.addView(questionsAdapter.getView(pos, null, feedbackLinearLayout));
            answerLinearLayout.addView(answersAdapter.getView(pos, null, answerLinearLayout));

            hashMap = new HashMap<>(feedbackCollection.questions.length);
            optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);
            optionsRadioGroup.setOnCheckedChangeListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        //mainScrollView.smoothScrollTo(0, 0);
        //mainScrollView.pageScroll(View.FOCUS_UP);

        // Wait until my scrollView is ready
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
            Button previousButton = (Button) findViewById(R.id.previousButton);
            previousButton.setVisibility(View.VISIBLE);
            progressText.setText((pos+1)+" of 8");
            if (pos >= questionsAdapter.getCount()) {
                progressText.setText("8 of 8");

                final Map<String, String> sortedMap = new TreeMap<>(hashMap);
                Toast.makeText(this, "Map is: " + sortedMap, Toast.LENGTH_LONG).show();
                pos = questionsAdapter.getCount() - 1;
                 StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_ADDRESS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(),"Feedback Response......." + response , Toast.LENGTH_LONG).show();
                    Log.i("TEST", "Feedback Response......." + response);
                    if(!response.equals("Error")){
                        credits = Integer.parseInt(response);
                        user.updateCredits(credits);
                        Log.i("TEST","User Class Credits:" + user.getCredits());
                        Intent intent = new Intent(FeedbackActivity.this, Dashboard.class);
                        intent.putExtra("credits",response);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Thank you for the feedback.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i("TEST","Error:"  + response);
                    }
//                    Intent intent = new Intent(FeedbackActivity.this, FeedbackSummary.class);
//                    intent.putExtra("summary",sortedMap.toString());
//                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Feedback Error......." + error , Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"Errror......." + error , Toast.LENGTH_LONG).show();
                    Log.i("TEST", "Feedback Error......." + error);

                }
            }){
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
//                finish();
            } else {
                feedbackLinearLayout.removeViewAt(0);
                answerLinearLayout.removeViewAt(0);
                feedbackLinearLayout.addView(questionsAdapter.getView(pos, null, feedbackLinearLayout));
                answerLinearLayout.addView(answersAdapter.getView(pos, null, answerLinearLayout));
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
        progressText.setText((pos+1)+" of 8");
        if (pos < 0) {
            progressText.setText("1 of 8");
            pos = 0;
        }
//        else if(pos == 0){
//            Button previousButton = (Button) findViewById(R.id.previousButton);
//            previousButton.setVisibility(View.GONE);
//        }
        else {
            feedbackLinearLayout.removeViewAt(0);
            answerLinearLayout.removeViewAt(0);
            feedbackLinearLayout.addView(questionsAdapter.getView(pos, null, feedbackLinearLayout));
            answerLinearLayout.addView(answersAdapter.getView(pos, null, answerLinearLayout));

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
        if(pos == 0){
//            startActivity(new Intent(FeedbackActivity.this, Dashboard.class));
            progressText.setText("1 of 8");
            Button previousButton = (Button) findViewById(R.id.previousButton);
            previousButton.setVisibility(View.GONE);
            //saveRadioState();
            finish();
        }
//        else if(pos == 1){
//            Button previousButton = (Button) findViewById(R.id.previousButton);
//            previousButton.setVisibility(View.GONE);
//        }
        else
            pos--;
            progressText.setText((pos+1)+" of 8");
            feedbackLinearLayout.removeViewAt(0);
            answerLinearLayout.removeViewAt(0);
            feedbackLinearLayout.addView(questionsAdapter.getView(pos, null, feedbackLinearLayout));
            answerLinearLayout.addView(answersAdapter.getView(pos, null, answerLinearLayout));
            saveRadioState();
            flag = true;
    }
}
