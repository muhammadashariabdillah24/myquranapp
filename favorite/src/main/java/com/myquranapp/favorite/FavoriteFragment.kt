package com.myquranapp.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.favorite.databinding.FragmentFavoriteBinding
import com.myquranapp.ui.detail.DetailActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding

    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavoriteSurahs()
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(
            onItemClick = { surah -> onSurahClick(surah) }
        )

        binding?.rvFavorite?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeFavoriteSurahs() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteSurahs.collect { surahs ->
                    adapter.submitList(surahs)
                    
                    binding?.apply {
                        if (surahs.isEmpty()) {
                            tvEmptyState.visibility = View.VISIBLE
                            rvFavorite.visibility = View.GONE
                        } else {
                            tvEmptyState.visibility = View.GONE
                            rvFavorite.visibility = View.VISIBLE
                        }
                    }
                }
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
