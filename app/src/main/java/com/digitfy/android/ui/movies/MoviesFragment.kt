package com.digitfy.android.ui.movies

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.digitfy.android.R
import com.digitfy.android.adapter.loadstate.LoadStateAdapter
import com.digitfy.android.adapter.movie.MovieAdapter
import com.digitfy.android.adapter.movie.MovieClickListener
import com.digitfy.android.databinding.FragmentMoviesBinding
import com.digitfy.android.utils.LOADSTATE_VIEW_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private var binding: FragmentMoviesBinding? = null
    private val viewModel: MoviesViewModel by viewModels()
    private lateinit var adapter: MovieAdapter
    private var isFirstTime = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if(binding ==  null)
            binding = FragmentMoviesBinding.inflate(inflater, container, false)

        setupActionBar()
        return binding!!.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFirstTime = false
    }

    private fun setupActionBar() {
        ((activity as AppCompatActivity).supportActionBar)?.title = getString(R.string.popular)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()

        fetchMovies()

    }

    private fun fetchMovies() {

        lifecycleScope.launch {
            viewModel.getMoviesList().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initComponents() {
        setHasOptionsMenu(true)

        binding!!.lifecycleOwner = viewLifecycleOwner

        initAdapter()
        binding!!.btnRetry.setOnClickListener { adapter.retry() }
    }

    private fun initAdapter() {
        adapter = MovieAdapter(
            MovieClickListener {

                findNavController().navigate(
                    MoviesFragmentDirections.actionMoviesFragmentToDetailsFragment(
                        it
                    )
                )
            }).apply {
            addLoadStateListener { loadState ->
                // If list has items. Show
                binding!!.rvMoviesList.isVisible = loadState.source.refresh is LoadState.NotLoading
                // If loading or refreshing show spinner
                binding!!.pbLoader.isVisible = loadState.source.refresh is LoadState.Loading
                // If initial load fails show Retry button and text
                binding!!.btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                binding!!.tvMovieError.isVisible = loadState.source.refresh is LoadState.Error
            }
        }

        binding!!.rvMoviesList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        // RecyclerView
        binding!!.rvMoviesList.hasFixedSize()
        val layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count))
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == LOADSTATE_VIEW_TYPE) 1
                else resources.getInteger(R.integer.span_count)
            }

        }
        binding!!.rvMoviesList.layoutManager = layoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        implementSearch(menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun implementSearch(menu: Menu) {
        val manager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.ic_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                //searchView.setQuery("", false)
                searchItem.collapseActionView()
                query?.let {
                    queryMovieList(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                fetchMovies()
                return true
            }
        }

        val actionMenuItem = menu.findItem(R.id.ic_search)
        actionMenuItem.setOnActionExpandListener(expandListener)
    }

    private fun queryMovieList(movieQuery: String) {
        lifecycleScope.launch {
            viewModel.queryMovieList(movieQuery).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            findNavController()
        ) || super.onOptionsItemSelected(item)
    }

}