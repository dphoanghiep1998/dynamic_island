package com.neko.hiepdph.dynamicislandvip.common.viewmanager

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.graphics.PixelFormat
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionP
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionR
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.customview.CustomRecyclerView
import com.neko.hiepdph.dynamicislandvip.common.customview.ItemOffsetDecoration2
import com.neko.hiepdph.dynamicislandvip.common.customview.StatusBarParentView
import com.neko.hiepdph.dynamicislandvip.common.notification.ActionParsable
import com.neko.hiepdph.dynamicislandvip.common.notification.CustomNotificationAdapter
import com.neko.hiepdph.dynamicislandvip.common.notification.CustomNotificationIconAdapter
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import java.util.Calendar
import java.util.Locale

class ViewManager(
    private val context: MyAccessService
) {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var statusBarParentView: View? = null
    private var statusBarView: StatusBarParentView? = null
    private var adapterDynamicIslandSmall: CustomNotificationIconAdapter? = null
    private var adapterDynamicIslandBig: CustomNotificationAdapter? = null
    private var rcvDynamicIslandSmall: RecyclerView? = null
    private var rcvDynamicIslandBig: CustomRecyclerView? = null
    private var isInFullScreen = false
    private var dynamicIslandParentLayout: LinearLayout? = null
    private var dynamicIslandTopLayout: LinearLayout? = null
    private var actionListener: BroadcastReceiver? = null
    private var notificationListener: BroadcastReceiver? = null
    private var listSmallDynamicIsland: ArrayList<Notification> = arrayListOf()
    private var listBigDynamicIsland: ArrayList<Notification> = arrayListOf()
    private var currentIndex: Int = 0
    private var filterPKG = arrayListOf<AppDetail>()
    var isControlEnabled: Boolean = true
    var tempMargin = 0
    var margin = 25
    var minCameIslandWidth = 150
    var pos = 1
    var flagKeyBoard: Int = 8913696
    var flagNormal: Int = 8913704
    var mediaHandler: Handler = Handler()
    var currentNotification: Notification? = null
    var mediaUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            currentNotification?.duration = getMediaDuration()
            currentNotification?.position = getMediaPosition()
            currentNotification?.let { currentNotification ->
                if (currentNotification.duration > 0) {
                    currentNotification.progressMax = 100
                    currentNotification.progress =
                        (((currentNotification.position.toFloat()) / (currentNotification.duration.toFloat())) * 100.0f).toInt()
                    currentNotification.progressIndeterminate = false
                    adapterDynamicIslandBig?.updateMediaProgress()
                }
            }

            mediaHandler.postDelayed(
                this, 1000
            )
        }
    }


    init {
        addDynamicView()
        initActionListener()
        initNotificationListener()
        initNotification()

    }

    private fun initNotification() {
        adapterDynamicIslandSmall = CustomNotificationIconAdapter(context,listSmallDynamicIsland, {}, { notification, i ->
            showFullIslandNotification(notification)
            currentIndex = i
        })
        rcvDynamicIslandSmall?.adapter = adapterDynamicIslandSmall
        rcvDynamicIslandSmall?.setHasFixedSize(true)
        rcvDynamicIslandSmall?.addItemDecoration(ItemOffsetDecoration2(context, 5))
        rcvDynamicIslandSmall?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapterDynamicIslandBig =
            CustomNotificationAdapter(context, listBigDynamicIsland, { notification, i -> })
        rcvDynamicIslandBig?.setAdapter(adapterDynamicIslandBig)
        rcvDynamicIslandBig?.setHasFixedSize(true)
        rcvDynamicIslandBig?.addItemDecoration(ItemOffsetDecoration2(context, 5))
        rcvDynamicIslandBig?.setLayoutManager(
            LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
        )
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
        rcvDynamicIslandSmall = statusBarParentView!!.findViewById(R.id.rv_island_small)
        rcvDynamicIslandBig = statusBarParentView!!.findViewById(R.id.rv_island_big)
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
                if (intent != null && intent.action == "from_notification_service" + context?.packageName) {
                    updateNotificationList(intent)

                }
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

    fun showSmallIslandNotification() {
        Handler().postDelayed({
            if (listSmallDynamicIsland.size == 0) {
                if (isShowingFullIsland()) {
                    closeFullNotificationIsland()
                }
                if (isShowingSmall()) {
                    closeSmallIslandNotification()
                }
            }
            if (adapterDynamicIslandSmall!!.itemCount >= 1) {
                rcvDynamicIslandSmall?.scrollToPosition(adapterDynamicIslandSmall!!.itemCount - 1)
            }
            if (isShowingSmall()) {
                dynamicIslandTopLayout?.visibility = View.VISIBLE
                rcvDynamicIslandSmall?.visibility = View.VISIBLE
            } else if (!isControlEnabled) {
            } else {
                if (isShowingFullIsland()) {
                    return@postDelayed
                }
                if (!isInFullScreen) {
                    dynamicIslandTopLayout?.visibility = View.VISIBLE
                    rcvDynamicIslandSmall?.visibility = View.VISIBLE
                    rcvDynamicIslandBig?.visibility = android.view.View.GONE
                    if (listSmallDynamicIsland.size == 0) {
                        val layoutParams =
                            dynamicIslandParentLayout?.layoutParams as LinearLayout.LayoutParams
                        layoutParams.width =
                            (context.resources.displayMetrics.scaledDensity * 0.0f).toInt()
                        dynamicIslandParentLayout?.setLayoutParams(layoutParams)
                        return@postDelayed
                    }
                    layoutParams?.height =
                        ((context.config.dynamicHeight.toFloat()) * context.resources.displayMetrics.scaledDensity).toInt()
                    layoutParams?.width = minCameIslandWidth
                    windowManager?.updateViewLayout(
                        statusBarParentView, layoutParams
                    )
                    val layoutParams2 =
                        dynamicIslandParentLayout?.layoutParams as LinearLayout.LayoutParams
                    layoutParams2.width = -1
                    layoutParams2.height = -1
                    dynamicIslandParentLayout?.setLayoutParams(layoutParams2)
                    if (adapterDynamicIslandSmall!!.itemCount >= 1) {
                        rcvDynamicIslandSmall?.scrollToPosition(adapterDynamicIslandSmall!!.itemCount - 1)
                    }
                }
            }
        }, 100)
    }

    fun updateNotificationList(intent: Intent) {
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
        val pendingIntent = intent.getParcelableExtra<Parcelable>("") as PendingIntent?
        var notification: Notification? = null

        arrayList = try {
            intent.getParcelableArrayListExtra<ActionParsable>("actions") as ArrayList<ActionParsable>
        } catch (unused: java.lang.Exception) {
            null
        }

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
                category
            )
            var existingIndex = -1
            for (i in 0 until listSmallDynamicIsland.size) {
                val n = listSmallDynamicIsland[i]
                if (!n.isLocal && n.groupKey == groupKey) {
                    existingIndex = i
                    break
                }
            }
            val sameGroup = groupKey == key

            if (existingIndex != -1) {
                val existing: Notification = listSmallDynamicIsland.get(existingIndex)
                existing.isClearable = isClearable
                existing.progress = progress
                existing.progressMax = progressMax
                existing.progressIndeterminate = progressIndeterminate

                if (sameGroup || notification.template == "InboxStyle" || notification.tag.lowercase(
                        Locale.getDefault()
                    ).contains("summary")
                ) {
                    updateNotificationItem(notification, existingIndex)
                } else if (!isSameItem(notification)) {
                    existing.keyMap[key] = notification
                }
            } else {
                listSmallDynamicIsland.add(notification)
            }
        }
        if (!isFilterPkgFound(packageName)) {
//            else if (Constants.getAutoCloseNoti(context) && notification.isOngoing) {
//                closeHeadsUpNotification(notification)
//            }
            if (notification == null || notification.category != (NotificationCompat.CATEGORY_CALL)) {
                showSmallIslandNotification()
            } else if (!notification.isOngoing || notification.actions == null || notification.actions.size != 2) {
                this.currentIndex = this.listSmallDynamicIsland.size - 1
                closeFullNotificationIsland()
            } else {
                showFullIslandNotification(notification)
            }
        }
        adapterDynamicIslandSmall?.notifyDataSetChanged()
        adapterDynamicIslandBig?.notifyDataSetChanged()

    }

    fun getMediaPosition(): Long {
        return try {
            (context.getSystemService("media_session") as MediaSessionManager).getActiveSessions(
                ComponentName(
                    context.applicationContext, NotificationListener::class.java
                )
            )[0].playbackState!!.position
        } catch (unused: java.lang.Exception) {
            0
        }
    }

    fun showFullIslandNotification(notification: Notification) {
        currentNotification = notification
        if (!notification.isLocal) {
            dynamicIslandTopLayout?.show()
            rcvDynamicIslandSmall?.visibility = View.GONE
            if (isShowingFullIsland()) {
                listBigDynamicIsland.clear()
                adapterDynamicIslandBig?.notifyItemChanged(0)
                listBigDynamicIsland.add(notification)
                adapterDynamicIslandBig?.notifyItemChanged(0)
                return
            }
            if (notification.template.equals("MediaStyle")) {
                setMediaUpdateHandler()
            }
            setFullIslandMargin(true)
            setKeyboardFlag(true)
            layoutParams?.height = -1
            layoutParams?.width = (context.resources.displayMetrics.scaledDensity * 350.0f).toInt()
            windowManager?.updateViewLayout(this.statusBarParentView, this.layoutParams)
            val layoutParams = this.dynamicIslandParentLayout?.layoutParams
            layoutParams?.width = -1
            if (notification.category == (NotificationCompat.CATEGORY_CALL) && notification.isOngoing && notification.useIphoneCallDesign) {
                layoutParams?.height =
                    (context.resources.displayMetrics.scaledDensity * 90.0f).toInt()
            } else if (notification.actions == null || notification.actions.size <= 0) {
                layoutParams?.height =
                    (context.resources.displayMetrics.scaledDensity * 110.0f).toInt()
            } else {
                layoutParams?.height =
                    (context.resources.displayMetrics.scaledDensity * 170.0f).toInt()
            }
            dynamicIslandParentLayout?.setLayoutParams(layoutParams)
            rcvDynamicIslandBig?.visibility = View.VISIBLE
            listBigDynamicIsland.clear()
            adapterDynamicIslandBig?.notifyItemChanged(0)
            listBigDynamicIsland.add(notification)
            adapterDynamicIslandBig?.notifyItemChanged(0)
        }
    }


    fun closeHeadsUpNotification(notification: Notification?) {
        Handler(Looper.getMainLooper()).postDelayed({
            val displayMetrics = context.resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels.toDouble()
            val screenWidth = displayMetrics.widthPixels.toFloat()

            val startX = screenWidth * 0.75f  // 3/4 of screen width
            val startY = (screenHeight * 0.1).toFloat()
            val endY = (screenHeight * 0.01).toFloat()

            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(startX, endY)
            }

            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 100, 50)).build()

            context.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    notification?.let { showFullIslandNotification(it) }
                }
            }, null)

        }, 700)
    }

    fun isShowingFullIsland(): Boolean {
        return layoutParams?.height == -1 && dynamicIslandTopLayout?.visibility == View.VISIBLE
    }

    fun isShowingSmall(): Boolean {
        return layoutParams?.width == this.minCameIslandWidth && this.dynamicIslandTopLayout?.visibility == View.VISIBLE
    }

    fun isFilterPkgFound(str: String?): Boolean {
        val it: Iterator<AppDetail> = this.filterPKG.iterator()
        while (it.hasNext()) {
            if (it.next().pkg == str) {
                return true
            }
        }
        return false
    }

    private fun updateNotificationItem(notification2: Notification, i: Int) {
        listSmallDynamicIsland[i].senderIcon = notification2.senderIcon
        listSmallDynamicIsland[i].icon = notification2.icon
        listSmallDynamicIsland[i].actions = notification2.actions
        listSmallDynamicIsland[i].pendingIntent = notification2.pendingIntent
        listSmallDynamicIsland[i].title = notification2.title
        listSmallDynamicIsland[i].text = notification2.text
        listSmallDynamicIsland[i].postTime = notification2.postTime
        listSmallDynamicIsland[i].count = notification2.count
        listSmallDynamicIsland[i].bigText = notification2.bigText
        listSmallDynamicIsland[i].app_name = notification2.app_name
        listSmallDynamicIsland[i].isClearable = notification2.isClearable
        listSmallDynamicIsland[i].color = notification2.color
        listSmallDynamicIsland[i].picture = notification2.picture
        listSmallDynamicIsland[i].template = notification2.template
        listSmallDynamicIsland[i].groupKey = notification2.groupKey
        listSmallDynamicIsland[i].isAppGroup = notification2.isAppGroup
        listSmallDynamicIsland[i].isGroup = notification2.isGroup
        listSmallDynamicIsland[i].isOngoing = notification2.isOngoing
        listSmallDynamicIsland[i].isGroupConversation = notification2.isGroupConversation
        listSmallDynamicIsland[i].showChronometer = notification2.showChronometer
        listSmallDynamicIsland[i].progress = notification2.progress
        listSmallDynamicIsland[i].progressMax = notification2.progressMax
        listSmallDynamicIsland[i].progressIndeterminate = notification2.progressIndeterminate
        listSmallDynamicIsland[i].category = notification2.category
    }

    private fun isSameItem(notification: Notification): Boolean {
        if (notification.pack.equals("com.whatsapp") && notification.template.equals("")) {
            return true
        }
        if (!notification.pack.equals("com.google.android.gm")) {
            return false
        }
        return true
    }

    fun setKeyboardFlag(z: Boolean) {
        Handler().postDelayed({
            if (z) {
                layoutParams?.flags = flagKeyBoard
            } else {
                layoutParams?.flags = flagNormal
            }
            try {
                windowManager?.updateViewLayout(
                    statusBarParentView, layoutParams
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }, 300)
    }

    fun getMediaDuration(): Long {
        return try {
            (context.getSystemService("media_session") as MediaSessionManager).getActiveSessions(
                ComponentName(
                    context.applicationContext, NotificationListener::class.java
                )
            )[0].metadata!!.getLong("android.media.metadata.DURATION")
        } catch (unused: java.lang.Exception) {
            1
        }
    }

    private fun setFullIslandMargin(z: Boolean) {
        if (z) {
            tempMargin = margin
            margin = (context.resources.displayMetrics.scaledDensity * 25.0f).toInt()
        } else {
            margin = this.tempMargin
        }
        val layoutParams = dynamicIslandTopLayout?.layoutParams as FrameLayout.LayoutParams
        if (pos == 1) {
            layoutParams.leftMargin = margin
            layoutParams.rightMargin = 0
        }
        if (pos == 2) {
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = 0
        }
        if (pos == 3) {
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = margin
        }
        dynamicIslandTopLayout?.setLayoutParams(layoutParams)
    }

    fun closeSmallIslandNotification() {
        val layoutParams = dynamicIslandParentLayout?.getLayoutParams()
        layoutParams?.width = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        layoutParams?.height = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        dynamicIslandParentLayout?.setLayoutParams(layoutParams)
        setKeyboardFlag(false)
        Handler().postDelayed(
            {
                layoutParams?.height = (convertDpToPixel(
                    1.0f, context
                ) * context.resources.displayMetrics.scaledDensity).toInt()
                layoutParams?.width = (convertDpToPixel(
                    1.0f, context
                ) * context.resources.displayMetrics.scaledDensity).toInt()
                windowManager?.updateViewLayout(this.statusBarParentView, layoutParams)
                dynamicIslandTopLayout?.visibility = View.GONE
            }, 500
        )
    }

    private fun setMediaUpdateHandler() {
        mediaHandler.postDelayed(this.mediaUpdateRunnable, 1000)
    }
}