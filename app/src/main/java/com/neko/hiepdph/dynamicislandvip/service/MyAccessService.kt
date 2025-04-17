package com.neko.hiepdph.dynamicislandvip.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.viewmanager.ViewManager
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail

class MyAccessService : AccessibilityService() {
    private var viewManager: ViewManager? = null
    private var callPKG = arrayListOf<AppDetail>()
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action){
            Constant.UPDATE_LAYOUT_SIZE -> {
                viewManager?.updateLayout()
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("TAG", "onServiceConnected: ")
        viewManager = ViewManager(this)
    }

    fun closeSmallIslandNotification() {
        viewManager?.showSmallIslandNotification()
    }
    fun closeFullIsLandNotification() {
        viewManager?.closeFullNotificationIsland()
    }
    fun isCallPkgFound(str: String?): Boolean {
        val it: Iterator<AppDetail> = callPKG.iterator()
        while (it.hasNext()) {
            if (it.next().pkg == str) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewManager?.unregisterReceiver()
    }
}