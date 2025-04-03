package com.neko.hiepdph.dynamicislandvip.data.db.entity

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app")
data class AppEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val pkg: String,
    val className: String) {}