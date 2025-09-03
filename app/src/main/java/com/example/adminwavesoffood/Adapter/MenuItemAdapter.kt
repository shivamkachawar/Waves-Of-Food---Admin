package com.example.adminwavesoffood.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwavesoffood.Model.AllMenu
import com.example.adminwavesoffood.databinding.ItemItemBinding
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener : (position : Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AllItemViewHolder>() {
    private val itemQuantities = IntArray(menuList.size){1}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    inner class AllItemViewHolder(private val binding : ItemItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position : Int){
            binding.apply {
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val uriString = menuItem.foodImage
                val uri = Uri.parse(uriString)
                foodName.text = menuItem.foodName
                foodPrice.text = menuItem.foodPrice
                Glide.with(context).load(uri).into(foodImage)



                deleteButton.setOnClickListener {
                    onDeleteClickListener(position)
                }
            }
        }



    }


}