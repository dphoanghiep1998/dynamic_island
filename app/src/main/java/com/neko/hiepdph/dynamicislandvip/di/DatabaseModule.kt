package com.neko.hiepdph.dynamicislandvip.di


import android.content.Context
import androidx.room.Room
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.data.db.AppDatabase
import com.neko.hiepdph.dynamicislandvip.data.db.dao.AppModelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, Constant.APP_DB)
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAppModelDao(appDatabase: AppDatabase): AppModelDao {
        return appDatabase.appModelDao
    }



}