package com.myquranapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myquranapp.R
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.utils.NetworkUtils
import com.myquranapp.core.utils.Resource
import com.myquranapp.databinding.FragmentHomeBinding
import com.myquranapp.ui.detail.DetailActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: SurahAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeSurahsState()
        
        // Check internet connection on first load
        checkInternetAndLoadData()
    }
    
    private fun checkInternetAndLoadData() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showNoInternetDialog()
        }
    }
    
    private fun showNoInternetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("Try Again") { dialog, _ ->
                dialog.dismiss()
                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    viewModel.loadSurahs()
                } else {
                    showNoInternetDialog() // Show again if still no internet
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun setupRecyclerView() {
        adapter = SurahAdapter(
            onItemClick = { surah -> onSurahClick(surah) },
            onFavoriteClick = { surah, isFavorite -> 
                viewModel.toggleFavorite(surah, isFavorite)
            }
        )

        binding.rvSurah.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadSurahs()
        }
        
        binding.swipeRefresh.setColorSchemeResources(
            R.color.primary,
            R.color.primary_dark,
            R.color.accent
        )
    }

    private fun observeSurahsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.surahsState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                            showError(false)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showError(false)
                            adapter.submitList(resource.data)
                            
                            if (resource.data.isEmpty()) {
                                showEmptyState(true)
                            } else {
                                showEmptyState(false)
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showError(true, resource.message)
                            Toast.makeText(
                                requireContext(),
                                resource.message ?: getString(R.string.error_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun showError(isError: Boolean, message: String? = null) {
        binding.apply {
            if (isError) {
                tvError.visibility = View.VISIBLE
                tvError.text = message ?: getString(R.string.error_message)
                rvSurah.visibility = View.GONE
            } else {
                tvError.visibility = View.GONE
                rvSurah.visibility = View.VISIBLE
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                tvEmptyState.visibility = View.VISIBLE
                rvSurah.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvSurah.visibility = View.VISIBLE
            }
        }
    }

    private fun onSurahClick(surah: Surah) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_SURAH_NUMBER, surah.sequence)
            putExtra(DetailActivity.EXTRA_SURAH_NAME, surah.name.latinShort)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
