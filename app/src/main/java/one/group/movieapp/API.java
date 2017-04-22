package one.group.movieapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class API {
    private static final String LOG_TAG = API.class.getSimpleName();

    private final Context mContext;

    private API(Context mContext) {
        this.mContext = mContext;
    }

    public static List<MovieItem> fetchMovieList(String requestUrl) {

        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        List<MovieItem> movieItem = extractFeatureFromJson(jsonResponse);

        return movieItem;
    }

    public static MovieItem fetchMovieDetails(String requestUrl) {

        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        MovieItem movieItem = extractFeatureDetailsFromJson(jsonResponse);

        return movieItem;
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
            urlConnection.setDoInput(true);
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
            Log.e(LOG_TAG, e.toString());
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

    private static List<MovieItem> extractFeatureFromJson(String movieJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }

        List<MovieItem> movieItemLists = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJson);

            // Extract the JSONArray associated with the key called ""results"",
            JSONArray movieListArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < movieListArray.length(); i++) {

                JSONObject currentBookList = movieListArray.getJSONObject(i);


                // Extract the value for the key called "title"
                String movieTitle = currentBookList.getString("title");
                // Extract the value for the key called "id+"
                int movieId = currentBookList.getInt("id");
                String movieThumbnail = currentBookList.getString("backdrop_path");


                MovieItem movieItemList = new MovieItem( movieId,  movieTitle,movieThumbnail  );

                movieItemLists.add(movieItemList);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", e.toString());
        }


        return movieItemLists;

    }




    private static MovieItem extractFeatureDetailsFromJson(String movieJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }

        MovieItem movieItemDetails = new MovieItem (null,null,null,null,null);
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJson);

            // Extract the JSONArray associated with the key called ""results"",
            String movieTitle = baseJsonResponse.getString("title");
            String moviePoster = baseJsonResponse.getString("poster_path");
            String movieReleaseDate = baseJsonResponse.getString("release_date");
            String movieOverview = baseJsonResponse.getString("overview");
            String movieVoteAverage = baseJsonResponse.getString("vote_average");
            Log.i(TAG, "extractFeatureDetailsFromJson: "+moviePoster);
            movieItemDetails   = new MovieItem(movieTitle,  movieOverview , movieReleaseDate ,  moviePoster, movieVoteAverage);


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", e.toString());
        }


        return movieItemDetails;

    }

}

