package com.digitfy.android.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.digitfy.android.data.source.MovieQueryPagingSource
import com.digitfy.android.data.source.MoviesPagingSource
import com.digitfy.android.model.movie.*
import com.digitfy.android.network.ApiService
import com.digitfy.android.utils.Result
import com.digitfy.android.utils.SECRET_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiService
) {

    fun getMovieResultStream(category: String?, language: String?):
            Flow<PagingData<Movie>> {
        return Pager(config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
            pagingSourceFactory = {
                MoviesPagingSource(
                    service,
                    category,
                    language
                )
            }).flow
    }

    fun getMovieQueryStream(query: String):
            Flow<PagingData<Movie>> {
        return Pager(config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
            pagingSourceFactory = {
                MovieQueryPagingSource(
                    query,
                    service
                )
            }).flow
    }

    suspend fun getMovieReviews(movieId: Int): Result<List<MovieReview>> {
        return withContext(Dispatchers.IO) {
            try {
                val movieReviews = service.getMovieReview(movieId, SECRET_KEY).results
                Result.Success(movieReviews)

            } catch (exception: IOException) {
                Result.Error(exception)
            } catch (exception: HttpException) {
                Result.Error(exception)
            }
        }
    }

    suspend fun getMovieTrailer(movieId: Int, language: String): Result<List<MovieTrailer>> {
        return withContext(Dispatchers.IO) {
            try {
                val movieTrailers = service.getMovieTrailers(movieId, SECRET_KEY, language).results
                Result.Success(movieTrailers)

            } catch (exception: IOException) {
                Result.Error(exception)
            } catch (exception: HttpException) {
                Result.Error(exception)
            }
        }
    }

    suspend fun getMovieCast(movieId: Int): Result<List<MovieCast>> {
        return withContext(Dispatchers.IO) {
            try {
                val castMembers = service.getMovieCredits(movieId, SECRET_KEY).cast
                Result.Success(castMembers)

            } catch (exception: IOException) {
                Result.Error(exception)
            } catch (exception: HttpException) {
                Result.Error(exception)
            }
        }
    }

    suspend fun getMovieGenres(language: String): Result<List<MovieGenres>> {
        return withContext(Dispatchers.IO) {
            try {
                val movieGenres = service.getMovieGenres(SECRET_KEY, language).genres
                Result.Success(movieGenres)

            } catch (exception: IOException) {
                Result.Error(exception)
            } catch (exception: HttpException) {
                Result.Error(exception)
            }
        }
    }
}