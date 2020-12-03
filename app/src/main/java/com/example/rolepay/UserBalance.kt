package com.example.rolepay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.sql.Timestamp
import kotlin.collections.ArrayList

class UserBalance : Fragment() {
    companion object {
        fun fetchBalance(textView: TextView?) {
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
                        if (textView != null)
                        textView.setText(resultData.getString("Data") + "€")
                        SubApplication.balanceAmount = resultData.getString("Data")?.toDouble()
                    } else { // Failed API call
                        Log.i(
                            "UserBalance",
                            "Something went wrong: " + resultData.getString("Error")
                        )
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
        val v = inflater.inflate(R.layout.activity_user_balance, container, false)
        val balanceText = v.findViewById(R.id.balance_value_text) as TextView
        val recyclerView = v.findViewById(R.id.transaction_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var transactionEvents = ArrayList<TransactionEvent>()
        val gson = Gson()
        // Fetch events
        val i = Intent()
        i.action = "ACTION_FETCH_TRANSACTIONS"
        i.putExtra("params", hashMapOf<String,String>("id" to SubApplication.userId.toString()))
        i.putExtra("path", "user/transaction")
        i.putExtra("RECEIVER", object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                super.onReceiveResult(resultCode, resultData)
                // Successful API call
                if (resultCode == 200) {
                    Log.d("UserBalance", "Data retrieved: " + resultData.getString("Data"))
                    val data = gson.fromJson(resultData.getString("Data"), Array<TransactionEvent>::class.java)
                    for (event: TransactionEvent in data){
                        if (event.sender == SubApplication.userId){
                            event.isNegative = true
                            event.publicToken = "adhnadhuia"
                        }else {
                            event.isNegative = false
                            event.publicToken = "adaidjwsoia"
                        }
                        transactionEvents.add(event)
                        recyclerView.adapter = CustomAdapter(transactionEvents)
                    }
                } else { // Failed API call
                    Log.i("UserBalance", "Something went wrong: " + resultData.getString("Error"))
                }
            }
        })
        DatabaseConnection.enqueueWork(context, i)
        if (SubApplication.balanceAmount == null) {
            fetchBalance(balanceText)
        }else {
            balanceText.setText(SubApplication.balanceAmount.toString() + "€")
        }
        return v
    }
}
class CustomAdapter(val eventList: ArrayList<TransactionEvent>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_event_layout, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(eventList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return eventList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(event: TransactionEvent) {
            Log.d("UserBalance", "Binding data into RecyclerView")
            val tokenView = itemView.findViewById(R.id.publicTokenText) as TextView
            val amountView  = itemView.findViewById(R.id.amountText) as TextView
            val timeView  = itemView.findViewById(R.id.timeView) as TextView
            tokenView.text = event.publicToken
            amountView.text = event.amount.toString() + "€"
            timeView.text = event.timestamp.toLocaleString()
        }
    }
}
data class TransactionEvent (
    val timestamp: Timestamp,
    val transaction_id: Int,
    val amount: Double,
    val sender: Int,
    val receiver: Int,
    var isNegative: Boolean,
    var publicToken: String)