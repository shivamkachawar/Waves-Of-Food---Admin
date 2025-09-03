package com.example.adminwavesoffood

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminwavesoffood.Model.AllMenu
import com.example.adminwavesoffood.databinding.ActivityAddItemBinding
import com.example.adminwavesoffood.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class AddItemActivity : AppCompatActivity() {

    //Food item details for Storing in Realtime database
    private lateinit var foodName : String
    private lateinit var foodPrice : String
    private lateinit var foodDescription : String
    private lateinit var foodIngredient : String
    private var foodImageUri : Uri? = null
    //Firebase
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var binding : ActivityAddItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialising Firebase & Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        binding.addItemButton.setOnClickListener{
            //getting data from fields
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.FoodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredients.text.toString().trim()

            if(!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredient.isBlank())){
                uploadData()
                Toast.makeText(this, "Item added successfully" , Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Please fill all the fields" , Toast.LENGTH_SHORT).show()
            }
        }
        binding.selectImage.setOnClickListener{
            pickImage.launch("image/*")

        }
        binding.backNavigation.setOnClickListener{
            finish()
        }
    }

    private fun uploadData() {
        Log.d("AddItemActivity", "Current user UID: ${auth.currentUser?.uid}, Email: ${auth.currentUser?.email}")
        //get a reference to the "menu" node in the database
        val menuRef = database.getReference("menu")
        //generate unique key for each new menu item
        val newItemKey = menuRef.push().key
        if(foodImageUri != null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    //Create a new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodImage = downloadUrl.toString()
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Data upload failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            }
                .addOnFailureListener{
                    Toast.makeText(this, "Image upload failed" , Toast.LENGTH_SHORT).show()
                }
        }
        else{
            Toast.makeText(this, "Please select an image" , Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.selectedImage.setImageURI(uri)
            foodImageUri = uri

        }
    }
}