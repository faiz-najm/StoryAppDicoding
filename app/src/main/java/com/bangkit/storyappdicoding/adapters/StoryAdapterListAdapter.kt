package com.bangkit.storyappdicoding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.remote.response.ListStoryItem
import com.bangkit.storyappdicoding.databinding.ItemStoryBinding
import com.bumptech.glide.Glide


class StoryAdapterListAdapter :
    ListAdapter<ListStoryItem, StoryAdapterListAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(
            items: ListStoryItem,
            ivItemPhoto: ImageView,
            tvItemName: TextView,
            tvItemDesc: TextView
        )
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickCallback = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemStoryBinding,
        itemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = getItem(position)
                    itemClickListener?.onItemClick(
                        user,
                        binding.ivItemPhoto,
                        binding.tvItemName,
                        binding.tvItemDesc
                    )
                }
            }
        }

        fun bind(item: ListStoryItem) = with(binding) {
            ivItemPhoto.transitionName = item.photoUrl
            tvItemName.transitionName = item.name
            tvItemDesc.transitionName = item.description

            Glide.with(itemView.context)
                .load(item.photoUrl)
                .error(R.drawable.ic_place_holder)
                .into(ivItemPhoto)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}