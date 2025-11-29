package com.myquranapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.myquranapp.databinding.ActivityMainBinding
import com.myquranapp.ui.about.AboutActivity
import com.myquranapp.ui.home.HomeFragment
import com.myquranapp.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupBottomNavigation()
        
        // Load HomeFragment on startup
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_surah -> {
                    loadFragment(HomeFragment())
                    supportActionBar?.title = getString(R.string.app_name)
                    true
                }
                R.id.navigation_favorite -> {
                    loadFavoriteFragment()
                    supportActionBar?.title = getString(R.string.favorite_title)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    private fun loadFavoriteFragment() {
        try {
            // Load FavoriteFragment from dynamic feature module using reflection
            val favoriteFragmentClass = Class.forName("com.myquranapp.favorite.FavoriteFragment")
            val fragment = favoriteFragmentClass.getDeclaredConstructor().newInstance() as Fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                openSearch()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun openSearch() {
        try {
            // Load SearchActivity from dynamic feature module using reflection
            val searchClass = Class.forName("com.myquranapp.search.SearchActivity")
            val intent = Intent(this, searchClass)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            Toast.makeText(this, "Search module not found", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to open Search", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
