package com.neko.hiepdph.dynamicislandvip.service

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.neko.hiepdph.dynamicislandvip.MainApplication
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion26
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion31
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.notification.ActionParsable
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationQueueManager
import com.neko.hiepdph.dynamicislandvip.common.viewmanager.ViewManager
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.view.accesspermisison.AccessibilityPermissionActivity
import java.util.ArrayDeque
import java.util.Calendar

class MyAccessService : AccessibilityService() {
    var isPhoneLocked: Boolean = false
    private var listSmallDynamicIsland: ArrayDeque<Notification> = ArrayDeque()

    private var viewManager: ViewManager? = null
    private var callPKG = arrayListOf<AppDetail>()

    private var actionListener: BroadcastReceiver? = null

    private var notificationListener: BroadcastReceiver? = null

    private var timeoutReceiver = 0L

    private var isPhoneCharging = false


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    companion object {
        var isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            Constant.TOGGLE_CONTROL -> {
                viewManager?.toggleView()
            }
            Constant.UPDATE_LANG -> {
                viewManager?.updateLang()
            }
            Constant.UPDATE_LAYOUT_SIZE -> {
                viewManager?.updateLayout()
            }

            Constant.UPDATE_BUBBLE -> {
                viewManager?.updateLayoutBubble()
            }

            Constant.UPDATE_BACKGROUND_COLOR -> {
                viewManager?.setBackgroundNotificationColor()
            }

            Constant.UPDATE_NOTCH_STYLE -> {
                viewManager?.setBackGroundNotification()
            }

            Constant.UPDATE_ADJUST_VIBRATION -> {
                viewManager?.updateAdjustVibration()
            }

            Constant.UPDATE_ADJUST_VOLUME -> {
                viewManager?.updateAdjustVolume()
            }

            Constant.UPDATE_ANIMATION -> {
                viewManager?.updateAnimation()
            }

            Constant.UPDATE_DISPLAY_MODE -> {
                viewManager?.updateDisplayMode(listSmallDynamicIsland.size)
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }


    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        viewManager = ViewManager(this)
        initNotificationListener()
        initActionListener()
        isRunning = true
//        if (MainActivity.isRunning) {
//            MainApplication.app.currentActivity?.let {
//                if (MainApplication.app.currentActivity is AccessibilityPermissionActivity) {
//                    (MainApplication.app.currentActivity as AccessibilityPermissionActivity).finish()
//                }
//            }
//        }
        if (MainApplication.app.currentActivity is AccessibilityPermissionActivity) {
            (MainApplication.app.currentActivity as AccessibilityPermissionActivity).finish()
        }
        startActivity(Intent(this, AccessibilityPermissionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
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

    fun vibrateShort(context: Context) {
        val vibrator = if (buildMinVersion31()) {
            val vibratorManager =
                context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        if (buildMinVersion26()) {
            val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            val mVibratePattern = longArrayOf(0, 300, 100, 400)
            vibrator.vibrate(mVibratePattern, -1)
        }

    }

    private fun initNotificationListener() {
        notificationListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null && intent.action == "from_notification_service" + context?.packageName) {
                    updateNotificationList(intent)
                    if (config.vibration) {
                        vibrateShort(this@MyAccessService)
                    }
                }
            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("from_notification_service" + packageName)
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("android.intent.action.USER_PRESENT")
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(notificationListener!!, intentFilter)
    }

    private fun initActionListener() {
        actionListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (isInitialStickyBroadcast) {
                    return
                }
                if (!config.controlEnable) {
                    return
                }

                if (intent != null) {
                    when (intent.action) {
                        "android.intent.action.BATTERY_CHANGED" -> {
                            if (config.vibration) {
                                vibrateShort(this@MyAccessService)
                            }
                            if (!context.config.batteryChargeEnable) {
                                return
                            }
                            if (NotificationQueueManager.getBusy()) {
                                return
                            }
                            if (System.currentTimeMillis() - timeoutReceiver < 2000L) {
                                return
                            }

                            val status = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1)
                            if ((status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)) {
                                if (isPhoneCharging) {
                                    return
                                }
                                isPhoneCharging = true
                                NotificationQueueManager.setBusy(true)
                                timeoutReceiver = System.currentTimeMillis()
                                var notification: Notification? = null
                                for ((i, n) in listSmallDynamicIsland.withIndex()) {
                                    if (n.type == Constant.TYPE_CHARGING) {
                                        notification = n
                                        break
                                    }
                                }
                                if (notification == null) {
                                    notification = Notification(Constant.TYPE_CHARGING, 0, 0)
                                }
                                val batteryLevel = Utils.getBatteryLevel(context)
                                notification.template = "battery"
                                notification.text = "$batteryLevel%"
                                notification.color = context.getColor(R.color.green_500)
                                notification.title = if (batteryLevel < 100) {
                                    context.getString(R.string.battery_charging)
                                } else {
                                    context.getString(R.string.battery_full)
                                }
                                listSmallDynamicIsland.push(notification)
                                viewManager?.setNotification(listSmallDynamicIsland)
                                Log.d("TAG", "isPhoneCharging: 123123")

                            } else {
                                isPhoneCharging = false
                            }


                        }

                        "android.media.RINGER_MODE_CHANGED" -> {
                            if (!context.config.ringingStateEnable) {
                                return
                            }
                            if (NotificationQueueManager.getBusy()) {
                                return
                            }
                            if (System.currentTimeMillis() - timeoutReceiver < 2000L) {
                                Log.d("TAG", "timeoutReceiver: ")
                                return
                            }
                            if (config.vibration) {
                                vibrateShort(this@MyAccessService)
                            }
                            NotificationQueueManager.setBusy(true)
                            timeoutReceiver = System.currentTimeMillis()
                            val dndState: Int = Utils.getDndState(context)
                            val iterator = listSmallDynamicIsland.iterator()

                            while (iterator.hasNext()) {
                                val notification = iterator.next()
                                if (notification.type == Constant.TYPE_SILENT) {
                                    iterator.remove()
                                    break
                                }
                            }

                            when (dndState) {
                                0 -> {
                                    val silentNotification = Notification(
                                        Constant.TYPE_SILENT, R.drawable.silent, 0
                                    ).apply {
                                        title = context.getString(R.string.silent_txt)
                                        template = "ringing"
                                        color = context.getColor(R.color.red_500)
                                    }
                                    listSmallDynamicIsland.push(silentNotification)

                                }

                                1 -> {
                                    val vibrateNotification = Notification(
                                        Constant.TYPE_SILENT, R.drawable.vibration_icon, 0
                                    ).apply {
                                        template = "ringing"
                                        title = context.getString(R.string.vibrate)
                                        color = context.getColor(R.color.purple_400)
                                    }
                                    listSmallDynamicIsland.push(vibrateNotification)


                                }

                                2 -> {
                                    val ringNotification = Notification(
                                        Constant.TYPE_SILENT, R.drawable.ic_adjust_volume, 0
                                    ).apply {
                                        template = "ringing"
                                        title = context.getString(R.string.ring)
                                        color = context.getColor(R.color.green_500)
                                    }
                                    listSmallDynamicIsland.push(ringNotification)
                                }

                            }
                            viewManager?.setNotification(listSmallDynamicIsland)
                            viewManager?.updateDisplayMode(listSmallDynamicIsland.size)

                        }


                    }
                    val keyguardManager = context.getSystemService("keyguard") as KeyguardManager

                    if (intent.action != "android.intent.action.USER_PRESENT" && intent.action != "android.intent.action.SCREEN_OFF" && intent.action != "android.intent.action.SCREEN_ON") {
                        return
                    }
                    if (keyguardManager.inKeyguardRestrictedInputMode()) {
                        isPhoneLocked = true
                        if (context.config.notificationHideOnLockScreen) {
                            closeAll()
                            return
                        }
                    } else {
                        isPhoneLocked = false
                        if (viewManager?.isShowingSmall() != true || viewManager?.isShowingFullIsland() == true) {
                            showAgain()
                        }

                    }


                }
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
        intentFilter.addAction("android.bluetooth.device.action.PHONE_STATE")
        ContextCompat.registerReceiver(
            this, actionListener, intentFilter, ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun clearSpecialNotification() {
        val iterator = listSmallDynamicIsland.iterator()
        while (iterator.hasNext()) {
            val n = iterator.next()
            if (n.type == Constant.TYPE_CHARGING) {
                iterator.remove()
            }
            if (n.type == Constant.TYPE_SILENT) {
                iterator.remove()
            }
            if (n.type == Constant.TYPE_AIRBUDS) {
                iterator.remove()
            }
        }
        viewManager?.setNotification(listSmallDynamicIsland)
    }

    fun updateNotificationList(intent: Intent) {
        if (config.controlEnable) {
            var arrayList: ArrayList<ActionParsable>? = null
            val packageName = intent.getStringExtra("package")
            val substName = intent.getCharSequenceExtra("substName")
            val subText = intent.getCharSequenceExtra("subText")
            val titleBig = intent.getCharSequenceExtra("titleBig")
            val summaryText = intent.getCharSequenceExtra("summaryText")
            val info_text = intent.getCharSequenceExtra("info_text")
            val progressMax = intent.getIntExtra("progressMax", 0)
            val progress = intent.getIntExtra("progress", 0)
            val progressIndeterminate = intent.getBooleanExtra("progressIndeterminate", false)
            val showChronometer = intent.getBooleanExtra("showChronometer", false)
            val isGroupConversation = intent.getBooleanExtra("isGroupConversation", false)
            val isAppGroup = intent.getBooleanExtra("isAppGroup", false)
            val isGroup = intent.getBooleanExtra("isGroup", false)
            val isOngoing = intent.getBooleanExtra("isOngoing", false)
            val groupKey = intent.getStringExtra("group_key")
            val key = intent.getStringExtra("key")
            var tag = intent.getStringExtra("tag")
            if (tag == null) {
                tag = ""
            }
            val uid = intent.getIntExtra("uId", 0)
            var template = intent.getStringExtra("template")
            if (template == null) {
                template = ""
            }
            val category = intent.getStringExtra("category")
            val appName = intent.getStringExtra("appName")
            val title = intent.getCharSequenceExtra("title")
            val text = intent.getCharSequenceExtra("text")
            val bigText = intent.getCharSequenceExtra("bigText")
            val color = intent.getIntExtra("color", -1)
            val isClearable = intent.getBooleanExtra("isClearable", true)
            val isAdded = intent.getBooleanExtra("isAdded", true)
            val postTime = intent.getLongExtra("postTime", Calendar.getInstance().time.time)
            val icon = Utils.getBitmapFromByteArray(intent.getByteArrayExtra("icon"))
            val largeIcon = Utils.getBitmapFromByteArray(intent.getByteArrayExtra("largeIcon"))
            val picture = Utils.getBitmapFromByteArray(intent.getByteArrayExtra("picture"))
            val extraTitle = intent.getCharSequenceExtra("extraTitle")
            val pendingIntent = intent.getParcelableExtra<Parcelable>("") as PendingIntent?
            val contentIntent =
                intent.getParcelableExtra<Parcelable>("contentIntent") as PendingIntent?
            var notification: Notification? = null

            arrayList = try {
                intent.getParcelableArrayListExtra<ActionParsable>("actions") as ArrayList<ActionParsable>
            } catch (unused: java.lang.Exception) {
                null
            }
            Log.d("TAG", "updateNotificationList: "+appName)

            if (isAdded) {
                notification = Notification(
                    icon,
                    largeIcon,
                    title,
                    text,
                    0,
                    packageName,
                    postTime,
                    pendingIntent,
                    arrayList,
                    bigText,
                    appName,
                    isClearable,
                    color,
                    picture,
                    groupKey,
                    key,
                    isGroupConversation,
                    isAppGroup,
                    isGroup,
                    isOngoing,
                    tag,
                    uid,
                    template,
                    substName,
                    subText,
                    titleBig,
                    info_text,
                    progressMax,
                    progress,
                    progressIndeterminate,
                    summaryText,
                    showChronometer,
                    category,
                    extraTitle.toString(),
                    contentIntent

                )
                var existingIndex = -1
                for (i in 0 until listSmallDynamicIsland.size) {
                    val n = listSmallDynamicIsland.elementAt(i)
                    if (!n.isLocal && n.groupKey == groupKey) {
                        existingIndex = i
                        break
                    }
                }
                val sameGroup = groupKey == key

                if (existingIndex != -1) {
                    val existing: Notification = listSmallDynamicIsland.elementAt(existingIndex)
                    existing.isClearable = isClearable
                    existing.progress = progress
                    existing.progressMax = progressMax
                    existing.progressIndeterminate = progressIndeterminate
                    updateNotificationItem(notification, existingIndex)
                } else {
                    listSmallDynamicIsland.push(notification)
                }
            } else {
                for (i in 0 until listSmallDynamicIsland.size) {
                    val n = listSmallDynamicIsland.elementAt(i)
                    if (n.key == key) {
                        listSmallDynamicIsland.remove(n)
                        Log.d("TAG", "updateNotificationList: " + listSmallDynamicIsland.size)
                        break
                    }
                }
                if (isShowingFullIsland()) {
                    if (viewManager?.getKeyOfFullIsland() == key) {
                        closeFullNotificationIsland()
                    }
                }

            }
            if (notification == null || notification.category != (NotificationCompat.CATEGORY_CALL)) {
                showSmallIslandNotification()
            } else if (notification.isOngoing) {
                showFullIslandNotification(notification)
            } else if (!notification.isOngoing || notification.actions == null || notification.actions.size != 2) {
                closeFullNotificationIsland()
            } else {
                showFullIslandNotification(notification)
            }

            setNotification(listSmallDynamicIsland)
            viewManager?.updateDisplayMode(listSmallDynamicIsland.size)

        }


    }

    private fun setNotification(lst: ArrayDeque<Notification>) {
        viewManager?.setNotification(lst)
    }

    private fun showSmallIslandNotification() {
//        if (listSmallDynamicIsland.isNotEmpty()) {
        viewManager?.showSmallIslandNotification()
//        }
    }

    private fun closeAll() {
        viewManager?.closeAll()
    }

    private fun closeSmallIslandNotification() {
        viewManager?.closeSmallIslandNotification()
    }

    private fun showFullIslandNotification(notification: Notification) {
        viewManager?.showFullIslandNotification(notification)
    }

    private fun closeFullNotificationIsland() {
        viewManager?.closeFullNotificationIsland()
    }

    private fun showAgain() {
        viewManager?.showAgain()
    }

    private fun isShowingFullIsland(): Boolean {
        return viewManager?.isShowingFullIsland() == true
    }

    private fun updateNotificationItem(notification2: Notification, i: Int) {
        listSmallDynamicIsland.elementAt(i).apply {
            senderIcon = notification2.senderIcon
            icon = notification2.icon
            actions = notification2.actions
            pendingIntent = notification2.pendingIntent
            title = notification2.title
            text = notification2.text
            postTime = notification2.postTime
            count = notification2.count
            bigText = notification2.bigText
            app_name = notification2.app_name
            isClearable = notification2.isClearable
            extraTitle = notification2.extraTitle
            color = notification2.color
            picture = notification2.picture
            template = notification2.template
            groupKey = notification2.groupKey
            isAppGroup = notification2.isAppGroup
            isGroup = notification2.isGroup
            isOngoing = notification2.isOngoing
            isGroupConversation = notification2.isGroupConversation
            showChronometer = notification2.showChronometer
            progress = notification2.progress
            progressMax = notification2.progressMax
            progressIndeterminate = notification2.progressIndeterminate
            category = notification2.category
        }

    }

    private fun isSameItem(notification: Notification): Boolean {
        if (notification.pack.contains("com.whatsapp") && notification.template.equals("")) {
            return true
        }
        if (!notification.pack.contains("com.google.android.gm")) {
            return false
        }
        return true
    }

    private fun unregisterReceiver() {
        try {
            unregisterReceiver(actionListener)
            unregisterReceiver(notificationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: ")
        isRunning = false
        config.controlEnable = false
        unregisterReceiver()
    }
}