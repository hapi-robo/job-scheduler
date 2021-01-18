package com.hrst.job_scheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class PackageUpdate extends JobService {
    private final String TAG = "PackageUpdate";

    @Override
    public boolean onStartJob(JobParameters params) {
        String url = params.getExtras().getString("text");
        Log.i(TAG, url);
        Utilities.download(this, url);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
