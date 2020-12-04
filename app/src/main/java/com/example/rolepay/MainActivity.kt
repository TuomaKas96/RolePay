package com.example.rolepay

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "MainActivity started")

    }

    // Add control for back button
    override fun onBackPressed()
    {
        // do stuff
        super.onBackPressed();
        Log.d("MainActivity", "Back was pressed")
        val destination = NavHostFragment.findNavController(nav_host_fragment).getCurrentDestination()?.label
        Log.d("MainActivity", "Destination: " + destination)
        if (destination != null) {
            if (destination.equals("activity_start_view")){
                UserMainView.logout(this, nav_host_fragment)
        }}
    }
}