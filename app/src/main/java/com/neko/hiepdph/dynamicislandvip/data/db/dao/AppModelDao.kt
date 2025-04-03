package com.neko.hiepdph.dynamicislandvip.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neko.hiepdph.dynamicislandvip.data.db.entity.AppEntity
import com.neko.hiepdph.dynamicislandvip.data.model.AppModel
import com.neko.hiepdph.dynamicislandvip.viewmodel.AppViewModel

@Dao
interface AppModelDao {
    @Query("select * from app order by id DESC")
    fun getListAppModel():LiveData<List<AppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppModel(entity:AppEntity)

    @Query("delete from app where id = :id ")
    fun deleteAppModel(id: Int)
}