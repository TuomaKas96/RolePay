package com.example.rolepay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class Admin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.activity_admin, container, false)
        val recyclerView = v.findViewById(R.id.users_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val environmentName = v.findViewById(R.id.environment_name) as EditText
        val gson = Gson()

        val j = Intent()
        j.action = "ACTION_FETCH_ENVIRONMENT_NAME"
        j.putExtra("params", hashMapOf<String,Any>("id" to SubApplication.environmentId.toString()))
        j.putExtra("path", "environment/name")
        j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                // Successful API call
                if (resultCode == 200) {
                    Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                    environmentName.setText(resultData.getString("Data").toString())
                } else { // Failed API call
                    Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                }
            }
        })
        DatabaseConnection.enqueueWork(context, j)

        val i = Intent()
        i.action = "ACTION_FETCH_USERS"
        i.putExtra("params", hashMapOf<String,String>("id" to SubApplication.environmentId.toString()))
        i.putExtra("path", "environment/users")
        i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                // Successful API call
                if (resultCode == 200) {
                    Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                    val data = gson.fromJson(resultData.getString("Data"), Array<User>::class.java)
                    var userList = ArrayList<User>()
                        for(user: User in data)
                            userList.add(user)
                        recyclerView.adapter = UserRecyclerAdapter(userList)

                } else { // Failed API call
                    Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                }
            }
        })
        DatabaseConnection.enqueueWork(context, i)

        return v
    }
}
class UserRecyclerAdapter(val userList: ArrayList<User>) : RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_card, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: User) {
            Log.d("UserBalance", "Binding data into RecyclerView")
            val publicToken = itemView.findViewById(R.id.public_token_editText) as EditText
            val privateToken = itemView.findViewById(R.id.private_token_editText) as EditText
            val balance = itemView.findViewById(R.id.balance_editText) as EditText
            publicToken.setText(user.public_token)
            privateToken.setText(user.private_token)
            balance.setText(user.balance.toString())
        }
    }
}
data class User (
    var public_token: String,
    var private_token: String,
    var balance: Double,
    val user_id: Int,
    val balance_id: Int
)