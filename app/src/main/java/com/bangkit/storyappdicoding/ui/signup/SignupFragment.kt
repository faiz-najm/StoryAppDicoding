package com.bangkit.storyappdicoding.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.databinding.FragmentSignupBinding
import com.bangkit.storyappdicoding.viewmodels.SignupViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class SignupFragment : Fragment() {

    private val signupViewModel: SignupViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[SignupViewModel::class.java]
    }

    private var _binding: FragmentSignupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupAction()
        playAnimation()

        with(binding) {
            edRegisterName.addTextChangedListener(CustomTextWatcher(nameEditTextLayout))
            edRegisterEmail.addTextChangedListener(CustomTextWatcher(emailEditTextLayout))
        }

        signupViewModel.signupResultLiveData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ApiStatus.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Akun berhasil dibuat. Silahkan login.",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(
                            SignupFragmentDirections.actionSignupFragmentToLoginFragment()
                        )
                    }

                    is ApiStatus.Error -> {
                        binding.progressBar.visibility = View.GONE
                        if (result.message == "Email is already taken") {
                            binding.emailEditTextLayout.error = "Email sudah digunakan"
                        } else {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Gagal")
                                .setMessage("Terjadi kesalahan. Silakan coba lagi.")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }

                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            signupViewModel.signup(name, email, password)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class CustomTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.emailEditTextLayout -> {
                    val emailEditTextLayout = view as TextInputLayout
                    if (s.isEmpty()) {
                        emailEditTextLayout.error = "Email tidak boleh kosong"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        emailEditTextLayout.error = "Email tidak valid"
                    } else {
                        emailEditTextLayout.error = null
                    }
                }

                R.id.nameEditTextLayout -> {
                    val nameEditTextLayout = view as TextInputLayout
                    if (s.isEmpty()) {
                        nameEditTextLayout.error = "Nama tidak boleh kosong"
                    } else {
                        nameEditTextLayout.error = null
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}