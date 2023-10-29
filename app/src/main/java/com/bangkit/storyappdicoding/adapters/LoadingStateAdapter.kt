package com.bangkit.storyappdicoding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyappdicoding.databinding.ItemStoryLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryLoadingBinding.inflate(inflater, parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(
        holder: LoadingStateViewHolder, loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(
        private val binding: ItemStoryLoadingBinding, retry: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
        }
    }
}

