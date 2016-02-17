package com.rcdev.popularmovies.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rcdev.popularmovies.R;
import com.rcdev.popularmovies.adapter.ReviewAdapter;
import com.rcdev.popularmovies.adapter.TrailerAdapter;
import com.rcdev.popularmovies.data.MovieContract;
import com.rcdev.popularmovies.objects.ReviewItem;
import com.rcdev.popularmovies.objects.TrailerItem;
import com.rcdev.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class DetailActivityFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    protected String id;
    protected String title;
    protected String thumb;
    protected String mPoster;
    protected String backdrop;
    protected String release;
    protected String rating;
    protected String overview;
    protected Button button;

    private ArrayList<TrailerItem> mTrailerData;
    private ArrayList<ReviewItem> mReviewData;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers", mTrailerData);
        outState.putParcelableArrayList("reviews", mReviewData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey("trailers") || !savedInstanceState.containsKey("reviews")) {
            mTrailerData = new ArrayList<>();
            mReviewData = new ArrayList<>();

        } else {
            mTrailerData = savedInstanceState.getParcelableArrayList("trailers");
            mReviewData = savedInstanceState.getParcelableArrayList("reviews");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        detailView.findViewById(R.id.btFavorite).setOnClickListener(this);

        if (intent != null && intent.hasExtra(Constants.ID)) {
            id = intent.getStringExtra(Constants.ID);
            Log.i(LOG_TAG, "movie id is: " + id);
        }

        if (intent != null && intent.hasExtra(Constants.TITLE)) {
            title = intent.getStringExtra(Constants.TITLE);
            ((TextView) detailView.findViewById(R.id.tvTitle))
                    .setText(title);

            mPoster = intent.getStringExtra("path");
            Log.i(LOG_TAG, "poster URL: " + mPoster);
            ImageView poster = (ImageView) detailView.findViewById(R.id.ivPoster);
            Picasso.with(getActivity())
                    .load(mPoster)
                    .fit()
                    .into(poster);

            getActivity().setTitle(title);

            backdrop = Constants.BASE_IMAGE_PATH + intent.getStringExtra("back_drop");
            Log.i(LOG_TAG, "Backdrop URL: " + backdrop);
            ImageView ivBackdrop = (ImageView) detailView.findViewById(R.id.ivBackdrop);
            Picasso.with(getActivity())
                    .load(backdrop)
                    .fit()
                    .into(ivBackdrop);

            release = intent.getStringExtra(Constants.RELEASE_DATE);
            ((TextView) detailView.findViewById(R.id.tvReleaseDate))
                    .setText(release);

            rating = intent.getStringExtra("vote_avg");
            ((TextView) detailView.findViewById(R.id.tvRating))
                    .setText(rating);

            overview = intent.getStringExtra(Constants.DESC);
            ((TextView) detailView.findViewById(R.id.tvOverview))
                    .setText(overview);

            ListView reviewListView = (ListView) detailView.findViewById(R.id.lvReviews);
            mReviewAdapter = new ReviewAdapter(getContext(), R.layout.review_item, mReviewData);
            reviewListView.setAdapter(mReviewAdapter);

            ListView trailerListView = (ListView) detailView.findViewById(R.id.lvTrailer);
            mTrailerAdapter = new TrailerAdapter(getContext(), R.layout.trailer_item, mTrailerData);
            trailerListView.setAdapter(mTrailerAdapter);

            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerData.get(position).getKey())));
                }
            });

        }
        return detailView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Execute FetchTrailerTask
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();

        //Execute FetchReviewTask
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();

    }

    // handle favorite button on click
    @Override
    public void onClick(View v) {
        Log.i(LOG_TAG, "Button clicked-");
        if (v == null) return;
        Log.i(LOG_TAG, "Button clicked+");

        final int resId = v.getId();
        if (resId == R.id.btFavorite) {
            Uri uri = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(id).build();
            Log.i(LOG_TAG, "uri is: " + uri);
            try {
                final Cursor favoriteCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                Log.i(LOG_TAG, "cursor: " + favoriteCursor);
                if ((favoriteCursor != null) && (!(favoriteCursor.moveToNext()))) {
                    ContentValues contentValues = generateContentValues();
                    Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);
                    long id = ContentUris.parseId(insertedUri);
                    Log.i(LOG_TAG, "id is :" + id);
                    if (id != -1) {
                        Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    deleteFavorite();
                    Toast.makeText(getActivity(), "Delete from favorites", Toast.LENGTH_SHORT).show();
                }
                if (favoriteCursor != null) {
                    favoriteCursor.close();
                }
            } catch (Exception e) {

            }
        }
    }

    //insert movie to FavoriteEntry table
    private ContentValues generateContentValues() {
        ContentValues favoriteMovieValues = new ContentValues();
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, title);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_THUMB, thumb);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_BACK_DROP, backdrop);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, mPoster);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, overview);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_RATING, rating);
        favoriteMovieValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, release);
        return favoriteMovieValues;
    }

    public void deleteFavorite() {
        getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI, id, null);
    }

    /*
    // handle favourite button on click
    @Override
    public void onClick(View v) {
        Log.i(LOG_TAG, "Button clicked-");
        if(v == null) return;
        Log.i(LOG_TAG, "Button clicked+");

        final int resId = v.getId();
        if (resId == R.id.favorite_button) {
            Uri uri = MovieContract.FavouriteEntry.CONTENT_URI.buildUpon().appendPath(movie_id).build();
            Log.i(LOG_TAG, "uri is: "+ uri);
            try {
            final Cursor favoriteCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            Log.i(LOG_TAG, "cursor: "+ favoriteCursor);
            if ((favoriteCursor != null) && (!(favoriteCursor.moveToNext()))) {
                ContentValues contentValues = generateContentValues();
                Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
                long id = ContentUris.parseId(insertedUri);
                Log.i(LOG_TAG,"id is :"+id);
                if (id != -1) {
                    Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            } else {
                deleteFavourite();
                Toast.makeText(getActivity(), "Delete from favorites", Toast.LENGTH_SHORT).show();
            }
                if(favoriteCursor != null) {
                    favoriteCursor.close();
                }
            } catch (Exception e) {

            }
        }
    }
    //insert movie to FavouriteEntry table
    private ContentValues generateContentValues() {
        ContentValues favouriteMovieValues = new ContentValues();
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, movie_id);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_TITLE, movie_title);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_THUMB, movie_thumb);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_BACK_DROP, movie_backdrop);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_POSTER, movie_poster);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_OVERVIEW, movie_overview);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RATING, movie_rating);
        favouriteMovieValues.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE, movie_release);


        return favouriteMovieValues;
    }

    public void deleteFavourite() {
        getActivity().getContentResolver().delete(MovieContract.FavouriteEntry.CONTENT_URI, movie_id, null);
    }
     */



    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<ReviewItem>> {

        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        private ArrayList<ReviewItem> getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            JSONObject reviewObject = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewObject.getJSONArray(Constants.RESULTS);
            ArrayList<ReviewItem> reviewCollection = new ArrayList<>();
            for (int i = 0; i < reviewArray.length(); i++) {
                ReviewItem reviewItem = new ReviewItem();
                JSONObject review = reviewArray.getJSONObject(i);
                reviewItem.setAuthor(review.getString("author"));
                reviewItem.setContent(review.getString("content"));
                reviewCollection.add(reviewItem);
            }
            return reviewCollection;
        }

        @Override
        protected ArrayList<ReviewItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewDataStr = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews";
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.e(LOG_TAG, "Review url is " + url);

                //Create the request to Moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Reading input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    //empty stream. No parsing
                    return null;
                }
                reviewDataStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getReviewDataFromJson(reviewDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewItem> result) {
            if (result != null && !result.isEmpty()) {
                mReviewAdapter.clear();
                for (ReviewItem review : result) {
                    mReviewAdapter.add(review);
                }
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<TrailerItem>> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private ArrayList<TrailerItem> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            JSONObject trailerObject = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerObject.getJSONArray(Constants.RESULTS);
            ArrayList<TrailerItem> trailerCollection = new ArrayList<>();
            for (int i = 0; i < trailerArray.length(); i++) {
                final String youtubeUrl = "https://www.youtube.com/watch?v=";
                TrailerItem trailerItem = new TrailerItem();
                JSONObject trailerResults = trailerArray.getJSONObject(i);
                trailerItem.setKey(youtubeUrl + trailerResults.getString(Constants.KEY));
                trailerItem.setName(trailerResults.getString(Constants.NAME));
                trailerCollection.add(trailerItem);

            }
            return trailerCollection;
        }

        @Override
        protected ArrayList<TrailerItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // contain the raw JSON response as as string
            String trailerDataStr = null;
            try {
                //construct URL
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos";
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .build();
                URL url = new URL(buildUri.toString());
                Log.e(LOG_TAG, "Trailer url is " + url);

                //Create the request to Moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Reading input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    //empty stream. No parsing
                    return null;
                }
                trailerDataStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getTrailerDataFromJson(trailerDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TrailerItem> result) {
            if (result != null) {
                mTrailerAdapter.clear();
                for (TrailerItem trailer : result) {
                    mTrailerAdapter.add(trailer);
                }
                mTrailerAdapter.notifyDataSetChanged();
            }
        }
    }
}





