package com.myquranapp.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.myquranapp.R
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.search.databinding.ActivitySearchBinding
import com.myquranapp.ui.detail.DetailActivity
import com.myquranapp.ui.home.SurahAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var adapter: SurahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSearchView()
        setupSearchMode()
        setupRecyclerView()
        observeLoadingState()
        observeSearchResults()
        observeSearchHistory()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.search_surah)
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            setIconifiedByDefault(false)
            requestFocus()
            
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { 
                        if (it.isNotEmpty()) {
                            viewModel.searchSurahs(it)
                            viewModel.addToHistory(it)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        viewModel.clearSearch()
                    } else {
                        viewModel.searchSurahs(newText)
                    }
                    return true
                }
            })
        }
    }

    private fun setupSearchMode() {
        binding.chipGroupSearchMode.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChip = findViewById<Chip>(checkedIds.firstOrNull() ?: com.myquranapp.search.R.id.chip_fuzzy)
            val isFuzzy = selectedChip.id == com.myquranapp.search.R.id.chip_fuzzy
            viewModel.setSearchMode(isFuzzy)
        }
    }

    private fun setupRecyclerView() {
        adapter = SurahAdapter(
            onItemClick = { surah -> onSurahClick(surah) },
            onFavoriteClick = { surah, isFavorite -> 
                viewModel.toggleFavorite(surah, isFavorite)
            }
        )

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    // Disable search while loading initial data
                    binding.searchView.isEnabled = !isLoading
                }
            }
        }
    }

    private fun observeSearchResults() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collect { surahs ->
                    adapter.submitList(surahs)
                    
                    binding.apply {
                        if (surahs.isEmpty() && searchView.query.isNotEmpty()) {
                            tvEmptyState.visibility = View.VISIBLE
                            tvEmptyState.text = getString(R.string.no_results_found)
                            rvSearchResults.visibility = View.GONE
                            layoutSearchHistory.visibility = View.GONE
                        } else if (surahs.isNotEmpty()) {
                            tvEmptyState.visibility = View.GONE
                            rvSearchResults.visibility = View.VISIBLE
                            layoutSearchHistory.visibility = View.GONE
                        } else {
                            rvSearchResults.visibility = View.GONE
                            layoutSearchHistory.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun observeSearchHistory() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchHistory.collect { history ->
                    binding.chipGroupHistory.removeAllViews()
                    
                    history.take(10).forEach { query ->
                        val chip = layoutInflater.inflate(
                            com.myquranapp.search.R.layout.item_chip_history,
                            binding.chipGroupHistory,
                            false
                        ) as Chip
                        
                        chip.text = query
                        chip.setOnClickListener {
                            binding.searchView.setQuery(query, true)
                        }
                        chip.setOnCloseIconClickListener {
                            viewModel.removeFromHistory(query)
                        }
                        
                        binding.chipGroupHistory.addView(chip)
                    }
                    
                    binding.layoutSearchHistory.visibility = 
                        if (history.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun onSurahClick(surah: Surah) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_SURAH_NUMBER, surah.sequence)
            putExtra(DetailActivity.EXTRA_SURAH_NAME, surah.name.latinShort)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}