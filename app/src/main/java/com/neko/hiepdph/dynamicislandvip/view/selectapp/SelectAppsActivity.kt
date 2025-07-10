package com.neko.hiepdph.dynamicislandvip.view.selectapp

import android.app.Dialog
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.databinding.ActivitySelectAppsBinding
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogExit
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectAppsActivity : BaseActivity<ActivitySelectAppsBinding>() {
    private var adapter: SelectAppAdapter? = null
    private var appList = mutableListOf<AppDetail>()
    private var currentSelectedList = mutableListOf<AppDetail>()


    override fun getViewBinding(): ActivitySelectAppsBinding {
        return ActivitySelectAppsBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.loadingView.show()
        initRecyclerView()
        currentSelectedList = config.listPackageFilter.toMutableList()
        lifecycleScope.launch(Dispatchers.Default) {
            appList = fetchInstalledAppList().toMutableList()
            delay(1000)
            launch(Dispatchers.Main) {
                binding.loadingView.hide()
                adapter?.setData(appList, currentSelectedList)
            }
        }
        val dialogShowExitConfirm = DialogExit(this, onAccept = {
            finish()
        }, onCancel = {

        })
        changeBackPressCallBack {
            if (dialogShowExitConfirm?.isShowing == false) {
                dialogShowExitConfirm?.show()
            } else {
                dialogShowExitConfirm?.dismiss()
            }

        }
    }


    override fun initButton() {
        val dialogShowExitConfirm = DialogExit(this, onAccept = {
            finish()
        }, onCancel = {

        })
        binding.btnBack.clickWithDebounce {
            if (dialogShowExitConfirm?.isShowing == false) {
                dialogShowExitConfirm?.show()
            } else {
                dialogShowExitConfirm?.dismiss()
            }
        }

        binding.btnApply.clickWithDebounce {
            config.listPackageFilter = currentSelectedList
            finish()
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
        binding.rcvApps.adapter = adapter
    }

    private fun fetchInstalledAppList(): List<AppDetail> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)

        val appDataList: java.util.ArrayList<AppDetail> = arrayListOf()
        resolveInfoList.forEach { resolveInfo ->
            with(resolveInfo) {
                if (activityInfo.packageName != packageName) {
                    val appDetail = AppDetail()
                    appDetail.label = resolveInfo.loadLabel(packageManager).toString()
                    appDetail.pkg = resolveInfo.activityInfo.packageName
                    appDetail.activityInfoName = resolveInfo.activityInfo.name
                    appDetail.isSorted = false
                    appDataList.add(appDetail)
                }
            }
        }


        return appDataList
    }

}