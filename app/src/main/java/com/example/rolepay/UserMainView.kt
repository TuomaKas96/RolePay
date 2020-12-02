package com.example.rolepay

import android.os.Bundle
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

        //TODO: add logout button and functionality.
        // lock the "back" button so user can't return to start view unless logging out

        //TODO: show balance value and add navigation to balance view (onClick for balance value)

        //sets public token text as the one from storage
        val publicTokenValue = v.findViewById(R.id.public_token_val_text) as TextView
        publicTokenValue.text = SubApplication.publicToken.toString()

        //navigate to new request
        val requestBtn = v.findViewById(R.id.new_request_button) as Button
        requestBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newRequest)
        }
        //navigate to new payment
        val paymentBtn = v.findViewById(R.id.new_payment_button) as Button
        paymentBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newPayment)
        }
        return v
    }
}