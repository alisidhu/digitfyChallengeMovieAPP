package com.digitfy.android.adapter.movie

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitfy.android.databinding.MovieItemBinding
import com.digitfy.android.model.movie.Movie
import com.digitfy.android.utils.Utils
import kotlinx.android.synthetic.main.fragment_movies.view.*

class MovieViewHolder(private val binding: MovieItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Movie?, clickListener: MovieClickListener) {
        binding.movie = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
        try {
            if (item != null && item.release_date.isNotEmpty()) {
                if(Utils.isCurrentYear(item.release_date)) {
                    binding.tvReleaseDate.setTextColor(Color.parseColor("#FFE30000"))
                    binding.tvReleaseDate.setTypeface(
                        binding.tvReleaseDate.typeface,
                        Typeface.BOLD
                    )
                }
                else {
                    binding.tvReleaseDate.setTextColor(Color.parseColor("#FF000000"))
                    binding.tvReleaseDate.setTypeface(
                        binding.tvReleaseDate.typeface,
                        Typeface.NORMAL)
                    binding.tvMovieTitle.textSize = 17f
                }
            }
        } catch (e: Exception) {
        }
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = MovieItemBinding.inflate(inflater, parent, false)
            return MovieViewHolder(
                binding
            )
        }
    }

}