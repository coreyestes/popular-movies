package com.rcdev.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.rcdev.popularmovies.adapter.MoviePosterAdapter;
import com.rcdev.popularmovies.objects.MovieItem;
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
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {
    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private MoviePosterAdapter mMovieAdapter;


    public void setmMovieAdapter(MoviePosterAdapter moviePosterAdapter){
        this.mMovieAdapter =moviePosterAdapter;
    }
    private ArrayList<MovieItem> getMovieDataFromJson(String MovieJasonStr) throws JSONException {
        final String RESULTS = "results";

        // Values we are going to fetch from JSON.
        final String ID = "id";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String BACKDROP_PATH = "backdrop_path";
        final String TITLE = "original_title";
        final String RATING = "vote_average";

        String thumbBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String posterBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String backdropBaseUrl = "http://image.tmdb.org/t/p/w342/";

        JSONObject movieJson = new JSONObject(MovieJasonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        //Loop JSON Array and fetch JSON movie objects
        ArrayList<MovieItem> resultMovies = new ArrayList<MovieItem>();
        for (int i = 0; i < movieArray.length(); i++) {
            //set strings default to null
            String id;
            String overview;
            String release_date;
            String thumb;
            String poster;
            String backdrop;
            String title;
            String rating;
            //Get JSON object representing the Movie.
            JSONObject currentMovie = movieArray.getJSONObject(i);

            //get needed data from JSON text
            id = currentMovie.getString(ID);
            overview = currentMovie.getString(OVERVIEW);
            release_date = currentMovie.getString(RELEASE_DATE);
            thumb = thumbBaseUrl + currentMovie.getString(POSTER_PATH);
            poster = posterBaseUrl + currentMovie.getString(POSTER_PATH);
            backdrop = backdropBaseUrl + currentMovie.getString(BACKDROP_PATH);
            title = currentMovie.getString(TITLE);
            rating = currentMovie.getString(RATING);

            // Make a movie object and add to movie list
            MovieItem movie = new MovieItem(id, thumb, title, poster, backdrop,overview, release_date, rating);
            resultMovies.add(movie);
            Log.i(LOG_TAG, movie.toString());
        }
        return resultMovies;
    }

    @Override
    protected ArrayList<MovieItem> doInBackground(String... params) {

        if (params.length == 0) {
            //nothing to look up
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will content the raw JSON response as a string.
        String movieJsonStr = null;

        //Sorting format passed in from setting activity,
        String sortFormat = null;
        sortFormat = params[0];


        try {

            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortFormat)
                    .appendQueryParameter(API_PARAM, Constants.API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "URL: " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {

                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
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
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieItem> result) {
        Log.i(LOG_TAG, "Result movie:   "+ result.toString());
        if (result != null && result.isEmpty()) {
            mMovieAdapter.clear();

            for (MovieItem item : result) {
                mMovieAdapter.add(item);
            }


        }
    }
}