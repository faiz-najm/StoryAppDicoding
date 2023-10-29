package com.bangkit.storyappdicoding.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.data.local.model.UserModel
import com.bangkit.storyappdicoding.databinding.FragmentLoginBinding
import com.bangkit.storyappdicoding.viewmodels.LoginViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        setupAction()
        playAnimation()

        with(binding) {
            edLoginEmail.addTextChangedListener(CustomTextWatcher(emailEditTextLayout))
        }

        loginViewModel.loginResultLiveData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ApiStatus.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val email = binding.edLoginEmail.text.toString()


                        loginViewModel.saveSession(
                            UserModel(
                                email,
                                result.data.loginResult.token
                            )
                        )

                        ViewModelFactory.refreshInstance()

                        Toast.makeText(
                            requireContext(),
                            "Login berhasil.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // go to story fragment and clear backstack
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToMainActivity()
                        )
                        requireActivity().finish()

                    }

                    is ApiStatus.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Login gagal.",
                            Toast.LENGTH_SHORT
                        ).show()
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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            loginViewModel.login(email, password)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    override fun onResume() {
        super.onResume()
        loginViewModel.getSession()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Custom TextWatcher
    private class CustomTextWatcher(private val emailEditTextLayout: TextInputLayout) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isEmpty()) {
                emailEditTextLayout.error = "Email tidak boleh kosong"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                emailEditTextLayout.error = "Email tidak valid"
            } else {
                emailEditTextLayout.error = null
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

}