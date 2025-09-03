package com.example.adminwavesoffood.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwavesoffood.databinding.OrderDetailsItemBinding

class OrderDetailsAdapter(
    private var context: Context,
    private var foodNames : MutableList<String>,
    private var foodImages : MutableList<String>,
    private var foodQuantities : MutableList<Int>,
    private var foodPrices : MutableList<String>
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsAdapter.OrderDetailsViewHolder {
        val binding = OrderDetailsItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return foodNames.size
    }
    inner class OrderDetailsViewHolder(private val binding : OrderDetailsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply {
                foodName.text = foodNames[position]
                quantity.text = foodQuantities[position].toString()
                foodPrice.text = "₹ " + foodPrices[position] + " /-"
                val uri = Uri.parse(foodImages[position])
                Glide.with(context).load(uri).into(foodImage)
            }
        }
    }
}