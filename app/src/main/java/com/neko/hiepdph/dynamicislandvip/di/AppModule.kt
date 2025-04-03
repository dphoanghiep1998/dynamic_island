package com.neko.hiepdph.dynamicislandvip.di

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.common.AppSharePreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSharePreference(@ApplicationContext context: Context): AppSharePreference {
       return AppSharePreference.getInstance(context)
    }
}
