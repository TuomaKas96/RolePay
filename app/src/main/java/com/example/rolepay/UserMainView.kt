package com.example.rolepay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

class UserMainView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_user_main_view, container, false)
        val requestBtn = v.findViewById(R.id.new_request_button) as Button
        requestBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newRequest)
        }
        val paymentBtn = v.findViewById(R.id.new_payment_button) as Button
        paymentBtn.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.newPayment)
        }
        return v
    }
}