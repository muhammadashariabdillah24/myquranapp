package com.myquranapp.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myquranapp.R
import com.myquranapp.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    private var isUpdatingFromSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeSettings()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.settings_title)
        }
    }
    
    private fun observeSettings() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    // Update UI with settings
                    isUpdatingFromSettings = true
                    binding.tvArabicFontSize.text = "${settings.arabicFontSize}sp"
                    binding.tvTranslationFontSize.text = "${settings.translationFontSize}sp"
                    binding.switchTransliteration.isChecked = settings.showTransliteration
                    binding.switchBismillah.isChecked = settings.showBismillah
                    binding.switchNoInternetModal.isChecked = settings.showNoInternetModal
                    isUpdatingFromSettings = false
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        // Arabic Font Size
        binding.layoutArabicFont.setOnClickListener {
            showFontSizeDialog(
                title = getString(R.string.settings_arabic_font_size),
                currentSize = viewModel.settings.value.arabicFontSize,
                onSizeSelected = { size -> viewModel.updateArabicFontSize(size) }
            )
        }
        
        // Translation Font Size
        binding.layoutTranslationFont.setOnClickListener {
            showFontSizeDialog(
                title = getString(R.string.settings_translation_font_size),
                currentSize = viewModel.settings.value.translationFontSize,
                onSizeSelected = { size -> viewModel.updateTranslationFontSize(size) }
            )
        }
        
        // Transliteration Switch
        binding.switchTransliteration.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromSettings) {
                viewModel.updateShowTransliteration(isChecked)
            }
        }
        
        // Bismillah Switch
        binding.switchBismillah.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromSettings) {
                viewModel.updateShowBismillah(isChecked)
            }
        }
        
        // No Internet Modal Switch
        binding.switchNoInternetModal.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromSettings) {
                viewModel.updateShowNoInternetModal(isChecked)
            }
        }
        
        // Clear Cache
        binding.layoutClearCache.setOnClickListener {
            showClearCacheDialog()
        }
    }
    
    private fun showFontSizeDialog(title: String, currentSize: Int, onSizeSelected: (Int) -> Unit) {
        val sizes = arrayOf("12sp", "14sp", "16sp", "18sp", "20sp", "22sp", "24sp", "26sp", "28sp", "30sp")
        val sizeValues = arrayOf(12, 14, 16, 18, 20, 22, 24, 26, 28, 30)
        val currentIndex = sizeValues.indexOf(currentSize)
        
        AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(sizes, currentIndex) { dialog, which ->
                onSizeSelected(sizeValues[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_clear_cache))
            .setMessage(getString(R.string.settings_clear_cache_desc))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.clearAllSettings()
                Toast.makeText(this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
