package com.nlrd.socialevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BOKSIC on 01/02/2017.
 */

public class TweetAdapter extends BaseAdapter {
    ArrayList<Tweet> tweetList;
    Context context;

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList) {
        this.tweetList = tweetList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tweetList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
        }

        Tweet tweet = tweetList.get(position);

        return convertView;
    }
}
