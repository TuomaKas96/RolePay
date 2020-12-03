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
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start_view.*
import org.json.JSONArray
import org.json.JSONException
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

            //TODO: maybe turn the code below into a separate function?

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
                        Log.d("StartView", "Data retrieved: " + resultData.getString("Data"))

                        //check for errors in data
                        var error = false
                        try{
                            //parse data
                            val jsonArray = JSONArray(resultData.getString("Data"))
                            val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                            //this is how you get a specific value from the JSON object
                            //val userID = jsonObject.get("user_id")
                        }
                        catch(e: JSONException){
                            //log the exception and set error as true
                            Log.d("StartView", "Error while logging in: " + e.toString())
                            error = true
                        }

                        //login failed if errors in data
                        if(error){
                            //hide the loader
                            loader.visibility = View.INVISIBLE

                            //show error message after failed API call
                            errorText.visibility = View.VISIBLE
                        }
                        //otherwise complete login with given data
                        else{
                            val jsonArray = JSONArray(resultData.getString("Data"))
                            val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                            //add data to storage for later use
                            SubApplication.userId = jsonObject.get("user_id") as Int?
                            SubApplication.publicToken = jsonObject.get("public_token") as String?
                            SubApplication.privateToken = jsonObject.get("private_token") as String?
                            SubApplication.admin = jsonObject.get("admin") as Int?
                            SubApplication.balanceId = jsonObject.get("balance_id") as Int?
                            SubApplication.environmentId = jsonObject.get("environment_id") as Int?

                            //hide the loader after the query
                            loader.visibility = View.INVISIBLE
                            //hide error message if visible from previous attempts
                            if (errorText.visibility == View.VISIBLE){
                                errorText.visibility = View.INVISIBLE
                            }

                            //Check whether it is an admin or a user that logins.
                            if(jsonObject.get("admin") == 0){
                                //navigate to user_main_view
                                NavHostFragment.findNavController(this@StartView).navigate(R.id.loginAsUser)
                            }
                            else{
                                //navigate to user_main_view as admin
                                NavHostFragment.findNavController(this@StartView).navigate(R.id.loginAsAdmin)
                            }
                        }


                    } else { // Failed API call
                        Log.i("SubApplication", "Something went wrong: " + resultData.getString("Error"))

                        //hide the loader
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