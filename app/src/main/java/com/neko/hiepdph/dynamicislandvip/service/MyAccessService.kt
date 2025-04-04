package com.neko.hiepdph.dynamicislandvip.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.neko.hiepdph.dynamicislandvip.common.viewmanager.ViewManager
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail

class MyAccessService : AccessibilityService() {
    private var viewManager: ViewManager? = null
    private var callPKG = arrayListOf<AppDetail>()
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        viewManager = ViewManager(this)
    }

    fun closeSmallIslandNotification() {
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
}