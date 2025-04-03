package com.neko.hiepdph.dynamicislandvip.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.neko.hiepdph.dynamicislandvip.data.db.dao.AppModelDao
import com.neko.hiepdph.dynamicislandvip.data.db.entity.AppEntity


@Database(
    entities = [AppEntity::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val appModelDao: AppModelDao
}
