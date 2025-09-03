package com.example.adminwavesoffood

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.adminwavesoffood.Model.UserModel
import com.example.adminwavesoffood.databinding.ActivityAdminProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminProfileBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var adminRef : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        adminRef = database.reference.child("user")
        binding.backNavigation.setOnClickListener{
            finish()
        }
        binding.saveInfoButton.setOnClickListener{
            updateUserData()
        }
        setFieldsEnabled(false)
        binding.saveInfoButton.visibility = View.GONE

        binding.clickToEdit.setOnClickListener{
            setFieldsEnabled(true)
            binding.saveInfoButton.visibility = View.VISIBLE
        }
        retrieveUserData()

    }

    private fun updateUserData() {
        val updateName = binding.nameField.text.toString()
        val updateEmail = binding.emailField.text.toString()
        val updatePassword = binding.passwordField.text.toString()
        val updateAddress = binding.addressField.text.toString()
        val updatePhone = binding.phoneField.text.toString()

        val userId = auth.currentUser?.uid
        if(userId != null){
            val userRef = database.getReference("user").child(userId)
            val userData = mapOf(
                "name" to updateName,
                "address" to updateAddress,
                "email" to updateEmail,
                "phone" to updatePhone,
                "password" to updatePassword
            )
            userRef.updateChildren(userData).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully" , Toast.LENGTH_SHORT).show()
                setFieldsEnabled(false)
                binding.saveInfoButton.visibility = View.GONE
            }.addOnFailureListener{
                Toast.makeText(this, "Profile update failed" , Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun setFieldsEnabled(enabled: Boolean) {
        val fields = listOf(binding.nameField, binding.phoneField, binding.addressField , binding.passwordField)

        for (field in fields) {
            if (enabled) {
                // Editing mode
                field.isFocusable = true
                field.isFocusableInTouchMode = true
                field.isClickable = true
                field.background = resources.getDrawable(android.R.drawable.edit_text, null) // default editable look
            } else {
                // View mode
                field.isFocusable = false
                field.isClickable = false
                field.background = null // remove underline/grey look
            }
        }

    }
    private fun retrieveUserData() {
        val currentUser = auth.currentUser?.uid
        if(currentUser != null){
            val userRef = adminRef.child(currentUser)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var name = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()
                        var password = snapshot.child("password").getValue()
                        setDataToTextView(name, email, password , address , phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
    private fun setDataToTextView(name : Any? , email : Any? , password : Any? , address : Any? , phone : Any?){
        binding.nameField.setText(name.toString())
        binding.emailField.setText(email.toString())
        binding.passwordField.setText(password.toString())
        binding.addressField.setText(address.toString())
        binding.phoneField.setText(phone .toString())
    }

}