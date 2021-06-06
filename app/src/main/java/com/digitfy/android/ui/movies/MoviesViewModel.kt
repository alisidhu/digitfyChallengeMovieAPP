package com.digitfy.android.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.digitfy.android.data.Repository
import com.digitfy.android.model.movie.Movie
import com.digitfy.android.utils.DEFAULT_CATEGORY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: Repository
) :
    ViewModel() {

    private var currentQuery: String? = null
    private var currentSearchresult: Flow<PagingData<Movie>>? = null
    private var currentQueryResult: Flow<PagingData<Movie>>? = null

    fun getMoviesList(): Flow<PagingData<Movie>> {
        val lastResult = currentSearchresult
        if (lastResult != null) {
            return lastResult
        }

        val newResult = repository.getMovieResultStream(DEFAULT_CATEGORY, DEFAULT_CATEGORY)
            .cachedIn(viewModelScope)
        currentSearchresult = newResult

        return newResult

    }

    fun queryMovieList(query: String): Flow<PagingData<Movie>> {
        val lastResult = currentQueryResult
        if (query == currentQuery &&
            lastResult != null
        ) {
            return lastResult
        }

        currentQuery = query
        val newResult = repository.getMovieQueryStream(query)
            .cachedIn(viewModelScope)
        currentQueryResult = newResult

        return newResult

    }


}