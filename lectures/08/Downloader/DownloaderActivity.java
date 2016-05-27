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

package com.downloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class DownloaderActivity extends Activity
        implements AdapterView.OnItemClickListener {

    // web domain where files will be downloaded from
    private static final String DOMAIN = "http://www.martystepp.com/files/";

    private ArrayList<String> listOfLinks;   // these are for the ListView
    private ArrayAdapter<String> adapter;

    /*
     * This method runs when the activity is started up.
     * Sets up initial state of the GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        // set up the ListView
        listOfLinks = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfLinks);
        ListView listOfLinks = (ListView) findViewById(R.id.list_of_links);
        listOfLinks.setAdapter(adapter);
        listOfLinks.setOnItemClickListener(this);

        // set up a broadcast receiver to be informed when downloads are finished
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloaderService.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new MyReceiver(), filter);
    }

    /*
     * This method is called when the "Go" button is clicked.
     * It fetches all links contained in the given web page.
     */
    public void onClickGo(View view) {
        EditText edit = (EditText) findViewById(R.id.the_url);
        String webPageURL = edit.getText().toString();
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.filelist));
        while (scan.hasNextLine()) {
            listOfLinks.add(scan.nextLine());
        }
        adapter.notifyDataSetChanged();
    }

    /*
     * This method is called when items in the ListView are clicked.
     * It initiates a request to the DownloadService to download the file.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        EditText edit = (EditText) findViewById(R.id.the_url);
        String domain = edit.getText().toString();
        String url = domain + listOfLinks.get(index);

        // send request to DownloadService using an intent
        Intent intent = new Intent(this, DownloaderService.class);
        intent.putExtra("url", url);
        intent.setAction(DownloaderService.ACTION_DOWNLOAD);
        startService(intent);
    }

    /*
     * This broadcast receiver listens for "download complete" broadcasts
     * sent by the DownloadService and reacts to them by showing a toast.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloaderService.ACTION_DOWNLOAD_COMPLETE)) {
                String url = intent.getStringExtra("url");
                Toast.makeText(DownloaderActivity.this, "done downloading " + url, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
