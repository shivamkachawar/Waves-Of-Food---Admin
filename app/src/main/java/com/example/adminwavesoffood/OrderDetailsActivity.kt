package com.example.adminwavesoffood

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwavesoffood.Adapter.OrderDetailsAdapter
import com.example.adminwavesoffood.Model.OrderDetails
import com.example.adminwavesoffood.databinding.ActivityAddItemBinding
import com.example.adminwavesoffood.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrderDetailsBinding
    private var userName : String ?= null
    private var address : String ?= null
    private var phone : String ?= null
    private var totalPrice : String ?= null

    private var foodNames : MutableList<String> = mutableListOf()
    private var foodImages : MutableList<String> = mutableListOf()
    private var foodQuantities : MutableList<Int> = mutableListOf()
    private var foodPrices : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            finish()
        }
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receivedOrderDetails = intent.getParcelableExtra<OrderDetails>("userOrderDetails")
        if(receivedOrderDetails != null){
            userName = receivedOrderDetails.userName
            address = receivedOrderDetails.address
            phone = receivedOrderDetails.phoneNumber
            totalPrice = receivedOrderDetails.totalPrice
            foodNames = receivedOrderDetails.foodNames!!
            foodImages = receivedOrderDetails.foodImages!!
            foodPrices = receivedOrderDetails.foodPrices!!
            foodQuantities = receivedOrderDetails.foodQuantities!!
        }

        setUserDetails()
        setAdapter()
    }

    private fun setAdapter() {
        binding.orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this , foodNames, foodImages, foodQuantities , foodPrices)
        binding.orderDetailsRecyclerView.adapter = adapter
    }

    private fun setUserDetails() {
        binding.nameField.text = userName
        binding.addressField.text = address
        binding.phoneField.text = phone
        binding.totalPayField.text = totalPrice
    }

}