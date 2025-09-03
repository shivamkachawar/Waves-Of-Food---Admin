package com.example.adminwavesoffood

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminwavesoffood.databinding.ActivityCreateUserBinding
import com.example.adminwavesoffood.databinding.ActivitySignupBinding

class CreateUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backNavigation.setOnClickListener{
            finish()
        }

    }
}