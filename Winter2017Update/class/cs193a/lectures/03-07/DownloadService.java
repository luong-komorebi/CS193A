/*
 * CS 193A, Marty Stepp
 * This service downloads files in the background.
 * The DownloaderActivity invokes this service so it can download files in a thread in the background.
 * Even if the downloader app is exited, the download will get finished
 * because it is running in a service.
 */

package cs193a.stanford.edu.downloader;

import android.app.*;
import android.content.*;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {
    // ID code that is used to launch the download notifications
    private static final int NOTIFICATION_ID = 1234;

    /*
     * This method is called every time an app/activity calls startService.
     * We extract the URl from the intent and download the corresponding file.
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent.getAction().equals("download")) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    // download the file and then show a notification
                    String url = intent.getStringExtra("url");
                    Log.d("DownloadService", "You want my service to download: " + url);
                    String contents = Downloader.downloadFake(url, /* delay */ 5000);
                    showNotification(url, contents);
                }
            });
            thread.start();
        }

        return START_STICKY;
    }

    /*
     * Pops up a notification telling the user that the download has completed.
     */
    private void showNotification(String url, String contents) {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Download complete")
                .setContentText(url)
                .setSmallIcon(R.drawable.icon_download);

        // use an intent (and PendingIntent) to make the notification clickable
        // to launch the Downloader app
        Intent launch = new Intent(this, DownloaderActivity.class);
        launch.putExtra("url", url);
        PendingIntent pend = PendingIntent.getActivity(
                this, 0, launch, 0);
        builder.setContentIntent(pend);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);

        // finished! yay! tell everyone!
        Intent done = new Intent();
        done.setAction("downloadcomplete");
        done.putExtra("url", url);
        done.putExtra("data", contents);
        sendBroadcast(done);
    }

    /*
     * This method is used for "bound" services.
     * Our service is not used in bound mode, so we just return null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;   // not supported
    }
}
