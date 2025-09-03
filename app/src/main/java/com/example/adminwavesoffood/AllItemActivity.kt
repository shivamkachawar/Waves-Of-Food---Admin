package com.example.adminwavesoffood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwavesoffood.Adapter.MenuItemAdapter
import com.example.adminwavesoffood.Model.AllMenu
import com.example.adminwavesoffood.databinding.ActivityAllItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class AllItemActivity : AppCompatActivity() {
    private lateinit var databaseReference : DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems : ArrayList<AllMenu> = ArrayList()

    private lateinit var binding : ActivityAllItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()


        binding.backNavigation.setOnClickListener{
            finish()
        }

    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef : DatabaseReference = database.reference.child("menu")
        //fetch data from database
        foodRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear existing data before populating
                menuItems.clear()
                //loop through each food item
                for (foodSnapShot in snapshot.children){
                    val menuItem = foodSnapShot.getValue(AllMenu :: class.java)
                    menuItem?.let{
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError" , "Error : ${error.message}")
            }
        })

    }

    private fun setAdapter() {

        val adapter = MenuItemAdapter(this@AllItemActivity , menuItems , databaseReference){ position ->
            deleteMenuItems(position)
        }
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(this@AllItemActivity)
        binding.menuRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun deleteMenuItems(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuRef = database.reference.child("menu").child(menuItemKey!!)
        foodMenuRef.removeValue().addOnCompleteListener() { task ->
            if(task.isSuccessful){
                menuItems.removeAt(position)
                binding.menuRecyclerView.adapter?.notifyItemRemoved(position)
            }
            else{
                Toast.makeText(this , "Item deletion failed" , Toast.LENGTH_SHORT).show()
            }
        }
    }

}