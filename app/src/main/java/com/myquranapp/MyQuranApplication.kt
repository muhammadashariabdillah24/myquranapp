package com.myquranapp

import android.app.Application
import com.myquranapp.core.di.databaseModule
import com.myquranapp.core.di.networkModule
import com.myquranapp.core.di.repositoryModule
import com.myquranapp.core.di.useCaseModule
import com.myquranapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyQuranApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyQuranApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
            
            // Load FavoriteModule from dynamic feature if available
            loadFavoriteModule()
            
            // Load SearchModule from dynamic feature if available
            loadSearchModule()
        }
    }
    
    private fun org.koin.core.KoinApplication.loadFavoriteModule() {
        try {
            val favoriteModuleClass = Class.forName("com.myquranapp.favorite.di.FavoriteModuleKt")
            val getFavoriteModule = favoriteModuleClass.getDeclaredMethod("getFavoriteModule")
            val favoriteModule = getFavoriteModule.invoke(null) as org.koin.core.module.Module
            modules(listOf(favoriteModule))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun org.koin.core.KoinApplication.loadSearchModule() {
        try {
            val searchModuleClass = Class.forName("com.myquranapp.search.di.SearchModuleKt")
            val getSearchModule = searchModuleClass.getDeclaredMethod("getSearchModule")
            val searchModule = getSearchModule.invoke(null) as org.koin.core.module.Module
            modules(listOf(searchModule))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
