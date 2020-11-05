package com.example.rolepay

import android.app.Application
import android.content.Intent
import android.util.Log

// This is where code commonly used all around the app will be written
class SubApplication : Application() {
    // Runs once when app starts
    override fun onCreate() {
        super.onCreate()
        Log.d("MainActivity", "App starting")
        val i = Intent()
        //i.setAction("ACTION_TEST_CONNECTION")
        i.setAction("ACTION_FETCH_USER")
        DatabaseConnection.enqueueWork(this, i)
    }
}