package com.rcdev.popularmovies.activity;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.data.MovieContract;

/**
 * Created by coreyestes
 * 2016 02 16
 */
public class FavoriteActivity extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Cursor mDetailCursor;
    private ImageView backdrop;
    private TextView movie_title;
    private TextView movie_release;
    private TextView movie_rating;
    private TextView movie_overview;
    private ListView trailer;
    private ListView review;
    private Cursor mCursor;
    private int mPosition;

    private Uri mUri;
    private static final int CURSOR_LOADER_ID = 0;

    public static FavoriteActivity newInstance(int position, Uri uri) {
        FavoriteActivity fragment = new FavoriteActivity();
        Bundle args = new Bundle();
        fragment.mPosition = position;
        fragment.mUri = uri;
        args.putInt("id", position);
        fragment.setArguments(args);
        return fragment;
    }

    public FavoriteActivity() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (args != null) {
            selection = MovieContract.FavoriteEntry._ID;
            selectionArgs = new String[]{String.valueOf(mPosition)};
        }
        return new CursorLoader(getActivity(),
                mUri,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        backdrop = (ImageView) rootView.findViewById(R.id.ivBackdrop);
        movie_title = (TextView) rootView.findViewById(R.id.tvTitle);
        movie_release = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        movie_rating = (TextView) rootView.findViewById(R.id.tvRating);
        movie_overview = (TextView) rootView.findViewById(R.id.tvOverview);
        trailer = (ListView) rootView.findViewById(R.id.lvTrailers);
        review = (ListView) rootView.findViewById(R.id.lvReviews);

        Bundle args = this.getArguments();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, FavoriteActivity.this);

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);
        backdrop.setImageResource(mDetailCursor.getInt(4));
        movie_title.setText(mDetailCursor.getString(2));
        movie_release.setText(mDetailCursor.getString(8));
        movie_rating.setText(mDetailCursor.getString(7));
        movie_overview.setText(mDetailCursor.getString(6));

    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
