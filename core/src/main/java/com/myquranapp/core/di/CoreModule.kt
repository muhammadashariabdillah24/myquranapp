package com.myquranapp.core.di

import com.myquranapp.core.data.QuranRepository
import com.myquranapp.core.data.SettingsRepository
import com.myquranapp.core.data.source.local.LocalDataSource
import com.myquranapp.core.data.source.local.EncryptedSettingsPreferences
import com.myquranapp.core.data.source.local.room.QuranDatabase
import com.myquranapp.core.data.source.remote.RemoteDataSource
import com.myquranapp.core.data.source.remote.network.ApiService
import com.myquranapp.core.domain.repository.IQuranRepository
import com.myquranapp.core.domain.repository.ISettingsRepository
import com.myquranapp.core.domain.usecase.QuranInteractor
import com.myquranapp.core.domain.usecase.QuranUseCase
import com.myquranapp.core.domain.usecase.SettingsUseCase
import com.myquranapp.core.utils.CertificatePinner
import com.myquranapp.core.utils.DatabaseEncryption
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    single {
        // Initialize SQLCipher
        DatabaseEncryption.initSQLCipher(androidContext())
        // Build encrypted database
        DatabaseEncryption.buildEncryptedDatabase(androidContext())
    }
    single { get<QuranDatabase>().surahDao() }
}

val networkModule = module {
    single {
        val loggingInterceptor = if (com.myquranapp.core.BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .certificatePinner(CertificatePinner.getCertificatePinner())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://staticquran.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    single { EncryptedSettingsPreferences(androidContext()) }
    single<IQuranRepository> {
        QuranRepository(
            get(),
            get()
        )
    }
    single<ISettingsRepository> {
        SettingsRepository(get())
    }
}

val useCaseModule = module {
    factory<QuranUseCase> { QuranInteractor(get()) }
    factory { SettingsUseCase(get()) }
}
