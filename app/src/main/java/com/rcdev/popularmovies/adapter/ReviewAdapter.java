package com.rcdev.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.objects.ReviewItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {


    private Context mContext;
    private int layoutResourceId;
    private ArrayList<ReviewItem> mReviews = new ArrayList<ReviewItem>();


    public ReviewAdapter(Context mContext, int layoutResourceId,  ArrayList<ReviewItem> mReviews) {
        super(mContext, layoutResourceId, mReviews);
        this.mContext = mContext;
        this.layoutResourceId = layoutResourceId;
        this.mReviews = mReviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;
        ReviewItem reviewData = mReviews.get(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            viewHolder = new ViewHolder(row);
            viewHolder.tvAuthor.setText(reviewData.getAuthor());
            viewHolder.tvContent.setText(reviewData.getContent());
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        return row;
    }


    static class ViewHolder {
        @Bind(R.id.tvAuthor)
        TextView tvAuthor;
        @Bind(R.id.tvContent)
        TextView tvContent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
