package com.example.adminwavesoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwavesoffood.Adapter.PendingOrderAdapter
import com.example.adminwavesoffood.Model.OrderDetails
import com.example.adminwavesoffood.databinding.ActivityPendingOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity() , PendingOrderAdapter.OnItemClicked {

    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItems: MutableList<OrderDetails> = mutableListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("orderDetails")

        //get order details
        getOrdersDetails()


        binding.backNavigation.setOnClickListener {
            finish()
        }
    }

    private fun getOrdersDetails() {
        //retrieve order details from firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItems.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItems) {
            //add data to respective list for populating the recycler view
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItems[position]
        intent.putExtra("userOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // handle item acceptance and update database
        val childItemPushKey = listOfOrderItems[position].itemPushKey
        val clickItemOrderRef = childItemPushKey?.let {
            database.reference.child("orderDetails").child(it)
        }
        clickItemOrderRef?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    private fun updateOrderAcceptStatus(position: Int) {
        // update order acceptance in user's buy history and order details
        val userIdOfClickedItem = listOfOrderItems[position].userId
        val pushKeyOfClickedItem = listOfOrderItems[position].itemPushKey
        val buyHistoryRef =
            database.reference.child("user").child(userIdOfClickedItem!!).child("buyHistory")
                .child(pushKeyOfClickedItem!!)
        buyHistoryRef.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }

    override fun onItemDispatchClickListener(position: Int) {
        // handle item dispatch and update database
        val dispatchItemPushKey = listOfOrderItems[position].itemPushKey
        val dispatchItemOrderRef =
            database.reference.child("completedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderRef.setValue(listOfOrderItems[position])
            .addOnSuccessListener {
                deleteThisItemFromPendingOrders(dispatchItemPushKey)
            }
    }

    private fun deleteThisItemFromPendingOrders(dispatchItemPushKey: String) {
        val orderDetailItemRef = database.reference.child("orderDetails").child(dispatchItemPushKey)
        orderDetailItemRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order is dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order dispatch failed", Toast.LENGTH_SHORT).show()
            }
    }

}