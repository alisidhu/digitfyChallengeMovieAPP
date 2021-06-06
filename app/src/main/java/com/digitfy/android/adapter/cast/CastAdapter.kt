package com.digitfy.android.adapter.cast

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digitfy.android.model.movie.MovieCast
import com.digitfy.android.utils.MOVIE_VIEW_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

class CastAdapter(private val clickListener: CastClickListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(CastDiffUtillCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MOVIE_VIEW_TYPE -> MovieCastViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieCastViewHolder -> {
                val movieCast = getItem(position) as DataItem.MovieCastItem
                holder.bind(movieCast.movieCast, clickListener)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.MovieCastItem -> MOVIE_VIEW_TYPE
        }
    }

    fun submitMovieCastList(list: List<MovieCast>) {
        adapterScope.launch {

            val castMembers = list.map { DataItem.MovieCastItem(it) }

            withContext(Dispatchers.Main) {
                submitList(castMembers)
            }
        }
    }

}

class CastClickListener(val clickListener: (cast: Any) -> Unit) {
    fun onClick(cast: Any) {
        when (cast) {
            is MovieCast -> clickListener(cast)
        }
    }
}

class CastDiffUtillCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    data class MovieCastItem(val movieCast: MovieCast) : DataItem() {
        override val id: Int
            get() = movieCast.id
    }
    abstract val id: Int
}