package com.rcdev.popularmovies.tasks;

/**
 * Created by coreyestes
 * 2016 02 04
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.rcdev.popularmovies.adapter.ReviewAdapter;
import com.rcdev.popularmovies.objects.ReviewItem;
import com.rcdev.popularmovies.utils.Constants;

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

/**
 * Created by coreyestes
 * 2016 01 28
 */
public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<ReviewItem>> {

    ArrayList<ReviewItem> reviewResults;
    private ReviewAdapter mReviewAdapter;

    String movie_id;
    private Context context;

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    public FetchReviewTask(Context context, String movie_id) {
        super();
        this.context = context;
        this.movie_id = movie_id;
    }

    public void setmReviewAdapter(ReviewAdapter reviewAdapter) {
        this.mReviewAdapter = reviewAdapter;
    }

    private ArrayList<ReviewItem> getReviewDataFromJson(String reviewJsonStr) throws JSONException {

        JSONObject reviewObject = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewObject.getJSONArray(Constants.RESULTS);
        String author;
        String content;


        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            author = review.getString(Constants.AUTHOR);
            content = review.getString(Constants.CONTENT);
            ReviewItem item = new ReviewItem(author, content);
            reviewResults.add(item);
        }
        return reviewResults;
    }



    @Override
    protected ArrayList<ReviewItem> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // contain the raw JSON response as as string
        String reviewDataStr = null;
        try {
            //construct URL
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews";
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