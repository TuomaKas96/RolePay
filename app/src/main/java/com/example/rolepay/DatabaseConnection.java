package com.example.rolepay;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
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
        Log.i("DatabaseConnection","Executing: " + label);
        try {
            Thread.sleep(1000);
            // Write tasks here
            switch (Objects.requireNonNull(intent.getAction())) {
                case "ACTION_TEST_CONNECTION":
                    apiConnection("");
                    break;
                case "ACTION_FETCH_USER":
                    apiConnection("user/1");
                    break;
                default:
                    Log.d("DatabaseConnection", "No action was provided");
            }
        } catch (InterruptedException e) {
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
    void apiConnection (String path) {
        Log.d("DatabaseConnection", "Testing connection");
        try {
            // Use local IP for the debugging PC
            URL url = new URL("http://192.168.1.101:3000/" + path);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("GET");
            InputStream inputStream = httpConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.d("DatabaseConnection", line);
            httpConn.disconnect();
        } catch (Exception e) {
            Log.d("DatabaseConnection", "Error: " + e.toString());
        }
        finally {
            Log.d("DatabaseConnection", "API call finished");
        }
    }
}