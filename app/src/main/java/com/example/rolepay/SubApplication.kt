package com.example.rolepay

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log


// This is where code commonly used all around the app will be written
class SubApplication : Application() {
    companion object {
        lateinit  var appContext: Context
        //Create data storage for user info
        var userId: Int? = 1
        var publicToken: String? = null
        var privateToken: String? = null
        var admin: Int? = null
        var balanceId: Int? = 1
        var balanceAmount: Double? = null
        var environmentId: Int? = 1
        // Access anywhere in app, for example: "SubApplication.userId"
    }

    // Runs once when app starts
    override fun onCreate() {
        super.onCreate()
        SubApplication.appContext = applicationContext
        Log.d("MainActivity", "App starting")

        // Create an intent
        val i = Intent()
        // Create parameters, they will be added to the url/request body
        //val params = hashMapOf<String,String>("name" to "NewName", "id" to "1")
        // Action is the name of intent, can be anything, currently used for debug
        i.action = "ACTION_TEST_CONNECTION"
        // Add parameters to intent
        //i.putExtra("params", params)
        // Set method
        //i.putExtra("method", "POST")
        // Add url path, these can be checked from server/routes/index.js
        i.putExtra("path", "") // Could be for example "environment/name"
        // Add receiver (= code that handles the response). This is required
        i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                // Successful API call
                if (resultCode == 200) {
                    // Write your logic here
                    Log.d("SubApplication", "Data retrieved: " + resultData.getString("Data"))
                } else { // Failed API call
                    // Write your logic here
                    Log.i(
                        "SubApplication",
                        "Something went wrong: " + resultData.getString("Error")
                    )
                }
            }
        })
        DatabaseConnection.enqueueWork(this, i)
    }
}