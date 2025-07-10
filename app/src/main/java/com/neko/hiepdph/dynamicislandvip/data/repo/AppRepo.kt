package com.neko.hiepdph.dynamicislandvip.data.repo

import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(
  @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
//    fun getListAppModel(): Flow<List<AppDetail>> {
//        return appDao.getListAppModel()
//    }
//
//    suspend fun insertAppModel(appModel: List<AppDetail>) = withContext(dispatcher) {
//        appDao.insertAppModels(appModel)
//    }
//
//    suspend fun deleteAppModel(id: Int) = withContext(dispatcher) {
//        appDao.deleteAppModel(id)
//    }
}