package com.example.mastek.blue.deep.swasthtesting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestionsAdapter extends BaseAdapter {
    public LayoutInflater mLayoutInflater;
    Questions mFeedbackCollection;

    public QuestionsAdapter(FeedbackActivity feedbackActivity, Questions feedbackCollection) {
        mLayoutInflater = LayoutInflater.from(feedbackActivity);
        mFeedbackCollection = feedbackCollection;
    }

    @Override
    public int getCount() {
        return mFeedbackCollection.questions.length;
    }

    @Override
    public Object getItem(int position) {
        return mFeedbackCollection.questions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.question_layout, parent, false);
        }
        TextView feedbackQuestion = (TextView) convertView.findViewById(R.id.feedbackQuestion);
        feedbackQuestion.setText(null);
        String question = mFeedbackCollection.questions[position].question;
        feedbackQuestion.setText(String.format("(%d.) %s", (position + 1), question));
//        feedbackQuestion.setText("Q" + (position + 1) + ") " + question);

        return convertView;
    }
}
