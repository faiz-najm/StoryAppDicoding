package com.bangkit.storyappdicoding.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bangkit.storyappdicoding.databinding.FragmentWelcomeBinding
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory
import com.bangkit.storyappdicoding.viewmodels.WelcomeViewModel

class WelcomeFragment : Fragment() {

    private val welcomeViewModel: WelcomeViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[WelcomeViewModel::class.java]
    }

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        setupAction()
        playAnimation()

        welcomeViewModel.sessionLiveData.observe(viewLifecycleOwner) { session ->
            if (session.isLogin) {
                // go to story fragment and clear backstack
                findNavController().navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToMainActivity()
                )
                requireActivity().finish()
            }
        }

        return binding.root
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }

        binding.signupButton.setOnClickListener {
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignupFragment())
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        welcomeViewModel.getSession()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}