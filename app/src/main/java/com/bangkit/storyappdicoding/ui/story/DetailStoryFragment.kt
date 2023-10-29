package com.bangkit.storyappdicoding.ui.story

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.databinding.FragmentDetailStoryBinding
import com.bangkit.storyappdicoding.viewmodels.DetailStoryViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory
import com.bumptech.glide.Glide

class DetailStoryFragment : Fragment() {

    private val viewModel: DetailStoryViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[DetailStoryViewModel::class.java]
    }

    private var _binding: FragmentDetailStoryBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        val story = DetailStoryFragmentArgs.fromBundle(arguments as Bundle).story

        binding.ivDetailPhoto.transitionName = story.photoUrl
        binding.tvDetailName.transitionName = story.name
        binding.tvDetailDescription.transitionName = story.description

        Glide.with(requireContext())
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description

        return binding.root
    }

}