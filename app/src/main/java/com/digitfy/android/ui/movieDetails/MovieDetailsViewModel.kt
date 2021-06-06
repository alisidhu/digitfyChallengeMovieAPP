package com.digitfy.android.ui.movieDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.digitfy.android.data.Repository
import com.digitfy.android.model.movie.*
import com.digitfy.android.utils.DEFAULT_LANGUAGE
import com.digitfy.android.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject
constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val movieId = savedStateHandle.get<Movie>("Movie")?.id!!

    // In-Memory Caching
    private var currentMovieCastResult: MutableLiveData<List<MovieCast>>? = null
    private var currentMovieTrailersResult: MutableLiveData<List<MovieTrailer>>? = null
    private var currentMovieReviewResult: MutableLiveData<List<MovieReview>>? = null
    private var currentMovieGenreResult: MutableLiveData<List<MovieGenres>>? = null
    private var currentMovieId: Int? = null

    fun getMovieGenre(): LiveData<List<MovieGenres>> {
        val genreList = MutableLiveData<List<MovieGenres>>()

        val lastResult = currentMovieGenreResult
        if (lastResult != null) {
            return lastResult
        }

        uiScope.launch {
            genreList.value = when (val movieGenres = repository.getMovieGenres(DEFAULT_LANGUAGE)) {
                is Result.Success -> movieGenres.data
                is Result.Error -> null
            }
            currentMovieGenreResult = genreList
        }
        return genreList
    }

    fun getMovieReview(): LiveData<List<MovieReview>> {
        val reviewList = MutableLiveData<List<MovieReview>>()

        val lastResult = currentMovieReviewResult
        if (movieId == currentMovieId && lastResult != null) {
            return lastResult
        }

        currentMovieId = movieId
        uiScope.launch {
            reviewList.value = when (val movieReviews = repository.getMovieReviews(movieId)) {
                is Result.Success -> {
                    if (movieReviews.data.size > 3) {
                        movieReviews.data.subList(0, 3)
                    } else {
                        movieReviews.data
                    }
                }
                is Result.Error -> null
            }
            currentMovieReviewResult = reviewList
        }
        return reviewList
    }

    fun getMovieTrailers(): LiveData<List<MovieTrailer>> {
        val trailers = MutableLiveData<List<MovieTrailer>>()

        val lastResult = currentMovieTrailersResult
        if (movieId == currentMovieId && lastResult != null) {
            return lastResult
        }

        currentMovieId = movieId
        uiScope.launch {
            trailers.value = when (val result = repository.getMovieTrailer(movieId, DEFAULT_LANGUAGE)) {
                is Result.Success -> result.data
                is Result.Error -> null
            }
            currentMovieTrailersResult = trailers
        }
        return trailers
    }

    fun getMovieCast(): LiveData<List<MovieCast>> {
        val castMembers = MutableLiveData<List<MovieCast>>()

        val lastResult = currentMovieCastResult
        if (movieId == currentMovieId && lastResult != null) {
            return lastResult
        }

        currentMovieId = movieId
        uiScope.launch {
            castMembers.value = when (val result = repository.getMovieCast(movieId)) {
                is Result.Success -> result.data.filter { it.profile_path != null }
                is Result.Error -> null
            }
            currentMovieCastResult = castMembers
        }
        return castMembers
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}