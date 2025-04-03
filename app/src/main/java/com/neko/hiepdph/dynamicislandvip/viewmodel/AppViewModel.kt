package com.neko.hiepdph.dynamicislandvip.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.dynamicislandvip.data.db.entity.AppEntity
import com.neko.hiepdph.dynamicislandvip.data.model.AppModel
import com.neko.hiepdph.dynamicislandvip.data.repo.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val appRepo: AppRepo) : ViewModel() {
    fun getListAppModel(): LiveData<List<AppEntity>> {
        return appRepo.getListAppModel()
    }

    fun deleteAppModel(id: Int) {
        viewModelScope.launch {
            appRepo.deleteAppModel(id)
        }
    }

    fun insertAppModel(appModel: AppModel) {
        viewModelScope.launch {
            appRepo.insertAppModel(appModel)
        }
    }

}