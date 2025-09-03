package com.example.adminwavesoffood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.adminwavesoffood.Model.UserModel
import com.example.adminwavesoffood.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.Firebase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private var userName: String? = null
    private var nameOfRestaurant: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityLoginBinding
    private var isSigningIn  = false
    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = Firebase.auth
        database = Firebase.database.reference

        // Email/password login (your existing flow)
        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createUserAccount(email, password)
            }
        }

        binding.alreadyhavebutton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Google sign-in button — Credential Manager flow
        binding.googleSignIn.setOnClickListener {
            launchGoogleSignIn()
        }
    }

    // (your original email/password login + createUserAccount stays the same)
    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        saveUserData()
                        updateUI(auth.currentUser)
                    } else {
                        Toast.makeText(this, "Create User & Login Failed", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "createUserAccount : Login Failed", task2.exception)
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        // careful: storing plain password is not recommended; keep it if your model requires it
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(email, password, userName, nameOfRestaurant)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }

    // ---------------- Credential Manager Google Sign-In ----------------
    private fun launchGoogleSignIn() {
        // Build GetGoogleIdOption — server client id (web client id) required
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id)) // must be web client id
            .setFilterByAuthorizedAccounts(false) // only accounts previously used (change if you want all)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        // call in coroutine (getCredential is suspend)
        lifecycleScope.launch {
            try {
                val response = credentialManager.getCredential(context = this@LoginActivity, request = request)
                handleGoogleCredentialResponse(response.credential)
            } catch (e: GetCredentialException) {
                // user cancelled, no credentials, or other recoverable issues
                Log.w(TAG, "Credential Manager getCredential failed", e)
                Toast.makeText(this@LoginActivity, "Google sign-in cancelled or unavailable", Toast.LENGTH_SHORT).show()
            } catch (t: Throwable) {
                Log.e(TAG, "Unexpected error during Google sign-in", t)
                Toast.makeText(this@LoginActivity, "Google sign-in error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleCredentialResponse(credential: androidx.credentials.Credential?) {
        if (credential == null) {
            Toast.makeText(this, "No credential returned", Toast.LENGTH_SHORT).show()
            return
        }

        // We expect a Google ID token credential (GoogleIdTokenCredential)
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCred = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCred.idToken
                if (idToken.isNotEmpty()) {
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "No ID token received", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse GoogleIdTokenCredential", e)
                Toast.makeText(this, "Google token parsing failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "Received unexpected credential type: ${credential::class.java}")
            Toast.makeText(this, "Unexpected credential received", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        isSigningIn = true
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Ensure FirebaseAuth user is reloaded before using it
                    auth.currentUser?.reload()?.addOnSuccessListener {
                        val freshUser = auth.currentUser
                        ensureUserRecordForGoogle(freshUser)
                        updateUI(freshUser)

                    }
                    isSigningIn = false
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Firebase auth failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun ensureUserRecordForGoogle(user: FirebaseUser?) {
        if (user == null) return
        val userRef = database.child("user").child(user.uid)
        userRef.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val model = UserModel(user.email ?: "", "", userName, nameOfRestaurant)
                userRef.setValue(model)
            }
        }
    }
    //check if user is already logged in
    override fun onStart() {
        super.onStart()
        val currUser = auth.currentUser
        if(currUser!= null && !isSigningIn){
            Log.d("AddItemActivity", "Current user UID: ${auth.currentUser?.uid}, Email: ${auth.currentUser?.email}")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}