package com.example.rolepay;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * Example implementation of a JobIntentService.
 */
public class DatabaseConnection extends JobIntentService {
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, DatabaseConnection.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        Log.i("DatabaseConnection", "Executing work: " + intent);
        String label = intent.getStringExtra("label");
        if (label == null) {
            label = intent.toString();
        }
        toast("Executing: " + label);
        for (int i = 0; i < 5; i++) {
            Log.i("DatabaseConnection", "Running service " + (i + 1)
                    + "/5 @ " + SystemClock.elapsedRealtime());
            try {
                Thread.sleep(1000);
                // Write tasks here
                switch (Objects.requireNonNull(intent.getAction())){
                    case "ACTION_TEST_CONNECTION": testConnection();break;
                    default: Log.d("DatabaseConnection", "No action was provided");
                }
            } catch (InterruptedException e) {
            }
        }
        Log.i("DatabaseConnection", "Completed service @ " + SystemClock.elapsedRealtime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast("All work complete");
    }

    @SuppressWarnings("deprecation")
    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(DatabaseConnection.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    void testConnection () {
        Log.d("DatabaseConnection", "Testing connection");
        String url = "jdbc:mysql://192.168.1.101:3306/rolepay";
        String user = "app";
        String pass = "appPassword";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(
                    url,
                    user,
                    pass
            );

            Log.d("DatabaseConnection","Connection works!");
        } catch (Exception e) {
            Log.d("DatabaseConnection",e.toString());
        }
    }
}