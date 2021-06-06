package com.digitfy.android.ui.movieDetails

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitfy.android.R
import com.digitfy.android.adapter.cast.CastAdapter
import com.digitfy.android.adapter.cast.CastClickListener
import com.digitfy.android.adapter.review.ReviewAdapter
import com.digitfy.android.adapter.review.ReviewClickListener
import com.digitfy.android.adapter.trailer.TrailerAdapter
import com.digitfy.android.adapter.trailer.TrailerClickListener
import com.digitfy.android.databinding.FragmentMovieDetailsBinding
import com.digitfy.android.model.movie.Movie
import com.digitfy.android.model.movie.MovieCast
import com.digitfy.android.model.movie.MovieReview
import com.digitfy.android.model.movie.MovieTrailer
import com.digitfy.android.utils.Utils
import com.digitfy.android.utils.Utils.openReview
import com.digitfy.android.utils.Utils.openTrailer
import com.digitfy.android.utils.Utils.shareDetails
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailsBinding
    private lateinit var movie: Movie
    private val viewModel: MovieDetailsViewModel by viewModels()
    private lateinit var castAdapter: CastAdapter
    private lateinit var trailerAdapter: TrailerAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        movie = MovieDetailsFragmentArgs.fromBundle(requireArguments()).Movie
        setupActionBar(movie.title)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        initAdapters()
        populateMoviesUI(movie)
    }

    private fun populateMoviesUI(movie: Movie) {

        binding.movie = movie
        binding.executePendingBindings()

        viewModel.getMovieGenre().observe(viewLifecycleOwner, { listOfGenres ->
            listOfGenres?.let {

                for (elem in movie.genre_ids) {
                    val filteredListOfGenres = listOfGenres.filter { it.id == elem }
                    for (item in filteredListOfGenres) {
                        val chip = Chip(requireContext())
                        chip.setChipBackgroundColorResource(android.R.color.transparent)
                        chip.chipStrokeColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.darker_gray
                            )
                        )
                        chip.chipStrokeWidth = Utils.dptoPx(requireContext(), 1)

                        chip.text = item.name
                        binding.chipGroup.addView(chip)
                    }
                }
            }
        })

        viewModel.getMovieCast().observe(viewLifecycleOwner, {
            it?.let { castMembers ->
                if (castMembers.isNotEmpty()) {
                    castAdapter.submitMovieCastList(castMembers)
                } else {
                    binding.tvCastError.visibility = View.VISIBLE
                    binding.rvCast.visibility = View.GONE
                }
            }
        })

        viewModel.getMovieTrailers().observe(viewLifecycleOwner, {
            it?.let { listOfTrailers ->
                if (listOfTrailers.isNotEmpty()) {
                    trailerAdapter.submitMovieTrailers(listOfTrailers)
                } else {
                    binding.tvTrailerError.visibility = View.VISIBLE
                    binding.rvTrailer.visibility = View.GONE
                }
            }
        })

        viewModel.getMovieReview().observe(viewLifecycleOwner, {
            it?.let { reviews ->
                if (reviews.isNotEmpty()) {
                    reviewAdapter.submitMovieReviewList(reviews)
                } else {
                    binding.tvReviewError.visibility = View.VISIBLE
                    binding.rvReview.visibility = View.GONE
                }
            }
        })
    }

    private fun initAdapters() {
        binding.rvCast.hasFixedSize()
        binding.rvTrailer.hasFixedSize()
        binding.rvReview.hasFixedSize()

        binding.rvCast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrailer.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvReview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        castAdapter = CastAdapter(CastClickListener {
            (it as MovieCast).let { movieCast ->
                openCastMember(movieCast.id, movieCast.name)
            }
        })

        trailerAdapter = TrailerAdapter(TrailerClickListener {
            openTrailer((it as MovieTrailer).youtubeLink, requireContext())
        })

        reviewAdapter =
            ReviewAdapter(ReviewClickListener {
                openReview((it as MovieReview).url, requireContext())
            })

        binding.rvCast.adapter = castAdapter
        binding.rvTrailer.adapter = trailerAdapter
        binding.rvReview.adapter = reviewAdapter
    }

    private fun openCastMember(id: Int, name: String) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ic_share -> {
                val message =
                    "*${movie.title}*\n${movie.overview}\n\n${getString(R.string.more_details)}\n${
                        getString(R.string.playstore_link)
                    }"
                shareDetails(message, requireContext())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar(title: String) {
        ((activity as AppCompatActivity).supportActionBar)?.title = title
        setHasOptionsMenu(true)
    }

}