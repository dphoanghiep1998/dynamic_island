package com.neko.hiepdph.dynamicislandvip.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.neko.hiepdph.dynamicislandvip.common.viewmanager.ViewManager

class MyAccessService: AccessibilityService() {
    private var viewManager:ViewManager ?= null
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        viewManager = ViewManager(this)
    }
}