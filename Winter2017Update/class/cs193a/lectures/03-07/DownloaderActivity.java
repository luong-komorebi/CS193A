/*
 * CS 193A, Marty Stepp
 * This activity is the main GUI for the downloader app.
 * You can type a URL in the top edit text pane, then press Go,
 * and all links in that page are shown as items in a list view.
 * You can click the list items to download them in the background
 * using a service.
 * Even if the downloader app is exited, the download will get finished
 * because it is running in a service.
 */

package cs193a.stanford.edu.downloader;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import stanford.androidlib.*;

public class DownloaderActivity extends SimpleActivity {
    // web domain where files will be downloaded from
    private static final String DOMAIN = "http://www.martystepp.com/files/";

    private ArrayList<String> listOfLinks;   // these are for the ListView

    /*
     * This method runs when the activity is started up.
     * Sets up initial state of the GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        // code for if I was invoked by a notification from the DownloaderService
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("url")) {
            String url = intent.getStringExtra("url");
            toast("I'm back! Thanks for downloading " + url);
        }

        // register receiver to hear broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction("downloadcomplete");
        registerReceiver(new MyReceiver(), filter);
    }

    /*
     * This method is called when the "Go" button is clicked.
     * It downloads the given web page.
     */
    public void getClick(View view) {
        EditText edit = (EditText) findViewById(R.id.the_url);
        String webPageURL = edit.getText().toString();

        EditText delayEditText = (EditText) findViewById(R.id.delay);
        int delayMS = Integer.valueOf(delayEditText.getText().toString());

        CheckBox fakeBox = (CheckBox) findViewById(R.id.fake);
        boolean fake = fakeBox.isChecked();

        // download this file using a service
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction("download");
        intent.putExtra("url", webPageURL);
        startService(intent);

        log("Starting download " + webPageURL);
        toast("Starting download ...");
    }

    /*
     * code that runs when service sends a broadcast
     * to indicate that it is done doing work
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra("url");
            log("Done downloading " + url);
            toast("Done downloading " + url);
        }
    }

    /*
     * This method is called when items in the ListView are clicked.
     * It initiates a request to the DownloadService to download the file.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        super.onItemClick(parent, view, index, id);
        String domain = findEditText(R.id.the_url).getText().toString();
        String url = domain + listOfLinks.get(index);
        boolean fake = findCheckBox(R.id.fake).isChecked();
        int delayMS = Integer.valueOf(findEditText(R.id.delay).getText().toString());
    }
}
