package com.example.adminwavesoffood

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwavesoffood.Model.UserModel
import com.example.adminwavesoffood.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignupActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var userName : String
    private lateinit var nameOfRestaurant : String
    private lateinit var email : String
    private lateinit var password : String


    private lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialisation of firebase auth
        auth = Firebase.auth
        //Initialisation of firebase database
        database = Firebase.database.reference
        binding.createButton.setOnClickListener{
            //get text from edit text
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            userName = binding.nameOfOwner.text.toString().trim()
            nameOfRestaurant = binding.restaurantName.text.toString().trim()

            if (userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all the details" , Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(email, password)
            }
        }
        binding.alreadyhavebutton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val locationList = arrayOf("Nanded" , "Pune" , "Mumbai" , "Delhi" , "Sangli" , "Kolhapur" , "Aurangabad" , "Parbhani")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listOfLocations
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Account Created Successfully" , Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Account Creation Failed" , Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure" , task.exception)
            }
        }

    }
    //saving data into firebase realtime database
    private fun saveUserData() {
        //get text from edit text
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        userName = binding.nameOfOwner.text.toString().trim()
        nameOfRestaurant = binding.restaurantName.text.toString().trim()

        val user = UserModel(email, password,userName, nameOfRestaurant)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //saving user data into firebase realtime database
        database.child("user").child(userId).setValue(user)
    }
}