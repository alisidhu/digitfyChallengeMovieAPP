package com.digitfy.android.adapter.loadstate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.digitfy.android.databinding.LoadStateFooterViewItemBinding


class LoadStateViewHolder(
    private val binding: LoadStateFooterViewItemBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvLoadStateError.text = loadState.error.localizedMessage
        }
        binding.tvLoadStateProgress.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState !is LoadState.Loading
        binding.tvLoadStateError.isVisible = loadState !is LoadState.Loading
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = LoadStateFooterViewItemBinding.inflate(inflater, parent, false)

            return LoadStateViewHolder(
                binding,
                retry
            )
        }
    }


}
