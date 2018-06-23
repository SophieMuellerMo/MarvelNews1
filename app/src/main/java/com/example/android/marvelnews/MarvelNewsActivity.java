package com.example.android.marvelnews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MarvelNewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<MarvelNews>> {

    /**
     * URL for marvelNews data from the Guardian dataset
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search";

    /**
     * Constant value for the marvelNews loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MARVELNEWS_LOADER_ID = 1;

    /**
     * Context
     */
    private Context currentContext;

    /**
     * Adapter for the list of marvelNews
     */
    private MarvelNewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marvel_news_activity);

        //Set context
        currentContext = this;

        // Find a reference to the {@link ListView} in the layout
        ListView marvelNewsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        marvelNewsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of marvelNews as input
        mAdapter = new MarvelNewsAdapter(this, new ArrayList<MarvelNews>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        marvelNewsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected marvelNews.
        marvelNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current marvelNews that was clicked on
                MarvelNews currentMarvelNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri marvelNewsUri = Uri.parse(currentMarvelNews.getUrl());

                // Create a new intent to view the marvelNews URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, marvelNewsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MARVELNEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<MarvelNews>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minDate = sharedPrefs.getString(
                getString(R.string.settings_min_date_key),
                getString(R.string.settings_min_date_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `order-by=relevance`
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("section", "film");
        uriBuilder.appendQueryParameter("from-date", minDate);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-elements", "all");
        uriBuilder.appendQueryParameter("q", "Marvel");
        uriBuilder.appendQueryParameter("api-key", "5369aa36-6009-4195-86b5-8dc3d98e9915");


        // Return the completed uri `https://content.guardianapis.com/search?order-by=relevance&section=film&from-date=2010-01-01&show-tags=contributor&show-elements=all&q=Marvel&api-key=5369aa36-6009-4195-86b5-8dc3d98e9915
        return new MarvelNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MarvelNews>> loader, List<MarvelNews> marvelNewss) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No marvelNewss found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous marvelNews data
        mAdapter.clear();

        // If there is a valid list of {@link MarvelNews}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (marvelNewss != null && !marvelNewss.isEmpty()) {
            mAdapter.addAll(marvelNewss);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MarvelNews>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}