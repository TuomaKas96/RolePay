package com.example.rolepay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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
    protected ResultReceiver receiver = null;
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
        String label = intent.getStringExtra("label");
        if (label == null) {
            label = intent.toString();
        }
        Log.i("DatabaseConnection","Executing: " + label);
        receiver = intent.getParcelableExtra("RECEIVER");

        //Checks if receiver was properly registered
        if (receiver == null) {
            Log.e(
                    label,
                    "No reciever received. There is nowhere to send the results!"
            );
            return;
        }
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
                case "ACTION_FETCH_ENVIRONMENT_NAME":
                    getEnvironmentName(intent);
                    break;
                default:
                    Log.d("DatabaseConnection", "No action was provided");
            }
        } catch (InterruptedException e) {
            Log.d("DatabaseConnection",e.toString());
        }

        Log.i("DatabaseConnection", "Completed service @ " + SystemClock.elapsedRealtime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DatabaseConnection","All work complete");
    }

    static String dbUrl = "http://192.168.1.101:3000/"; //Change to match your local IP

    void apiConnection (String path) {
        Log.d("DatabaseConnection", "Testing connection");
        try {
            // Use local IP for the debugging PC
            URL url = new URL( dbUrl + path);
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
    void getEnvironmentName (Intent intent) {
        Log.d("DatabaseConnection", "Fetching environment name by id");
        Bundle response = new Bundle();
        try {
            URL url = new URL( dbUrl + "environment/name/" + intent.getIntExtra("id", 0));
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            response.putString("Data", bufferedReader.readLine());
            Log.d("DatabaseConnection", response.getString("Data"));
            httpConn.disconnect();
            receiver.send(200, response);
        } catch (Exception e) {
            Log.d("DatabaseConnection", "Error: " + e.toString());
            response.putString("Error", e.toString());
            receiver.send(404, response);
        }
    }
}