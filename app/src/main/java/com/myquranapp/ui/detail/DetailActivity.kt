package com.myquranapp.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myquranapp.R
import com.myquranapp.core.domain.usecase.SettingsUseCase
import com.myquranapp.core.utils.NetworkUtils
import com.myquranapp.core.utils.Resource
import com.myquranapp.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private val settingsUseCase: SettingsUseCase by inject()
    private lateinit var adapter: AyahAdapter
    private var favoriteMenu: MenuItem? = null
    private var surahNumber: Int = 1

    companion object {
        const val EXTRA_SURAH_NUMBER = "extra_surah_number"
        const val EXTRA_SURAH_NAME = "extra_surah_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, 1)
        val surahName = intent.getStringExtra(EXTRA_SURAH_NAME) ?: ""

        setupToolbar(surahName)
        setupRecyclerView()
        observeSurahDetail()
        observeFavoriteState()
        observeSettings()
        
        // Check internet and load data
        checkInternetAndLoadData()
    }
    
    private fun checkInternetAndLoadData() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoInternetDialog()
        } else {
            viewModel.loadSurahDetail(surahNumber)
        }
    }
    
    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("Try Again") { dialog, _ ->
                dialog.dismiss()
                if (NetworkUtils.isNetworkAvailable(this)) {
                    viewModel.loadSurahDetail(surahNumber)
                } else {
                    showNoInternetDialog() // Show again if still no internet
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish() // Close activity if user cancels
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        favoriteMenu = menu?.findItem(R.id.action_favorite)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                viewModel.toggleFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun observeFavoriteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collect { isFavorite ->
                    updateFavoriteIcon(isFavorite)
                }
            }
        }
    }
    
    private fun observeSettings() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsUseCase.getSettings().collect { settings ->
                    adapter.updateSettings(settings)
                    // Update bismillah visibility based on settings
                    binding.cardBismillah.visibility = if (settings.showBismillah) View.VISIBLE else View.GONE
                }
            }
        }
    }
    
    private fun updateFavoriteIcon(isFavorite: Boolean) {
        favoriteMenu?.apply {
            icon = ContextCompat.getDrawable(
                this@DetailActivity,
                if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
            title = if (isFavorite) "Remove from Favorite" else "Add to Favorite"
        }
    }

    private fun setupToolbar(surahName: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = surahName
        }
    }

    private fun setupRecyclerView() {
        adapter = AyahAdapter(
            onPlayClick = { audioUrl ->
                viewModel.playAudio(audioUrl)
            },
            onStopClick = {
                viewModel.stopAudio()
            }
        )

        binding.rvAyah.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = this@DetailActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeSurahDetail() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.surahDetailState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                            showError(false)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showError(false)
                            resource.data?.let { surahDetail ->
                                binding.tvSurahInfo.text = getString(
                                    R.string.surah_info_format,
                                    surahDetail.name.latinShort,
                                    surahDetail.ayahCount,
                                    surahDetail.type.latin
                                )
                                // Display Bismillah
                                binding.tvBismillahArabic.text = surahDetail.preBismillah.text
                                binding.tvBismillahTranslation.text = surahDetail.preBismillah.translation
                                
                                adapter.submitList(surahDetail.ayahs)
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showError(true, resource.message)
                            Toast.makeText(
                                this@DetailActivity,
                                resource.message ?: getString(R.string.error_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Observe playing audio URL
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPlayingUrl.collect { playingUrl ->
                    adapter.setCurrentPlayingUrl(playingUrl)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvAyah.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean, message: String? = null) {
        binding.apply {
            if (isError) {
                tvError.visibility = View.VISIBLE
                tvError.text = message ?: getString(R.string.error_message)
                rvAyah.visibility = View.GONE
            } else {
                tvError.visibility = View.GONE
                rvAyah.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAudio()
    }
}
