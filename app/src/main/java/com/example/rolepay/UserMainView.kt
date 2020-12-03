package com.example.rolepay

import android.content.Intent
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
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

        //TODO: lock the "back" button so user can't return to start view unless logging out


        //creates a dialog from which user can then choose to logout or cancel
        val logoutBtn = v.findViewById(R.id.logout_button) as Button
        logoutBtn.setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            //set title for alert dialog
            builder.setTitle(R.string.logout_dialog_title)
            //set message for alert dialog
            builder.setMessage(R.string.logout_dialog_msg)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                //sets all storage data back to null
                SubApplication.userId = null
                SubApplication.publicToken = null
                SubApplication.privateToken = null
                SubApplication.admin = null
                SubApplication.balanceId = null
                SubApplication.environmentId = null

                //navigate to StartView
                NavHostFragment.findNavController(this).navigate(R.id.returnToStartView)
            }
            //performing cancel action
            builder.setNeutralButton("Cancel"){dialogInterface , which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }

        //sets public token text as the one from storage
        val publicTokenValue = v.findViewById(R.id.public_token_val_text) as TextView
        publicTokenValue.text = SubApplication.publicToken.toString()

        //navigate to new payment
        val paymentBtn = v.findViewById(R.id.new_payment_button) as Button
        paymentBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newPayment)
        }
        val balanceText = v.findViewById(R.id.balance_value_text) as TextView
        if (SubApplication.balanceAmount == null) {
            // Fetch balance
            UserBalance.fetchBalance(balanceText)
        }else {
            balanceText.setText(SubApplication.balanceAmount.toString() + "€")
        }
        return v
    }
}