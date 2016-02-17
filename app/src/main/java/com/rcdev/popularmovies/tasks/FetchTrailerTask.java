package com.rcdev.popularmovies.tasks;

/**
 * Created by coreyestes
 * 2016 02 04
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.rcdev.popularmovies.adapter.TrailerAdapter;
import com.rcdev.popularmovies.objects.TrailerItem;
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
import java.util.List;

/**
 * Created by coreyestes
 * 2016 01 28
 */
public class FetchTrailerTask extends AsyncTask<String, Void, List<TrailerItem>> {


    public TrailerAdapter mTrailerAdapter;

    String movie_id;
    private Context context;

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    public FetchTrailerTask(Context context, String movie_id) {
        this.context = context;
        this.movie_id = movie_id;
    }

    public void setmTrailerAdapter(TrailerAdapter trailerAdapter) {
        this.mTrailerAdapter = trailerAdapter;
    }

    private ArrayList<TrailerItem> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {

        JSONObject trailerObject = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerObject.getJSONArray(Constants.RESULTS);
        String key;
        String name;
        ArrayList<TrailerItem> trailerCollection = new ArrayList<>();

        final String YoutubeBaseUrl = "https://www.youtube.com/watch?v=";

        for (int i = 0; i < trailerArray.length(); i++) {
            TrailerItem trailerItem = new TrailerItem();
            JSONObject trailerResults = trailerArray.getJSONObject(i);
            trailerItem.setKey(trailerResults.getString(Constants.KEY));
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
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos";
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
    protected void onPostExecute(List<TrailerItem> result) {
        if (result != null && !result.isEmpty()) {
            mTrailerAdapter.clear();
            for (TrailerItem trailer : result) {
                mTrailerAdapter.add(trailer);
            }
            mTrailerAdapter.notifyDataSetChanged();
        }
    }
}