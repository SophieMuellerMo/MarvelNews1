package com.example.android.marvelnews;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving marvelNew data from Guardians dataset.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardians dataset and return a list of {@link MarvelNews} objects.
     */
    public static List<MarvelNews> fetchMarvelNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link MarvelNews}s
        List<MarvelNews> marvelNewss = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link MarvelNews}s
        return marvelNewss;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the marvelNews JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link MarvelNews} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<MarvelNews> extractFeatureFromJson(String marvelNewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(marvelNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding marvelNews to
        List<MarvelNews> marvelNewss = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(marvelNewsJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of features (or marvelNewss).
            JSONArray marvelNewsArray = baseJsonResponse.getJSONObject("response").getJSONArray("results");

            // For each marvelNews in the marvelNewsArray, create an {@link MarvelNews} object
            for (int i = 0; i < marvelNewsArray.length(); i++) {

                // Get a single marvelNews at position i within the list of marvelNewss
                JSONObject currentMarvelNews = marvelNewsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentMarvelNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentMarvelNews.getString("sectionName");

                //"Tags" element
                JSONArray tags = currentMarvelNews.getJSONArray("tags");

                //If "tags" array is not null
                String author = "";
                if (!tags.isNull(0)) {
                    JSONObject currentTag = tags.getJSONObject(0);

                    //Author first name
                    String authorFirstName = !currentTag.isNull("firstName") ? currentTag.getString("firstName") : "";

                    //Author last name
                    String authorLastName = !currentTag.isNull("lastName") ? currentTag.getString("lastName") : "";

                    //Author full name
                    author = StringUtils.capitalize(authorFirstName.toLowerCase().trim()) + " " + StringUtils.capitalize(authorLastName.toLowerCase().trim());
                    if (authorFirstName.trim() != "" || authorLastName.trim() != "") {
                        author = ("Author: ").concat(author);
                    } else {
                        author = "";
                    }
                }

                // Extract the value for the key called "webPublicationDate"
                String time = currentMarvelNews.getString("webPublicationDate");

                //Format publication date
                Date publicationDate = null;
                try {
                    publicationDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(time);
                } catch (Exception e) {
                    // If an error is thrown when executing the above statement in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("QueryUtils", "Problem parsing the news date", e);
                }

                // Extract the value for the key called "webUrl"
                String url = currentMarvelNews.getString("webUrl");

                // Create a new {@link MarvelNews} object with the author, title, section name,
                // time and url from the JSON response.
                MarvelNews marvelNews = new MarvelNews(author, title, sectionName, publicationDate, url);

                // Add the new {@link MarvelNews} to the list of marvelNewss.
                marvelNewss.add(marvelNews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the marvelNews JSON results", e);
        }

        // Return the list of marvelNewss
        return marvelNewss;
    }
}