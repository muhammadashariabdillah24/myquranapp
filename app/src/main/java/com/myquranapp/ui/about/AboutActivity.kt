package com.myquranapp.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myquranapp.BuildConfig
import com.myquranapp.R
import com.myquranapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAboutInfo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.about_app)
        }
    }

    private fun setupAboutInfo() {
        binding.apply {
            tvAppName.text = getString(R.string.app_name)
            tvAppVersion.text = getString(R.string.settings_version, BuildConfig.VERSION_NAME)
            tvAppDescription.text = getString(R.string.app_description)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
