/*
 * CS 193A, Marty Stepp
 * This service downloads files in the background.
 * Even if the downloader app is exited, the service remains running.
 */

package com.downloader;

import android.app.*;
import android.content.*;
import android.os.*;

public class DownloaderService extends Service {
    // we declare constants for "actions" that are packed into each intent
    public static final String ACTION_DOWNLOAD = "download";
    public static final String ACTION_DOWNLOAD_COMPLETE = "download_complete";

    // constant ID sent when we broadcast a download-complete message
    public static final int ID_NOTIFICATION_DL_COMPLETE = 1234;

    // handler thread loops waiting for jobs (downloads) to run
    // in a separate thread
    private HandlerThread hThread;

    /*
     * This method runs when the service is started up.
     * Sets up initial state of the handler thread.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        hThread = new HandlerThread("Skippy");
        hThread.start();
    }

    /*
     * This method is called each time a request comes in from the app
     * via an intent.  It processes the request by enqueuing a new
     * download job in the background handler thread.
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_DOWNLOAD)) {
            // create a "runnable" task for this download
            Runnable runner = new Runnable() {
                public void run() {
                    // download the file
                    String url = intent.getStringExtra("url");
                    Downloader.downloadFake(url);

                    // show a notification in the top notifications bar
                    Notification.Builder builder = new Notification.Builder(DownloaderService.this)
                            .setContentTitle("Download complete")
                            .setContentText(url)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.icon_download);
                    Notification notification = builder.build();
                    NotificationManager manager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(ID_NOTIFICATION_DL_COMPLETE, notification);

                    // broadcast a message back to the app to inform it that we are done
                    Intent done = new Intent();
                    done.setAction(ACTION_DOWNLOAD_COMPLETE);
                    done.putExtra("url", url);
                    sendBroadcast(done);
                }
            };

            // wrap the runnable into a Handler job to be given to our background thread
            Handler handler = new Handler(hThread.getLooper());
            handler.post(runner);
        }

        return START_STICKY;   // START_STICKY means service will keep running
    }

    /*
     * This method specifies how our service will deal with binding.
     * We don't choose to support binding, which is indicated by returning null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
