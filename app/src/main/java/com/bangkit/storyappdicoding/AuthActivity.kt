package com.bangkit.storyappdicoding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.databinding.ActivityAuthBinding
import com.bangkit.storyappdicoding.viewmodels.AuthViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by lazy {
        val factory = ViewModelFactory.getInstance(this)
        ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}