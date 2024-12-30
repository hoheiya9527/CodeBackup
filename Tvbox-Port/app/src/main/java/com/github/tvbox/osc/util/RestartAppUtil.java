package com.github.tvbox.osc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class RestartAppUtil {

    public static void restartApp(Activity activity) {
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(ReopenWorker.class)
                .setInitialDelay(1, TimeUnit.SECONDS).build();
        WorkManager.getInstance(activity.getApplicationContext()).enqueue(workRequest);
//        Process.killProcess(Process.myPid());
        activity.finish();
    }

    public static class ReopenWorker extends Worker {
        private Context context;

        public ReopenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            this.context = context;
        }

        @NonNull
        @Override
        public Result doWork() {
            Intent intent = new Intent(context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return Result.success();
        }
    }
}
