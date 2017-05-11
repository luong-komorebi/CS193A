/*
 * CS 193A, Marty Stepp
 * This activity class demonstrates several useful libraries shown in class today.
 */

package cs193a.stanford.edu.puppyviewer;

import android.os.*;
import android.view.*;
import android.widget.*;
import com.daimajia.androidanimations.library.*;
import com.koushikdutta.async.future.*;
import com.koushikdutta.ion.*;
import com.nineoldandroids.animation.*;
import com.squareup.picasso.*;
import butterknife.*;
import stanford.androidlib.*;

public class PuppyActivity extends SimpleActivity {
    // constant for base web URL where image files are found
    private static final String WEBSITE_DIRECTORY = "http://www.martystepp.com/dogs/";

    // fixed array that we used previously for testing
//    private static final ALL_IMAGES = {
//            "barney-and-clyde-12.jpg",
//            "daisy-14.jpg",
//            "barney-and-clyde-02-large.jpg"
//    };

    // this is equivalent to:  ImageView img = (ImageView) findViewById(R.id.puppyphoto);
    // the variable will survive any destroy/reload caused by rotation etc.
    @BindView(R.id.puppyphoto) ImageView img;

    /*
     * Called when the activity first loads up.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puppy);
        ButterKnife.bind(this);
        downloadImageNames();
    }

    /*
     * Called when the "Click Me" button is clicked.
     * Causes the currently selected image to be downloaded and displayed.
     * This is somewhat unnecessary now that we have the OnItemSelected code below.
     */
    @OnClick(R.id.clickme)
    public void clickMeClick(View view) {
        YoYo.with(Techniques.Wobble)
                .duration(2000)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        displayImage();
                    }
                })
                .playOn(view);

    }

    /*
     * This is attached as the onItemSelected handler on the spinner.
     * It causes the currently selected item to be downloaded and displayed.
     */
    @OnItemSelected(R.id.puppy_spinner)
    public void itemGotSelected(AdapterView<?> parent, View view, int position, long id) {
        displayImage();
    }

    /*
     * This method causes the currently selected item in the spinner to be loaded
     * and displayed using the Picasso library.
     */
    private void displayImage() {
        String imageUrl = WEBSITE_DIRECTORY + $SP(R.id.puppy_spinner).getSelectedItem().toString();
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .into(img);
    }

    /*
     * This code is called when the activity first loads up.
     * It uses the Ion framework to download a list of image file names to display.
     */
    private void downloadImageNames() {
        Ion.with(this)
                .load(WEBSITE_DIRECTORY + "files.txt")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    public void onCompleted(Exception e, String result) {
                        // code to process the result when done downloading
                        // "abc.jpg \n xyz.jpg \n ..."
                        String[] allImages = result.split("\n");
                        SimpleList.with(PuppyActivity.this)
                                .setItems(R.id.puppy_spinner, allImages);
                    }
                });
    }
}
