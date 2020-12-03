package com.example.rolepay

import android.content.Context
import androidx.core.app.JobIntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.os.SystemClock
import android.util.Log
import com.example.rolepay.DatabaseConnection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class DatabaseConnection : JobIntentService() {
    protected var receiver: ResultReceiver? = null
    override fun onHandleWork(intent: Intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        var label = intent.getStringExtra("label")
        if (label == null) {
            label = intent.toString()
        }
        Log.i("DatabaseConnection", "Executing: $label")
        receiver = intent.getParcelableExtra("RECEIVER")

        //Checks if receiver was properly registered
        if (receiver == null) {
            Log.e(
                label,
                "No reciever received. There is nowhere to send the results!"
            )
            return
        }
        try {
            Thread.sleep(1000)
            // Start connection
            apiConnection(intent)
        } catch (e: InterruptedException) {
            Log.d("DatabaseConnection", e.toString())
        }
        Log.i("DatabaseConnection", "Completed service @ " + SystemClock.elapsedRealtime())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DatabaseConnection", "All work complete")
    }

    fun apiConnection(intent: Intent) {
        val response = Bundle()
        Log.d("DatabaseConnection", "Starting API call")
        try {
            val url: URL
            val httpConn: HttpURLConnection
            if (intent.getStringExtra("method") == "POST") {
                Log.d("DBCon", "POST coming")
                // Form the url
                if (intent.getSerializableExtra("params") == null) {
                    Log.e("DBCon", "POST query had no parameters! Aborting")
                    return
                }
                url = URL(dbUrl + intent.getStringExtra("path"))
                httpConn = url.openConnection() as HttpURLConnection
                httpConn.requestMethod = "POST"
                httpConn.setRequestProperty("Content-Type", "application/json")
                // Tell server that we will send data
                httpConn.doOutput = true
                // Form data from intent extras
                val params = intent.getSerializableExtra("params")
                val gson = Gson()
                val json = gson.toJson(params)
                Log.d("DBCon", json)
                httpConn.outputStream.use { outputStream ->
                    val input = json.toByteArray(charset("utf-8"))
                    outputStream.write(input, 0, input.size)
                }
            } else {
                var editedUrl: String? = dbUrl
                if (intent.getStringExtra("path") != null) editedUrl += intent.getStringExtra("path")
                // Then let's mutilate the data until it's a map again... There may be a better way
                // to do this.
                if (intent.getSerializableExtra("params") != null) {
                    editedUrl += "?"
                    val params = intent.getSerializableExtra("params")
                    val gson = Gson()
                    val json = gson.toJson(params)
                    val stringStringMap = object : TypeToken<Map<String?, String?>?>() {}.type
                    val map = gson.fromJson<Map<String, String>>(json, stringStringMap)
                    var i = 0
                    val iterator = map.entries.iterator()
                    // Go through all parameters
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        // Add "&" if not the first parameter
                        if (i != 0) editedUrl += "&" else i++
                        editedUrl += entry.key + "=" + entry.value + ""
                    }
                }
                url = URL(editedUrl)
                Log.d("DbCon", url.toString())
                httpConn = url.openConnection() as HttpURLConnection
                httpConn.requestMethod = "GET"
            }
            // Set timeout
            httpConn.connectTimeout = 5000 //set timeout to 5 seconds
            // Get ready to read
            val bufferedReader = BufferedReader(InputStreamReader(httpConn.inputStream))
            response.putString("Data", bufferedReader.readLine())
            Log.d("DatabaseConnection", response.getString("Data"))
            httpConn.disconnect()
            receiver!!.send(200, response)
        } catch (e: Exception) {
            Log.d("DatabaseConnection", "Error: $e")
            response.putString("Error", e.toString())
            receiver!!.send(404, response)
        } finally {
            Log.d("DatabaseConnection", "API call finished")
        }
    }

    companion object {
        /**
         * Unique job ID for this service.
         */
        const val JOB_ID = 1000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context?, work: Intent?) {
            enqueueWork(context!!, DatabaseConnection::class.java, JOB_ID, work!!)
        }

        //Change to match your local IP
        // IMPORTANT! Your device must be in the same LAN for this to work
        var dbUrl = "http://89.166.118.240:3000/"
    }
}