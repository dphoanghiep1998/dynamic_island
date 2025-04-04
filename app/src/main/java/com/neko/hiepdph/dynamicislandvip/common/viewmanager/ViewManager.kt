package com.neko.hiepdph.dynamicislandvip.common.viewmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionP
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionR
import com.neko.hiepdph.dynamicislandvip.common.customview.CustomRecyclerView
import com.neko.hiepdph.dynamicislandvip.common.customview.StatusBarParentView
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService

class ViewManager(
    private val context: MyAccessService
) {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var statusBarParentView: View? = null
    private var statusBarView: StatusBarParentView? = null
    private var rvDynamicIslandSmall: RecyclerView? = null
    private var rvDynamicIslandBig: CustomRecyclerView? = null
    private var isInFullScreen = false
    private var dynamicIslandParentLayout: LinearLayout? = null
    private var dynamicIslandTopLayout: LinearLayout? = null
    private var actionListener: BroadcastReceiver? = null
    private var notificationListener: BroadcastReceiver? = null
    private var listSmallDynamicIsland: ArrayList<Notification> = arrayListOf()
    private var listBigDynamicIsland: ArrayList<Notification> = arrayListOf()


    init {
        addDynamicView()
        initActionListener()
        initNotificationListener()


    }


    private fun addDynamicView() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            format = PixelFormat.TRANSLUCENT
            height = 30 * context.resources.displayMetrics.scaledDensity.toInt()
            width = 140 * context.resources.displayMetrics.scaledDensity.toInt()
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION
            if (buildMinVersionP()) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        statusBarView =
            LayoutInflater.from(context).inflate(R.layout.status_bar, null) as StatusBarParentView

        if (buildMinVersionR()) {
            statusBarView?.setOnApplyWindowInsetsListener { view, insets ->
                val isFullscreen: Boolean = insets.isVisible(WindowInsets.Type.statusBars())
                if (isFullscreen) {
                    isInFullScreen = false
                    showSmallIslandNotification()
                } else {
                    isInFullScreen = true
//                    if (!Constants.getShowInFullScreen(this@MAccessibilityService.mContext)) {
//                        closeSmallIslandNotification()
//                    }
                }
                insets
            }
        } else {

        }

        statusBarParentView = statusBarView!!.findViewById(R.id.statusbar_parent)
        dynamicIslandParentLayout = statusBarParentView!!.findViewById(R.id.island_parent_layout)
        dynamicIslandTopLayout = statusBarParentView!!.findViewById(R.id.island_top_layout)
        rvDynamicIslandSmall = statusBarParentView!!.findViewById(R.id.rv_island_small)
        rvDynamicIslandBig = statusBarParentView!!.findViewById(R.id.rv_island_big)
        statusBarView?.setOnClickListener {
            closeFullNotificationIsland()
        }

        try {
            windowManager?.addView(statusBarView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "LMAOOO loi~ roi", Toast.LENGTH_SHORT).show()
        }


    }

    private fun initActionListener() {
        actionListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("android.intent.action.USER_PRESENT")
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED")
        intentFilter.addAction("android.media.RINGER_MODE_CHANGED")
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED")
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED")
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED")
        context.registerReceiver(actionListener, intentFilter)
    }

    private fun initNotificationListener() {
        notificationListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("from_notification_service" + context.packageName)
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("android.intent.action.USER_PRESENT")
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(notificationListener!!, intentFilter)
    }

    fun closeFullNotificationIsland() {

    }

    private fun showSmallIslandNotification() {
        Handler().postDelayed({

        }, 100)
    }


}