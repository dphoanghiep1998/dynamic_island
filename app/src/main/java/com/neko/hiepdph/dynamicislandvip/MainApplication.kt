package com.neko.hiepdph.dynamicislandvip

import android.app.Activity
import android.app.Application
import com.neko.hiepdph.dynamicislandvip.common.AppSharePreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        lateinit var app: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(this)
    }
}