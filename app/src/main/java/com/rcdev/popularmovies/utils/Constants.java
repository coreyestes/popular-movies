package com.rcdev.popularmovies.utils;

/**
 * Created by coreyestes on 7/22/15.
 */
public final class Constants {
    private Constants() {
    }

    //BASE URLS
    public static final String BASE_REQ_PATH = "http://api.themoviedb.org/3/discover/movie?";
    public static final String BASE_IMAGE_PATH = "http://image.tmdb.org/t/p/w780/";

    //API JSON VALUES
    public static final String ID = "id";
    public static final String RESULTS = "results";
    public static final String POSTER_PATH = "poster_path";
    public static final String TITLE = "title";
    public static final String DESC = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String POPULARITY = "popularity";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String VOTE_COUNT = "vote_count";
    public static final String BACKDROP = "backdrop_path";

    //REQUEST PARAMS
    public static final String SORT_BY = "sort_by";
    public static final String DEFAULT = "popularity.desc";

    //SORTING PARAMS
    public static final String ASCENDING = "asc";
    public static final String DESCENDING = "desc";

    //OTHER
    public static final String API_KEY = "5694871dec73960c938ad1efe0851922";
    public static final String IMAGE_SIZE = "w780/";

    //REVIEW
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";

    //TRAILER
    public static final String KEY = "key";
    public static final String NAME = "name";



}
