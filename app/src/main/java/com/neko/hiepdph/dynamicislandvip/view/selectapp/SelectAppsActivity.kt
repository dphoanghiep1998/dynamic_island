package com.neko.hiepdph.dynamicislandvip.view.selectapp

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.data.model.AppModel
import com.neko.hiepdph.dynamicislandvip.databinding.ActivitySelectAppsBinding
import com.neko.hiepdph.dynamicislandvip.viewmodel.AppViewModel
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectAppsActivity : BaseActivity<ActivitySelectAppsBinding>() {
    private var adapter: SelectAppAdapter? = null
    private var appList = mutableListOf<AppModel>()
    private var currentSelectedList = mutableListOf<AppModel>()
    private val viewModel by viewModels<AppViewModel>()


    override fun getViewBinding(): ActivitySelectAppsBinding {
        return ActivitySelectAppsBinding.inflate(layoutInflater)
    }

    override fun initView() {
        lifecycleScope.launch(Dispatchers.Default) {
            appList = fetchInstalledAppList().toMutableList()
            delay(1000)
            launch(Dispatchers.Main) {
                initRecyclerView()
            }
        }
        observeApp()
    }

    override fun initButton() {
        binding.btnBack.clickWithDebounce {
            finish()
        }

        binding.btnApply.clickWithDebounce {

        }
    }

    private fun observeApp() {
        viewModel.getListAppModel().observe(this) {
            currentSelectedList = it.map { AppModel(it.id, it.pkg, it.className) }.toMutableList()
            adapter?.setData(appList, currentSelectedList)

        }

    }

    private fun initRecyclerView() {
        adapter = SelectAppAdapter(onClickAll = {
            currentSelectedList.clear()
            currentSelectedList.addAll(it)
        }, onClickItem = { listSelectedItem ->
            currentSelectedList.clear()
            currentSelectedList.addAll(listSelectedItem)
        })
        adapter?.setData(appList, currentSelectedList)
        binding.rcvApps.adapter = adapter
    }

    private fun fetchInstalledAppList(): List<AppModel> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)

        val appDataList: java.util.ArrayList<AppModel> = arrayListOf()
        resolveInfoList.forEach { resolveInfo ->
            with(resolveInfo) {
                if (activityInfo.packageName != packageName) {
                    val mainActivityName =
                        activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1)
                    val appData = AppModel(
                        id = System.currentTimeMillis(),
                        className = loadLabel(packageManager) as String,
                        pkg = "${activityInfo.packageName}/$mainActivityName",
                    )
                    appData.icon = loadIcon(packageManager)
                    appDataList.add(appData)
                }
            }
        }


        return appDataList
    }

}