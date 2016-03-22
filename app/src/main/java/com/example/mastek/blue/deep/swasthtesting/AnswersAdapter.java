package com.example.mastek.blue.deep.swasthtesting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class AnswersAdapter extends BaseAdapter {
    public LayoutInflater mLayoutInflater;
    Answers mAnswersCollection;

    public AnswersAdapter(FeedbackActivity feedbackActivity, Answers answersCollection) {
        mLayoutInflater = LayoutInflater.from(feedbackActivity);
        mAnswersCollection = answersCollection;
    }

    @Override
    public int getCount() {
        return mAnswersCollection.answers.length;
    }

    @Override
    public Object getItem(int position) {
        return mAnswersCollection.answers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.answer_layout, parent, false);
        }
        RadioButton option1 = (RadioButton) convertView.findViewById(R.id.option1);
        RadioButton option2 = (RadioButton) convertView.findViewById(R.id.option2);
        RadioButton option3 = (RadioButton) convertView.findViewById(R.id.option3);
        RadioButton option4 = (RadioButton) convertView.findViewById(R.id.option4);
        RadioButton option5 = (RadioButton) convertView.findViewById(R.id.option5);

        Answer answer = mAnswersCollection.answers[position];

        if (answer.option1.equals("-"))
            option1.setVisibility(View.GONE);
        else
            option1.setText(mAnswersCollection.answers[position].option1);

        if (answer.option2.equals("-"))
            option2.setVisibility(View.GONE);
        else
            option2.setText(mAnswersCollection.answers[position].option2);

        if (answer.option3.equals("-"))
            option3.setVisibility(View.GONE);
        else
            option3.setText(mAnswersCollection.answers[position].option3);

        if (answer.option4.equals("-"))
            option4.setVisibility(View.GONE);
        else
            option4.setText(mAnswersCollection.answers[position].option4);

        if (answer.option5.equals("-"))
            option5.setVisibility(View.GONE);
        else
            option5.setText(mAnswersCollection.answers[position].option5);

        return convertView;
    }
}
