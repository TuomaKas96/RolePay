package com.example.rolepay

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "MainActivity started")

        //login button click listener
        findViewById<Button>(R.id.login_button).setOnClickListener {
            loginHandler(it)
        }
    }

    /*
    * TODO:  make loginHandler work with db
    *
    *  check input through db.
    * if input == ok -> login, create session and navigate to main view
    * else display error text (turn visible) and maybe highlight input field
    * */
    private fun loginHandler(view: View){
        val loginInput = findViewById<EditText>(R.id.login_input)
        val errorText = findViewById<TextView>(R.id.login_error_text)
        val testText = findViewById<TextView>(R.id.test_text)

        //remove this after testing
        testText.text = loginInput.text.toString()

        /*
        check input through db
        SELECT 1 FROM user WHERE private_token = '$loginInput.text.toString()'
        if (QUERY IS OK){
            set login as private_token for later use
            set public_token_value_text to public_token value from db
            navigate to main view
        }
        else{
            errorText.visibility = errorText.VISIBLE
        }
        */

        // Hide the keyboard
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}