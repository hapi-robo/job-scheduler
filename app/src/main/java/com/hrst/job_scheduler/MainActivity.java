package com.hrst.job_scheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;

public class MainActivity extends AppCompatActivity {
    private int UPDATE_PACKAGE_ID = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register download-complete receiver
        registerReceiver(Utilities.onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Add data to job
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("text", "https://github.com/hapi-robo/sandbox/releases/download/v2.0.0/connect-v1.3.2-debug.apk");

        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(UPDATE_PACKAGE_ID, new ComponentName(this, PackageUpdate.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister download-complete receiver
        unregisterReceiver(Utilities.onDownloadComplete);
    }
}