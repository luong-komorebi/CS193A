/*
 * CS 193A, Marty Stepp
 * This program demonstrates REST APIs by fetching some data from two simple APIs,
 * the Internet Chuck Norris Database (ICNDb) and The Cat API.
 * We use the Ion library for downloading JSON/XML data from URLs, and
 * we use the Picasso library for fetching images from URLs.
 */

package cs193a.stanford.edu.resttest;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import org.json.*;
import stanford.androidlib.*;
import stanford.androidlib.xml.*;

public class RestActivity extends SimpleActivity {
    // auto-generated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
    }

    /*
     * Called when the "Chuck Norris Joke" button is clicked.
     * Downloads a Chuck Norris joke using the Chuck Norris API as JSON data
     * and then displays that in a text view.
     */
    public void chuckNorrisClick(View view) {
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // data has arrived
                        processJoke(result);
                    }
                });
    }

    /*
     * Displays a Chuck Norris joke using the Chuck Norris API from JSON data into a text view.
     * The JSON data will be in the following format:
     * {
     *     "type": "success",
     *     "value": {
     *         "id": 387,
     *         "joke": "For Spring Break '05, Chuck Norris drove to Madagascar, riding a chariot pulled by two electric eels.",
     *         "categories": []
     *     }
     * }
     */
    private void processJoke(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject value = json.getJSONObject("value");
            String joke = value.getString("joke");
            $TV(R.id.output).setText(joke);
        } catch (JSONException jsone) {
            Log.wtf("help", jsone);
        }
    }

    /*
     * Called when the Cat Pic button is clicked.
     * Downloads cat pictures from the Cat API as XML, converts the
     */
    public void catClick(View view) {
        GridLayout grid = $(R.id.grid);
        grid.removeAllViews();

        Ion.with(this)
                .load("http://thecatapi.com/api/images/get?format=xml&results_per_page=6")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // data has arrived
                        loadCatPics(result);
                    }
                });
    }

    /*
     * The JSON data will be in the following format:
     * {
     *   "response": {
     *     "data": {
     *         "images": {
     *             "image": [
     *                {"url":"http:\/\/24.media.tumblr.com\/tumblr_luw2y2MCum1qbdrypo1_500.jpg","id":"d40","source_url":"http:\/\/thecatapi.com\/?id=d40"},
     *                {"url":"http:\/\/25.media.tumblr.com\/tumblr_m4rwp4gkQ11r6jd7fo1_400.jpg","id":"e85","source_url":"http:\/\/thecatapi.com\/?id=e85"},
     *  ...
     * }
     */
    public void loadCatPics(String result) {
        try {

            JSONObject json = XML.toJSONObject(result);
            log(json);
            JSONArray a = json.getJSONObject("response")
                    .getJSONObject("data")
                    .getJSONObject("images")
                    .getJSONArray("image");
            for (int i = 0; i < a.length(); i++) {
                JSONObject img = a.getJSONObject(i);
                String url = img.getString("url");
                log("loading image from " + url);
                loadImage(url);
            }

        } catch (JSONException jsone) {
            Log.wtf("help", jsone);
        }
    }

    /*
     * Downloads and displays the image at the given URL using the Picasso library.
     */
    public void loadImage(String url) {
        ImageView imgView = new ImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        imgView.setLayoutParams(params);
        GridLayout grid = $(R.id.grid);
        grid.addView(imgView);

        Picasso.with(this)
                .load(url)
                .resize(300, 300)
                .into(imgView);
    }
}
