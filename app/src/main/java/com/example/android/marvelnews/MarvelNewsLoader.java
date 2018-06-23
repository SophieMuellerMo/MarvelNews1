package com.example.android.marvelnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of marvelNews by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class MarvelNewsLoader extends AsyncTaskLoader<List<MarvelNews>> {

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link MarvelNewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MarvelNewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<MarvelNews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of marvelNews.
        List<MarvelNews> marvelNewss = QueryUtils.fetchMarvelNewsData(mUrl);
        return marvelNewss;
    }
}