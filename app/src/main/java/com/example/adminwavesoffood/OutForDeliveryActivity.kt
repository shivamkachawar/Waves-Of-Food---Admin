package com.example.adminwavesoffood

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwavesoffood.Adapter.DeliveryAdapter
import com.example.adminwavesoffood.Model.OrderDetails
import com.example.adminwavesoffood.databinding.ActivityOutForDeliveryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOutForDeliveryBinding
    private lateinit var database : FirebaseDatabase
    private  var completeOrderList : ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOutForDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backNavigation.setOnClickListener{
            finish()
        }

        // retrieve & display accepted orders
        retrieveCompleteOrderDetails()

    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val completeOrderRef = database.reference.child("completedOrder").orderByChild("currentTime")
        completeOrderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completeOrderList.clear()
                for (orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        completeOrderList.add(it)
                    }
                }
                completeOrderList.reverse()
                setDataIntoRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setDataIntoRecyclerView() {
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()
        for (order in completeOrderList){
            order.userName?.let { customerName.add(it) }
           moneyStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customerName , moneyStatus)
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.deliveryRecyclerView.adapter = adapter
    }
}