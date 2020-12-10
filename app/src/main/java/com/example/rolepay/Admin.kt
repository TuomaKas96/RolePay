package com.example.rolepay

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class Admin : Fragment() {

    companion object {
        val gson = Gson()
        lateinit var recyclerView: RecyclerView
        lateinit var environmentName: EditText
        lateinit var adminContext: Context
        fun createUser () {
            MainActivity.loader.visibility = VISIBLE
            val i = Intent()
            i.action = "ACTION_NEW_USER"
            i.putExtra("params", hashMapOf<String,String>("environment" to SubApplication.environmentId.toString()))
            i.putExtra("path", "user/add")
            i.putExtra("method", "POST")
            i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    MainActivity.loader.visibility = INVISIBLE
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                        fetchUsers()
                        MainActivity.makeToast("New user added")
                    } else { // Failed API call
                        Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                        MainActivity.makeToast("Error: " + resultData.getString("Error"))
                    }
                }
            })
            DatabaseConnection.enqueueWork(SubApplication.appContext, i)
        }
        fun updateUser (userId: Int, publicToken: String, balance: String?, balanceId: Int) {
            var params = hashMapOf<String,Any>("id" to userId, "public" to publicToken)
            if (balance != null){
                params.put("balance", balance)
                params.put("balanceId", balanceId)
            }
            MainActivity.loader.visibility = VISIBLE
            val i = Intent()
            i.action = "ACTION_UPDATE_USER"
            i.putExtra("params", params)
            i.putExtra("path", "user/update")
            i.putExtra("method", "POST")
            i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    MainActivity.loader.visibility = INVISIBLE
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                        fetchUsers()
                        MainActivity.makeToast("User updated")
                    } else { // Failed API call
                        Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                        MainActivity.makeToast("Error: " + resultData.getString("Error"))
                    }
                }
            })
            DatabaseConnection.enqueueWork(SubApplication.appContext, i)
        }
        fun deleteUser (id: Int, balanceId: Int, context: Context) {
            val builder = AlertDialog.Builder(context)
            //set title for alert dialog
            builder.setTitle(R.string.remove_user)
            //set message for alert dialog
            builder.setMessage(R.string.remove_user_msg)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                MainActivity.loader.visibility = VISIBLE
                val j = Intent()
                j.action = "ACTION_DELETE_USER"
                j.putExtra("params", hashMapOf<String,Any>("id" to id, "balanceId" to balanceId))
                j.putExtra("path", "user/remove")
                j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                        super.onReceiveResult(resultCode, resultData)
                        MainActivity.loader.visibility = INVISIBLE
                        // Successful API call
                        if (resultCode == 200) {
                            Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                            fetchUsers()
                            MainActivity.makeToast("User deleted")
                        } else { // Failed API call
                            Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                            MainActivity.makeToast("Error: " + resultData.getString("Error"))
                        }
                    }
                })
                DatabaseConnection.enqueueWork(SubApplication.appContext, j)
            }
            //performing cancel action
            builder.setNeutralButton("Cancel"){dialogInterface , which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.show()

        }
        fun updateEnvironmentName (name: String) {
            MainActivity.loader.visibility = VISIBLE
            val j = Intent()
            j.action = "ACTION_UPDATE_ENVIRONMENT_NAME"
            j.putExtra("params", hashMapOf<String,Any>("id" to SubApplication.environmentId.toString(), "name" to name))
            j.putExtra("path", "environment/update")
            j.putExtra("method", "POST")
            j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    MainActivity.loader.visibility = INVISIBLE
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                    } else { // Failed API call
                        Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                        MainActivity.makeToast("Error: " + resultData.getString("Error"))
                    }
                }
            })
            DatabaseConnection.enqueueWork(SubApplication.appContext, j)
        }
        fun removeEnvironment (id: Int, fragment: Fragment, context: Context) {
            val builder = AlertDialog.Builder(context)
            //set title for alert dialog
            builder.setTitle(R.string.remove_environment)
            //set message for alert dialog
            builder.setMessage(R.string.remove_environment_msg)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                MainActivity.loader.visibility = VISIBLE
                val j = Intent()
                j.action = "ACTION_DELETE_ENVIRONMENT"
                j.putExtra("params", hashMapOf<String,Any>("id" to id))
                j.putExtra("path", "environment/remove")
                j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                        super.onReceiveResult(resultCode, resultData)
                        MainActivity.loader.visibility = INVISIBLE
                        // Successful API call
                        if (resultCode == 200) {
                            Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                            NavHostFragment.findNavController(fragment).navigate(R.id.startView)
                            MainActivity.makeToast("Environment deleted")
                        } else { // Failed API call
                            Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                            MainActivity.makeToast("Error: " + resultData.getString("Error"))
                        }
                    }
                })
                DatabaseConnection.enqueueWork(SubApplication.appContext, j)
            }
            //performing cancel action
            builder.setNeutralButton("Cancel"){dialogInterface , which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.show()

        }
        fun fetchUsers() {
            MainActivity.loader.visibility = VISIBLE
            val i = Intent()
            i.action = "ACTION_FETCH_USERS"
            i.putExtra("params", hashMapOf<String,String>("id" to SubApplication.environmentId.toString()))
            i.putExtra("path", "environment/users")
            i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    MainActivity.loader.visibility = INVISIBLE
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
                        MainActivity.makeToast("Error: " + resultData.getString("Error"))
                    }
                }
            })
            DatabaseConnection.enqueueWork(SubApplication.appContext, i)
        }
        fun fetchEnvironmentName () {
            MainActivity.loader.visibility = VISIBLE
            val j = Intent()
            j.action = "ACTION_FETCH_ENVIRONMENT_NAME"
            j.putExtra("params", hashMapOf<String,Any>("id" to SubApplication.environmentId.toString()))
            j.putExtra("path", "environment/name")
            j.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    super.onReceiveResult(resultCode, resultData)
                    MainActivity.loader.visibility = INVISIBLE
                    // Successful API call
                    if (resultCode == 200) {
                        Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                        environmentName.setText(resultData.getString("Data").toString())
                    } else { // Failed API call
                        Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                        MainActivity.makeToast("Error: " + resultData.getString("Error"))
                    }
                }
            })
            DatabaseConnection.enqueueWork(SubApplication.appContext, j)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.activity_admin, container, false)
        adminContext = v.context
        recyclerView = v.findViewById(R.id.users_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        environmentName = v.findViewById(R.id.environment_name) as EditText
        val newUserBtn = v.findViewById(R.id.new_user_btn) as Button
        val saveEnvironmentName = v.findViewById(R.id.save_name_btn) as Button
        val deleteEnvironment = v.findViewById(R.id.remove_environment) as Button
        val privateToken = v.findViewById(R.id.private_token) as TextView
        privateToken.text = SubApplication.privateToken
        fetchEnvironmentName()
        fetchUsers()
        newUserBtn.setOnClickListener {
            createUser()
        }
        saveEnvironmentName.setOnClickListener {
            updateEnvironmentName(environmentName.text.toString())
        }
        deleteEnvironment.setOnClickListener {
            SubApplication.environmentId?.let { it1 -> context?.let { it2 ->
                removeEnvironment(it1, this,
                    it2
                )
            } }
        }
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
            val saveBtn = itemView.findViewById(R.id.save_btn) as Button
            val deleteUser = itemView.findViewById(R.id.remove_user_btn) as Button
            saveBtn.setOnClickListener {
                var newBalance: String? = null
                if (!balance.text.equals(user.balance.toString()))
                    newBalance = balance.text.toString()
                Admin.updateUser(user.user_id, publicToken.text.toString(), newBalance, user.balance_id)
            }
            deleteUser.setOnClickListener {
                Admin.deleteUser(user.user_id, user.balance_id, Admin.adminContext)
            }
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