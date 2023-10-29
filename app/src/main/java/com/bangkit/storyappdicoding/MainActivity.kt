package com.bangkit.storyappdicoding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.databinding.ActivityMainBinding
import com.bangkit.storyappdicoding.viewmodels.MainViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        val factory = ViewModelFactory.getInstance(this)
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}