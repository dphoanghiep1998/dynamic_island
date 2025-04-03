package com.neko.hiepdph.dynamicislandvip.data.repo

import androidx.lifecycle.LiveData
import com.neko.hiepdph.dynamicislandvip.data.db.dao.AppModelDao
import com.neko.hiepdph.dynamicislandvip.data.db.entity.AppEntity
import com.neko.hiepdph.dynamicislandvip.data.model.AppModel
import com.neko.hiepdph.dynamicislandvip.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(
        private val appDao:AppModelDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    fun getListAppModel(): LiveData<List<AppEntity>> {
        return appDao.getListAppModel()
    }

    suspend fun insertAppModel(appModel: AppModel) = withContext(dispatcher) {
        appDao.insertAppModel(
            AppEntity(
            id = appModel.id, appModel.pkg, appModel.className

        )
        )
    }

    suspend fun deleteAppModel(id: Int) = withContext(dispatcher) {
        appDao.deleteAppModel(id)
    }
}