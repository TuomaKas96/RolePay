package com.example.rolepay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "MainActivity started")
    }

    //Called when app loses focus
    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "MainActivity paused")
    }

    //Called when app has focus again
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "MainActivity resumed")
    }

    //Called when app is no longer visible
    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "MainActivity stopped")
    }

    //Called when app is restarted
    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "MainActivity restarted")
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "MainActivity started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "MainActivity destroyed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MainActivity", "MainActivity instance state saved")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("MainActivity", "MainActivity instance state restored")
    }
}