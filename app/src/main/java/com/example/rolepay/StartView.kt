package com.example.rolepay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start_view.*
import org.json.JSONArray
import org.json.JSONObject

class StartView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_start_view, container, false)
        val environmentBtn = v.findViewById(R.id.new_environment_button) as Button

        environmentBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newEnvironment)
        }
        val loginBtn = v.findViewById(R.id.login_button) as Button
        loginBtn.setOnClickListener {

            val loginInput = v.findViewById(R.id.login_input) as EditText
            val tokenValue = "'" + loginInput.text.toString() + "'"
            val errorText = v.findViewById(R.id.login_error_text) as TextView
            val loader = v.findViewById(R.id.progress_loader) as ProgressBar

            loader.visibility = View.VISIBLE //makes the loader spinny thing visible after pressing the button

            // Create an intent
            val i = Intent()
            // Create parameters, they will be added to the url/request body
            val params = hashMapOf<String,String>("token" to tokenValue)
            // Action is the name of intent, can be anything, currently used for debug
            i.action = "ACTION_LOGIN_AS_USER"
            // Add parameters to intent
            i.putExtra("params", params)
            // Set method
            i.putExtra("method", "GET")
            // Add url path, these can be checked from server/routes/index.js
            i.putExtra("path", "user/") // Could be for example "environment/name"
            // Add receiver (= code that handles the response). This is required
            i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("SubApplication", "Data retrieved: " + resultData.getString("Data"))

                        //hide the loader after the query
                        loader.visibility = View.INVISIBLE
                        //hide error message if visible from previous attempts
                        if (errorText.visibility == View.VISIBLE){
                            errorText.visibility = View.INVISIBLE
                        }

                        //TODO: parse resultData and add data to session or some such
                        //Log.d("StartView", "User id: " +  userID)

                        //TODO: Check whether it is an admin or a user that logins. If admin, use "R.id.loginAsAdmin"
                        //navigate to user_main_view
                        NavHostFragment.findNavController(this@StartView).navigate(R.id.loginAsUser)
                    } else { // Failed API call
                        Log.i("SubApplication", "Something went wrong: " + resultData.getString("Error"))

                        //hide the loader after the query
                        loader.visibility = View.INVISIBLE

                        //show error message after failed API call
                        errorText.visibility = View.VISIBLE

                    }
                }
            })
            val c = requireActivity().applicationContext;
            DatabaseConnection.enqueueWork(c, i)

        }
        return v
    }
}