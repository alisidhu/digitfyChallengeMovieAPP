package com.digitfy.android.adapter.review

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digitfy.android.model.movie.MovieReview
import com.digitfy.android.utils.MOVIE_VIEW_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewAdapter(private val clickListener: ReviewClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(ReviewDiffUtilItemCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MOVIE_VIEW_TYPE -> MovieReviewViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieReviewViewHolder -> {
                val movieReview = getItem(position) as DataItem.MovieReviewItem
                holder.bind(movieReview.movieReview, clickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.MovieReviewItem -> MOVIE_VIEW_TYPE
        }
    }

    fun submitMovieReviewList(list: List<MovieReview>) {
        adapterScope.launch {

            val listOfReviews = list.map {
                DataItem.MovieReviewItem(it)
            }

            withContext(Dispatchers.Main) {
                submitList(listOfReviews)
            }
        }
    }
}

class ReviewDiffUtilItemCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}

class ReviewClickListener(val clickListener: (review: Any) -> Unit) {
    fun onClick(review: Any) {
        when (review) {
            is MovieReview -> clickListener(review)
        }
    }
}

sealed class DataItem {
    data class MovieReviewItem(val movieReview: MovieReview) : DataItem() {
        override val id: String
            get() = movieReview.id
    }
    abstract val id: String
}