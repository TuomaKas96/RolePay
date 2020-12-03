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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

class UserMainView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_user_main_view, container, false)
        val balance = v.findViewById(R.id.balance_value_text) as TextView
        balance.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.viewBalance)
        }
        val requestBtn = v.findViewById(R.id.new_request_button) as Button
        requestBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newRequest)
        }
        val paymentBtn = v.findViewById(R.id.new_payment_button) as Button
        paymentBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newPayment)
        }
        val balanceText = v.findViewById(R.id.balance_value_text) as TextView
        if (SubApplication.balanceAmount == null) {
            // Fetch balance
            val j = Intent()
            j.action = "ACTION_FETCH_BALANCE"
            j.putExtra("params", hashMapOf<String,String>("id" to SubApplication.balanceId.toString()))
            j.putExtra("path", "balance")
            j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                        balanceText.setText(resultData.getString("Data") + "€")

                    } else { // Failed API call
                        Log.i(
                            "UserBalance",
                            "Something went wrong: " + resultData.getString("Error")
                        )
                        balanceText.setText("0€")
                    }
                }
            })
            DatabaseConnection.enqueueWork(context, j)
        }else {
            balanceText.setText(SubApplication.balanceAmount.toString() + "€")
        }
        return v
    }
}