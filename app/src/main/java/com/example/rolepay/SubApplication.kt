package com.example.rolepay

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log


// This is where code commonly used all around the app will be written
class SubApplication : Application() {
    // Runs once when app starts
    override fun onCreate() {
        super.onCreate()
        Log.d("MainActivity", "App starting")
        val i = Intent()
        //i.setAction("ACTION_TEST_CONNECTION")
        i.setAction("ACTION_FETCH_ENVIRONMENT_NAME")
        i.putExtra("id", 4)
        i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                if (resultCode == 200) {
                    Log.d("SubApplication", "Data retrieved: " + resultData.getString("Data"))
                } else {
                    Log.i("SubApplication", "Something went wrong: " + resultData.getString("Error"))
                }
            }
        })
        DatabaseConnection.enqueueWork(this, i)
    }
}