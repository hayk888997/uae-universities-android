package com.d3vly.core.data.di

import android.content.Context
import androidx.room.Room
import com.d3vly.core.data.BuildConfig
import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.local.UniversityDatabase
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.data.repository.UniversityRepositoryImpl
import com.d3vly.core.domain.repository.UniversityRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUniversityRepository(
        implementation: UniversityRepositoryImpl,
    ): UniversityRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideUniversityApi(): UniversityApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.UNIVERSITIES_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UniversityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUniversityDatabase(
        @ApplicationContext context: Context,
    ): UniversityDatabase {
        return Room.databaseBuilder(
            context,
            UniversityDatabase::class.java,
            "universities.db",
        ).build()
    }

    @Provides
    fun provideUniversityDao(database: UniversityDatabase): UniversityDao {
        return database.universityDao()
    }

    private const val NETWORK_TIMEOUT_SECONDS = 15L
}
