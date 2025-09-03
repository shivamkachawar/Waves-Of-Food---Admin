package com.example.adminwavesoffood.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwavesoffood.PendingOrderActivity
import com.example.adminwavesoffood.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val context: PendingOrderActivity,
    private val customerNames: MutableList<String>,
    private val quantities: MutableList<String>,
    private val foodImages: MutableList<String>,
    private val itemClicked : OnItemClicked
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {
interface OnItemClicked {
    fun onItemClickListener(position: Int)
    fun onItemAcceptClickListener(position: Int)
    fun onItemDispatchClickListener(position: Int)
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return customerNames.size
    }
    inner class PendingOrderViewHolder(private val binding : PendingOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position : Int){
            binding.apply{
                customerName.text = customerNames[position]
                quantity.text = quantities[position]
                val uri = Uri.parse(foodImages[position])


                acceptButton.apply{
                    if(!isAccepted){
                        text = "Accept"
                    }
                    else{
                        text = "Dispatch"
                    }
                    setOnClickListener{
                        if(!isAccepted){
                            text = "Dispatched"
                            isAccepted = true
                            showToast("Order is Accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        }
                        else{
                            customerNames.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order is dispatched")
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener{
                    itemClicked.onItemClickListener(position)
                }

            }
        }
        private fun showToast(msg : String){
            Toast.makeText(context , msg , Toast.LENGTH_SHORT).show()
        }
    }
}