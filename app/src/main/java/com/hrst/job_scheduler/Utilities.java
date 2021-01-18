package com.hrst.job_scheduler;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Utilities {
    private static final String TAG = "PackageUpdate";
    private static long mDownloadId;

    /**
     * Get MIME from file name
     * @param fileName File name
     * @return MIME type
     */
    private static String getMimeFromFileName(String fileName) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return map.getMimeTypeFromExtension(ext);
    }

    /**
     * Broadcast receiver for handling ACTION_DOWNLOAD_COMPLETE intents
     */
    public static final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the download ID received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            // Check if the ID matches our download ID
            if (mDownloadId == id) {
                Log.i(TAG, "Download ID: " + mDownloadId);

                // Get file URI
                DownloadManager dm = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(mDownloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int colIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(colIndex)) {
                        Log.i(TAG, "Download complete");
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Log.i(TAG, "URI: " + uriString);
                    } else {
                        Log.w(TAG, "Download failed, status code: " + c.getInt(colIndex));
                    }
                }
            }
        }
    };

    /**
     * Start package update
     * @param url Package URL
     */
    public static void download(Context context, String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);

        // https://developer.android.com/reference/android/app/DownloadManager.Request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setTitle(fileName)
                .setMimeType(getMimeFromFileName(fileName));

        DownloadManager dm = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        mDownloadId = dm.enqueue(request); // add download request to the queue
        Log.i(TAG, "Downloading...");
    }
}
