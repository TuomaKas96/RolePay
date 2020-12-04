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
        // Change between payment and request
        //TODO: Requests not possible yet
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
            //TODO: Add error messages if input is not correct

            // Check that inputs have been filled
            var valid = true
            if (publicToken.getText().isEmpty())
                valid = false
            if (amount.getText().isEmpty() )
                valid = false
            if (valid)
            sendTransaction(
                typeSwitch.isChecked,
                publicToken.getText().toString(),
                amount.text.toString().toDouble()
            )
        }
        return v
    }
    fun sendTransaction(method: Boolean, token: String, amount: Double) {
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
                var myMsg: String
                if (resultCode == 200) {
                    Log.d("Transaction", "Data retrieved: " + resultData.getString("Data"))
                    myMsg = "Transaction complete!"
                    UserBalance.fetchBalance(null)

                } else { // Failed API call
                    Log.i("Transaction", "Something went wrong: " + resultData.getString("Error"))
                    myMsg = "Transaction failed, error: " + resultData.getString("Error")
                }
                // Show a popup window after call is ready
                //TODO: Show a spinner when processing request
                val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
                dialog.setMessage(myMsg)
                dialog.setTitle("Transaction complete")
                dialog.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        // Return to usermainview after successful transaction
                        if (resultCode == 200)
                        parentFragment?.let {
                            NavHostFragment.findNavController(it).navigate(R.id.userMainView)
                        }
                    })
                val alertDialog: AlertDialog = dialog.create()
                alertDialog.show()

            }
        })
        DatabaseConnection.enqueueWork(context, i)
    }
}