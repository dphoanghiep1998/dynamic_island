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
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.Utils.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionP
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.ItemOffsetDecoration2
import com.neko.hiepdph.dynamicislandvip.common.notification.ActionParsable
import com.neko.hiepdph.dynamicislandvip.common.notification.CustomNotificationAdapter
import com.neko.hiepdph.dynamicislandvip.common.notification.CustomNotificationIconAdapter
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutViewDynamicIslandBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import java.util.Calendar
import java.util.Locale

class ViewManager(
    private val context: MyAccessService
) {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isInFullScreen = false
    private var actionListener: BroadcastReceiver? = null
    private var notificationListener: BroadcastReceiver? = null
    private var listSmallDynamicIsland: ArrayList<Notification> = arrayListOf()
    private var listBigDynamicIsland: ArrayList<Notification> = arrayListOf()
    private var currentIndex: Int = 0
    private var filterPKG = arrayListOf<AppDetail>()
    private var firstInit = true
    private lateinit var binding: LayoutViewDynamicIslandBinding
    var margin: Int = 25
    var adapter_island_big: CustomNotificationAdapter? = null
    var isPhoneLocked: Boolean = false

    var adapter_island_small: CustomNotificationIconAdapter? = null

    var isControlEnabled: Boolean = true
    var tempMargin = 0
    var pos = 2
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
//                    adapterDynamicIslandBig?.updateMediaProgress()
                }
            }

            mediaHandler.postDelayed(
                this, 1000
            )
        }
    }

    var runnableFullScreen = object : Runnable {
        override fun run() {


        }
    }


    init {
        addDynamicView()
        initActionListener()
        initNotificationListener()
        initNotifications()

    }

    private fun addDynamicView() {
        binding = LayoutViewDynamicIslandBinding.inflate(LayoutInflater.from(context), null, false)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags = 8913704
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
            height = context.config.dynamicHeight
            width = context.config.dynamicWidth
            y = context.config.dynamicMarginVertical
            x = context.config.dynamicMarginHorizontal
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION
            if (buildMinVersionP()) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        try {
            windowManager?.addView(binding.root, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "LMAOOO loi~ roi", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initNotifications() {
        val customNotificationIconAdapter = CustomNotificationIconAdapter(context,
            listSmallDynamicIsland,
            onItemClicked = {},
            onItemClickedPos = { notification, i ->
                showFullIslandNotification(notification)
                currentIndex = i
            })


        adapter_island_small = customNotificationIconAdapter
        binding.rvIslandSmall.setAdapter(customNotificationIconAdapter)
        binding.rvIslandSmall.setHasFixedSize(true)
        binding.rvIslandSmall.addItemDecoration(
            ItemOffsetDecoration2(
                context, 5
            )
        )
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.rvIslandSmall.setLayoutManager(linearLayoutManager)
        adapter_island_big = CustomNotificationAdapter(
            context,
            this.listBigDynamicIsland,
            onItemClicked = { notification, i ->
                showFullIslandNotification(notification);
//                i
                i?.let { index -> currentIndex = index }
            })
        val linearLayoutManager2 = LinearLayoutManager(context)
        linearLayoutManager2.orientation = RecyclerView.VERTICAL
        binding.rvIslandBig.setAdapter(this.adapter_island_big)
        binding.rvIslandBig.setHasFixedSize(true)
        binding.rvIslandBig.addItemDecoration(ItemOffsetDecoration2(context, 5))
        binding.rvIslandBig.setLayoutManager(linearLayoutManager2)
    }

    private fun initActionListener() {
        Log.d("TAG", "initActionListener: ")
        actionListener = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent != null) {
                    when (intent.action) {
                        "android.intent.action.BATTERY_CHANGED" -> {
                            if (!context.config.batteryChargeEnable) {
                                return
                            }
                            val status = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1)
                            if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
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
                                notification.text = "$batteryLevel%"
                                notification.color = context.getColor(R.color.green_500)
                                notification.title = if (batteryLevel < 100) {
                                    context.getString(R.string.battery_charging)
                                } else {
                                    context.getString(R.string.battery_full)
                                }

                            } else {
                                val iterator = listSmallDynamicIsland.iterator()
                                while (iterator.hasNext()) {
                                    val n = iterator.next()
                                    if (n.type == Constant.TYPE_CHARGING) {
                                        iterator.remove()
                                        break
                                    }
                                }
                            }
                        }

                        "android.media.RINGER_MODE_CHANGED" -> {
                            if (!context.config.ringingStateEnable) {
                                return
                            }
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
                                        color = context.getColor(R.color.red_500)
                                    }
                                }

                                1 -> {
                                    val vibrateNotification = Notification(
                                        Constant.TYPE_SILENT, R.drawable.vibration_icon, 0
                                    ).apply {
                                        title = context.getString(R.string.vibrate)
                                        color = context.getColor(R.color.purple_400)
                                    }
                                }
                            }
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
        mediaHandler.removeCallbacks(mediaUpdateRunnable)
        setFullIslandMargin(false)
        setKeyboardFlag(false)
        layoutParams?.height = (context.resources.displayMetrics.scaledDensity * 170f).toInt()

        windowManager?.updateViewLayout(binding.root, layoutParams)
        val layoutParams = binding.islandParentLayout.layoutParams
        layoutParams.width = (context.resources.displayMetrics.scaledDensity * 0.0f).toInt()
        layoutParams.height =
            ((30.toFloat()) * context.resources.displayMetrics.scaledDensity).toInt()
        binding.islandParentLayout.setLayoutParams(layoutParams)
        binding.rvIslandSmall.visibility = View.VISIBLE
        this.binding.rvIslandBig.visibility = View.GONE
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (listSmallDynamicIsland.size == 0) {
                    closeSmallIslandNotification()
                    return
                }
                layoutParams.height =
                    ((30.toFloat()) * context.resources.displayMetrics.scaledDensity).toInt()
                layoutParams.width = 150
                windowManager?.updateViewLayout(
                    binding.root, layoutParams
                )
                Handler().postDelayed(
                    {
                        val layoutParams =
                            binding.islandParentLayout.layoutParams as LinearLayout.LayoutParams
                        layoutParams.width = -1
                        layoutParams.height = -1
                        binding.islandParentLayout.setLayoutParams(layoutParams)
                        if (adapter_island_small!!.itemCount > currentIndex) {
                            binding.rvIslandSmall.scrollToPosition(currentIndex)
                        }
                    }, 100
                )
            }

            fun `comlockservicesMAccessibilityService$10`() {/*LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) MAccessibilityService.this.island_parent_layout.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                MAccessibilityService.this.island_parent_layout.setLayoutParams(layoutParams);
                if (MAccessibilityService.this.adapter_island_small.getItemCount() > MAccessibilityService.this.currentIndex) {
                    MAccessibilityService.this.rv_island_small.scrollToPosition(MAccessibilityService.this.currentIndex);
                }*/
            }
        }, 500)
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
            if (adapter_island_small!!.itemCount >= 1) {
                binding.rvIslandSmall.scrollToPosition(adapter_island_small!!.itemCount - 1)
            }

            if (isShowingSmall()) {
                binding.islandTopLayout.show()
                binding.rvIslandSmall.show()
            } else if (!isControlEnabled) {

            } else {
//                if ((isPhoneLocked && !Constant.getShowOnLock(mContext)) || isShowingFullIsland()) {
//                    return
//                }
                if (!isInFullScreen) {
                    binding.islandTopLayout.visibility = View.VISIBLE
                    binding.rvIslandSmall.visibility = View.VISIBLE
                    binding.rvIslandBig.visibility = View.GONE
                    if (listSmallDynamicIsland.size == 0) {
                        val layoutParams =
                            binding.islandParentLayout.layoutParams as LinearLayout.LayoutParams
                        layoutParams.width =
                            (context.resources.displayMetrics.scaledDensity * 0.0f).toInt()
                        binding.islandParentLayout.setLayoutParams(layoutParams)
                        return@postDelayed
                    }
                    layoutParams?.height = context.resources.displayMetrics.heightPixels
                    layoutParams?.width = context.resources.displayMetrics.widthPixels
                    windowManager?.updateViewLayout(
                        binding.root, layoutParams
                    )
                    val layoutParams2 =
                        binding.islandParentLayout.layoutParams as LinearLayout.LayoutParams
                    layoutParams2.width = -1
                    layoutParams2.height = -1
                    binding.islandParentLayout.setLayoutParams(layoutParams2)
                    if (adapter_island_small!!.getItemCount() >= 1) {
                        binding.rvIslandSmall.scrollToPosition(adapter_island_small!!.itemCount - 1)
                    }
                }
            }

        }, 100)
    }

    private fun setFullIslandMargin(z: Boolean) {
        if (z) {
            this.tempMargin = this.margin
            this.margin = (context.resources.displayMetrics.scaledDensity * 25.0f).toInt()
        } else {
            this.margin = this.tempMargin
        }
        val layoutParams = binding.islandTopLayout.layoutParams as FrameLayout.LayoutParams
        if (pos == 1) {
            layoutParams.leftMargin = this.margin
            layoutParams.rightMargin = 0
        }
        if (pos == 2) {
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = 0
        }
        if (pos == 3) {
            layoutParams.leftMargin = 0
            layoutParams.rightMargin = this.margin
        }
        binding.islandTopLayout.setLayoutParams(layoutParams)
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
                listSmallDynamicIsland.add(0, notification)
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
//        adapterDynamicIslandSmall?.notifyDataSetChanged()
//        adapterDynamicIslandBig?.notifyDataSetChanged()

    }

    fun updateLayout() {
        Log.d("TAG", "updateLayout: ")
        layoutParams?.apply {
            height = context.config.dynamicHeight
            width = context.config.dynamicWidth
            y = context.config.dynamicMarginVertical
            x = context.config.dynamicMarginHorizontal
        }
        windowManager?.updateViewLayout(binding.root, layoutParams)

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
            binding.islandTopLayout.show()
            binding.rvIslandSmall.visibility = View.GONE
            if (isShowingFullIsland()) {
                listBigDynamicIsland.clear()
                adapter_island_big?.notifyItemChanged(0)
                listBigDynamicIsland.add(notification)
                adapter_island_big?.notifyItemChanged(0)
                return
            }
            if (notification.template.equals("MediaStyle")) {
                setMediaUpdateHandler()
            }
            setFullIslandMargin(true)
            setKeyboardFlag(true)

            layoutParams?.height = -1
            layoutParams?.width = (context.resources.displayMetrics.scaledDensity * 350).toInt()

            windowManager?.updateViewLayout(binding.statusbarParent, layoutParams)
            val layoutParams = binding.islandParentLayout.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = -1

            if (notification.category.contains(NotificationCompat.CATEGORY_CALL) && notification.isOngoing && notification.useIphoneCallDesign) {
                layoutParams.height =
                    (context.resources.displayMetrics.scaledDensity * 90.0f).toInt()
            } else if (notification.actions == null || notification.actions.size <= 0) {
                layoutParams.height =
                    (context.resources.displayMetrics.scaledDensity * 110.0f).toInt()
            } else {
                layoutParams.height =
                    (context.resources.displayMetrics.scaledDensity * 170.0f).toInt()
            }

            binding.islandParentLayout.layoutParams = layoutParams
            binding.rvIslandBig.show()
            listBigDynamicIsland.clear()
            adapter_island_big?.notifyItemChanged(0)
            listBigDynamicIsland.add(notification)
            adapter_island_big?.notifyItemChanged(0)

        }
    }

    fun hideSmallNotificationIsland() {

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

            context.dispatchGesture(
                gesture, object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription) {
                        super.onCompleted(gestureDescription)
                        notification?.let { showFullIslandNotification(it) }
                    }
                }, null
            )

        }, 700)
    }

    fun isShowingFullIsland(): Boolean {
//        return layoutParams?.height == -1 && dynamicIslandTopLayout?.visibility == View.VISIBLE
        return false
    }

    fun isShowingSmall(): Boolean {
        return binding.rvIslandSmall.visibility == View.VISIBLE


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
                    binding.root, layoutParams
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


    fun closeSmallIslandNotification() {
        val layoutParams = binding.islandParentLayout.layoutParams
        layoutParams?.width = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        layoutParams?.height = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        binding.islandParentLayout.setLayoutParams(layoutParams)
        setKeyboardFlag(false)
        Handler().postDelayed(
            {
                layoutParams?.height = (convertDpToPixel(
                    1.0f, context
                ) * context.resources.displayMetrics.scaledDensity).toInt()
                layoutParams?.width = (convertDpToPixel(
                    1.0f, context
                ) * context.resources.displayMetrics.scaledDensity).toInt()
                windowManager?.updateViewLayout(binding.statusbarParent, layoutParams)
                binding.islandTopLayout.visibility = View.GONE
            }, 500
        )
    }

    private fun setMediaUpdateHandler() {
        mediaHandler.postDelayed(this.mediaUpdateRunnable, 1000)
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(actionListener)
            context.unregisterReceiver(notificationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}