package com.neko.hiepdph.dynamicislandvip

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.neko.hiepdph.dynamicislandvip.common.AppSharePreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    var currentActivity: Activity? = null

    companion object {
        lateinit var app: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(this)
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())

    }
    inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}