package com.rcdev.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.objects.TrailerItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class TrailerAdapter extends ArrayAdapter<TrailerItem> {


    private Context mContext;
    private int layoutResourceId;
    private ArrayList<TrailerItem> mTrailers = new ArrayList<>();


    public TrailerAdapter(Context mContext, int layoutResourceId,  ArrayList<TrailerItem> mTrailers) {
        super(mContext, 0, mTrailers);
        this.mContext = mContext;
        this.layoutResourceId = layoutResourceId;
        this.mTrailers = mTrailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;
        TrailerItem trailerData = mTrailers.get(position);


        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            viewHolder = new ViewHolder(row);
            viewHolder.tvTitle = (TextView) row.findViewById(R.id.tvTrailerTitle);
            viewHolder.tvTitle.setText(trailerData.getName());
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        return row;
    }


    static class ViewHolder {
        @Bind(R.id.tvTrailerTitle)
        TextView tvTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
