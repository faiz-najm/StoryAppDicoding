package com.bangkit.storyappdicoding.ui.setting

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.databinding.FragmentSettingBinding
import com.bangkit.storyappdicoding.databinding.ThemeDialogBinding
import com.bangkit.storyappdicoding.viewmodels.SettingViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory

class SettingFragment : Fragment() {

    private var themeStatus: Int = 0

    private lateinit var viewModel: SettingViewModel

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                title = "Settings"
                setDisplayHomeAsUpEnabled(true)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            when (isDarkModeActive) {
                0 -> {
                    themeStatus = 0
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    binding.themeInp.setText("System Default")
                }

                1 -> {
                    themeStatus = 1
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.themeInp.setText("Light")
                }

                2 -> {
                    themeStatus = 2
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.themeInp.setText("Dark")
                }
            }
        }

        binding.themeInp.setOnClickListener {
            // Membuat dialog untuk memilih gambar dari camera atau gallery
            val pictureDialogBinding: ThemeDialogBinding =
                ThemeDialogBinding.inflate(LayoutInflater.from(context))

            val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                .setView(pictureDialogBinding.root)
                .create()

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            when (themeStatus) {
                0 -> pictureDialogBinding.rbSystemDefault.isChecked = true
                1 -> pictureDialogBinding.rbLight.isChecked = true
                2 -> pictureDialogBinding.rbDark.isChecked = true
            }

            pictureDialogBinding.rbSystemDefault.setOnClickListener {
                // Set theme to system default
                viewModel.saveThemeSetting(0)
                dialog.dismiss()
            }

            pictureDialogBinding.rbLight.setOnClickListener {
                // Set theme to light
                viewModel.saveThemeSetting(1)
                dialog.dismiss()
            }

            pictureDialogBinding.rbDark.setOnClickListener {
                // Set theme to dark
                viewModel.saveThemeSetting(2)
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}