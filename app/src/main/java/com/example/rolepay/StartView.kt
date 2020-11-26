package com.example.rolepay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start_view.*

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
            //TODO: Check whether it is an admin or a user that logins. If admin, use "R.id.loginAsAdmin"
            NavHostFragment.findNavController(this).navigate(R.id.loginAsUser)
        }
        return v
    }
}