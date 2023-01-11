package com.sebix.testinfuraapi.di

import android.content.Context
import com.sebix.testinfuraapi.utils.MnemonicSeedGenerator
import com.sebix.testinfuraapi.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class Modules {
    @Provides
    @Singleton
    fun provideMnemonicSeedGenerator(): MnemonicSeedGenerator {
        return MnemonicSeedGenerator()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(@ApplicationContext context: Context): SharedPreferencesHelper {
        return SharedPreferencesHelper(context = context)
    }
}