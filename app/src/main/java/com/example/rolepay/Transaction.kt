package com.example.rolepay

import android.app.AlertDialog
import android.content.DialogInterface
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
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class Transaction : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_transaction, container, false)
        val typeSwitch = v.findViewById(R.id.switch_transaction) as Switch
        val payButton = v.findViewById(R.id.confirm_button_pay) as Button
        val publicToken = v.findViewById(R.id.editText_public_token) as EditText
        val amount = v.findViewById(R.id.editText_amount) as EditText
        val warning = v.findViewById(R.id.transaction_warning) as TextView
        // Change between payment and request
        typeSwitch.setOnClickListener {
            // Change button text accordingly
            if (getString(R.string.pay).equals(payButton.text)) {
                payButton.setText(getString(R.string.request))
                Log.d("Transaction", "Text was pay")
            }
            else {
                payButton.setText(getString(R.string.pay))
                Log.d("Transaction", "Text was request")
            }
        }
        payButton.setOnClickListener {
            // Check that inputs have been filled
            var valid = true
            if (publicToken.getText().isEmpty()) {
                warning.text = getString(R.string.empty_publicToken)
                valid = false
            }
            if (amount.getText().isEmpty() ) {
                warning.text = getString(R.string.empty_amount)
                valid = false
            }
            if (valid == true)
            sendTransaction(
                typeSwitch.isChecked,
                publicToken.getText().toString(),
                amount.text.toString().toDouble()
            )
        }
        return v
    }
    fun sendTransaction(method: Boolean, token: String, amount: Double) {
        MainActivity.loader.visibility = View.VISIBLE
        val userId = SubApplication.userId
        // Create intent
        val i = Intent()
        i.action = "ACTION_NEW_TRANSACTION"
        i.putExtra("method", "POST")
        i.putExtra("path", "transaction/add")
        val params = hashMapOf<String, Any?>(
            "amount" to amount,
            "sender" to userId,
            "receiver" to token
        )
        i.putExtra("params", params)

        i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                MainActivity.loader.visibility = View.INVISIBLE
                var myMsg: String
                if (resultCode == 200) {
                    Log.d("Transaction", "Data retrieved: " + resultData.getString("Data"))
                    MainActivity.makeToast("Maksu onnistui!")
                    UserBalance.fetchBalance(null)
                    parentFragment?.let {
                        NavHostFragment.findNavController(it).navigate(R.id.userMainView)
                    }
                } else { // Failed API call
                    Log.i("Transaction", "Something went wrong: " + resultData.getString("Error"))
                    MainActivity.makeToast("Maksu epäonnistui: " + resultData.getString("Error"))
                }
            }
        })
        DatabaseConnection.enqueueWork(context, i)
    }
}