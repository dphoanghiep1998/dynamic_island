package com.neko.hiepdph.dynamicislandvip.common.viewmanager

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.session.MediaSessionManager
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersionP
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.IClickBubbleListener
import com.neko.hiepdph.dynamicislandvip.common.customview.IClickListener
import com.neko.hiepdph.dynamicislandvip.common.customview.ViewBubble
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationQueueManager
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.common.toPx
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutViewDynamicIslandBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import kotlinx.coroutines.Runnable
import java.util.ArrayDeque

class ViewManager(
    private val context: MyAccessService
) {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var layoutParamsSpecialView: WindowManager.LayoutParams? = null
    private var layoutParamsFull: WindowManager.LayoutParams? = null
    private var isInFullScreen = false
    private lateinit var binding: LayoutViewDynamicIslandBinding
    private var listNotification = mutableListOf<Notification>()


    var margin: Int = 25

    var shakeAnimation: ObjectAnimator? = null
    var isControlEnabled: Boolean = true
    var tempMargin = 0
    var flagKeyBoard: Int = 8913696
    var flagNormal: Int = 8913704
    var mediaHandler: Handler = Handler()
    var currentNotification: Notification? = null
    var viewBubble: ViewBubble? = null
    var viewBubble2: ViewBubble? = null
    var isBubbleEnabled = true
    var handlerTimeShowBubble = Handler()
    var runnableTimeShowBubble: Runnable? = null
    var isBubbleShowing = false
    var runnableClear: Runnable? = null
    var handlerClearSpecialNotification: Handler = Handler()
    var handlerRemove = Handler()
    var runnableRemove: Runnable? = null
    var point: Point? = null

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
                    binding.rvIslandBig.updateMediaProgress(currentNotification)
                }
            }

            mediaHandler.postDelayed(
                this, 1000
            )
        }
    }

    init {
        binding = LayoutViewDynamicIslandBinding.inflate(LayoutInflater.from(context), null, false)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        point = Point()
        windowManager?.defaultDisplay?.getRealSize(point)

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

        layoutParamsSpecialView = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags = 8913704
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
            val yM =
                if (context.toPx(45).toInt() + context.config.dynamicMarginVertical > point!!.y) {
                    point!!.y - context.toPx(45).toInt()
                } else {
                    context.config.dynamicMarginVertical
                }
            x = context.toPx(40).toInt()
            y = yM
            height = context.toPx(45).toInt()
            width = (context.resources.displayMetrics.widthPixels - context.toPx(80).toInt())
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION

            if (buildMinVersionP()) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        layoutParamsFull = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags = 8913704
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
            height = -1
            width = -1
            x = 0
            val yM =
                if (context.toPx(150).toInt() + context.config.dynamicMarginVertical > point!!.y) {
                    point!!.y - context.toPx(150).toInt()
                } else {
                    context.config.dynamicMarginVertical
                }
            y = yM
        }
        runnableTimeShowBubble = Runnable {

            when (context.config.bubbleDisplayTime) {
                0 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 3000)
                }

                1 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 10000)
                }

                2 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 30000)
                }

                3 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 60000)
                }

                4 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 300000)
                }

                5 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 600000)
                }

                6 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 3000)
                }
            }
        }

        binding.statusbarParent.clickWithDebounce {
            if (context.config.clickMode == 0) {
                closeFullNotificationIsland()
            }
        }

        binding.statusbarParent.setOnLongClickListener {
            if (context.config.clickMode == 1) {
                closeFullNotificationIsland()
            }
            return@setOnLongClickListener true
        }
        binding.rvIslandBig.setAccessService(context)
        shakeAnimation = ObjectAnimator.ofFloat(
            binding.root, "translationX", 0f, 25f, -25f, 25f, -25f, 6f, -6f, 0f
        )

    }

    fun updateAnimation() {
        if (isShowingFullIsland()) {
            return
        }
        if (context.config.notificationAnimation == 1) {
            shakeAnimation?.start()
        } else {
            binding.islandParentLayout.startNeonRunner()
        }
    }

    fun updateDisplayMode(numOfNotification: Int) {
        if (isControlEnabled) {
            if (context.config.displayMode == 0) {
                if (!binding.root.isVisible) {
                    showAgain()
                }
            } else {
                runnableRemove?.let { handlerRemove.removeCallbacks(it) }
                if (numOfNotification == 0) {
                    runnableRemove = Runnable {
                        binding.root.animate().scaleX(.5f).scaleY(.5f).alpha(0f).setDuration(500)
                            .withEndAction {
                                closeAll()
                            }
                    }
                    runnableRemove?.let { handlerRemove.postDelayed(it, 500) }
                } else {
                    if (binding.root.isVisible) {

                    } else {
                        binding.root.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500)
                            .withStartAction {
                                showAgain()
                            }
                    }
                }

            }
        }
    }


    fun toggleView() {
        if (context.config.controlEnable) {
            isControlEnabled = true
            binding.root.show()
            addDynamicView()
            initAction()
        } else {
            removeDynamicIsland()
        }

    }

    fun getKeyOfFullIsland(): String? {
        return binding.rvIslandBig.getNotificationKey()
    }


    @SuppressLint("WrongConstant")
    private fun addDynamicView() {
        setFullIslandMargin(false)
        setKeyboardFlag(false)
        try {
            windowManager?.addView(binding.root, layoutParams)
            checkBubble()
            checkBackgroundNotificationColor()
            checkDisplayMode()
            updateAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "LMAOOO loi~ roi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBackgroundNotificationColor() {
        setBackGroundNotification()
        setBackgroundNotificationColor()
    }

    private fun checkBubble() {
        viewBubble = ViewBubble(context)
        viewBubble2 = ViewBubble(context)
        binding.bubblePositionLeft.removeAllViews()
        binding.bubblePositionRight.removeAllViews()

        binding.bubblePositionLeft.addView(
            viewBubble, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.bubblePositionRight.addView(
            viewBubble2, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        if (context.config.showBubble) {
            isBubbleEnabled = true
            if (context.config.bubbleFrequency == 1) {
                showBubble()
            } else {
                binding.bubblePositionLeft.hide()
                binding.bubblePositionRight.hide()
            }
        } else {
            binding.bubblePositionLeft.hide()
            binding.bubblePositionRight.hide()
            isBubbleEnabled = false
        }
    }

    private fun checkDisplayMode() {
        if (context.config.displayMode == 0) {
            showAgain()
        } else {
            if (listNotification.isNotEmpty()) {
                showAgain()
            } else {
                closeAll()
            }
        }
    }


    private fun initAction() {
        binding.rvIslandSmall.setListener(object : IClickListener {
            override fun onClick(notification: Notification, position: Int?) {
                Log.d("TAG", "onClick: ")
                if (context.config.clickMode == 0) {
                    showFullIslandNotification(notification)
                } else {
                    notification.contentIntent?.send()
                }
            }


            override fun onLongClick(notification: Notification?, position: Int?) {

                if (context.config.clickMode == 1) {
                    notification?.let { showFullIslandNotification(it) }
                } else {
                    notification?.contentIntent?.send()
                }
            }
        })
    }


    fun setNotification(lst: ArrayDeque<Notification>) {
        listNotification.clear()
        listNotification.addAll(lst)

        val listTemplate = lst.map { it.template }
        var isChecked = false
        for (i in listTemplate) {
            if (i != null && i.contains("MediaStyle")) {
                val index = listTemplate.indexOf(i)
                val itemNotification = lst.elementAt(index)
                if (context.config.showBubble) {
                    if (isBubbleEnabled) {
                        viewBubble?.setNotification(itemNotification)
                        viewBubble2?.setNotification(itemNotification)
                    }
                }
                isChecked = true
            }
        }
        if (!isChecked) {
            viewBubble?.setNotification(null)
            viewBubble2?.setNotification(null)
        }
        if (lst.isNotEmpty()) {
            runnableClear = Runnable {
                if (!isBubbleShowing && isBubbleEnabled) {
                    showBubble()
                }
                if (!isChecked) {
                    stopTimeoutBubble()
                    startTimeoutBubble()
                } else {
                    stopTimeoutBubble()
                }
                context.clearSpecialNotification()
                setBackGroundNotification()
                setBackgroundNotificationColor()
                binding.islandTopLayout.visibility = View.VISIBLE
                binding.rvIslandSmall.visibility = View.VISIBLE
                binding.rvIslandBig.visibility = View.GONE
                val point = Point()
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay?.getRealSize(point)
                val yM =
                    if (context.config.dynamicHeight + context.config.dynamicMarginVertical > point.y) {
                        point.y - context.config.dynamicHeight
                    } else {
                        context.config.dynamicMarginVertical
                    }
                layoutParams?.y = yM
                layoutParams?.x = context.config.dynamicMarginHorizontal
                layoutParams?.height = context.config.dynamicHeight
                layoutParams?.width = context.config.dynamicWidth

                windowManager.updateViewLayout(
                    binding.root, layoutParams
                )
                NotificationQueueManager.setBusy(false)
            }


            if (lst.map { it.type.lowercase() }
                    .contains(Constant.TYPE_AIRBUDS.lowercase()) || lst.map { it.type.lowercase() }
                    .contains(Constant.TYPE_CHARGING.lowercase())) {
                hideBubble()
                showSpecialView()
                runnableClear?.let { handlerClearSpecialNotification.removeCallbacks(it) }
                runnableClear?.let { handlerClearSpecialNotification.postDelayed(it, 3000) }
            } else if (lst.map { it.type.lowercase() }.contains(Constant.TYPE_SILENT.lowercase())) {
                runnableClear?.let { handlerClearSpecialNotification.removeCallbacks(it) }
                runnableClear?.let { handlerClearSpecialNotification.postDelayed(it, 3000) }
                hideBubble()
            } else {
                if (context.config.notificationDisplay) {
                    hideBubble()
                    showSpecialView()
                    runnableClear?.let { handlerClearSpecialNotification.removeCallbacks(it) }
                    runnableClear?.let { handlerClearSpecialNotification.postDelayed(it, 3000) }
                }
            }
        } else {
            if (isControlEnabled) {
                if (context.config.displayMode == 0) {
                    if (!binding.root.isVisible) {
                        showAgain()
                    }
                } else {
                    runnableRemove?.let { handlerRemove.removeCallbacks(it) }
                    if (listNotification.isEmpty()) {
                        runnableRemove = Runnable {
                            binding.root.animate().scaleX(.5f).scaleY(.5f).alpha(0f)
                                .setDuration(500).withEndAction {
                                    closeAll()
                                }
                        }
                        runnableRemove?.let { handlerRemove.postDelayed(it, 500) }
                    } else {
                        if (!binding.root.isVisible) {
                            binding.root.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500)
                                .withStartAction {
                                    showAgain()

                                }
                        }
                    }

                }
            }
        }

        binding.rvIslandSmall.setNotification(lst)

    }


    fun removeDynamicIsland() {
        if (windowManager != null) {
            windowManager?.removeView(binding.root)
            binding.root.hide()
            isControlEnabled = false
        }
    }


    fun showSpecialView() {
        val layoutParamsTop = binding.islandTopLayout.layoutParams
        layoutParamsTop.width = -1
        layoutParamsTop.height = -1
        binding.islandTopLayout.setLayoutParams(layoutParamsTop)
        binding.islandParentLayout.setBackgroundResource(R.drawable.rounded_rectangle_notification)
        setBackgroundNotificationColor()
        val layoutParamsParent = binding.islandParentLayout.layoutParams
        layoutParamsParent.width = 0
        layoutParamsParent.height = -1
        binding.islandParentLayout.setLayoutParams(layoutParamsParent)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.islandTopLayout) // your ConstraintLayout instance

        constraintSet.connect(
            binding.islandParentLayout.id, // ID of the view you're positioning
            ConstraintSet.START,
            binding.bubblePositionLeft.id, // ID of the target view (or ConstraintSet.PARENT_ID)
            ConstraintSet.END
        )
        constraintSet.connect(
            binding.islandParentLayout.id, // ID of the view you're positioning
            ConstraintSet.END,
            binding.bubblePositionRight.id, // ID of the target view (or ConstraintSet.PARENT_ID)
            ConstraintSet.START
        )
        constraintSet.applyTo(binding.islandTopLayout)

        windowManager?.updateViewLayout(binding.root, layoutParamsSpecialView)
    }


    fun closeFullNotificationIsland() {
        mediaHandler.removeCallbacks(mediaUpdateRunnable)
        setFullIslandMargin(false)
        setKeyboardFlag(false)

        val layoutParamsParent = binding.islandParentLayout.layoutParams
        layoutParamsParent.width = 0
        layoutParamsParent.height = -1
        binding.islandParentLayout.setLayoutParams(layoutParamsParent)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.islandTopLayout) // your ConstraintLayout instance

        constraintSet.connect(
            binding.islandParentLayout.id, // ID of the view you're positioning
            ConstraintSet.START,
            binding.bubblePositionLeft.id, // ID of the target view (or ConstraintSet.PARENT_ID)
            ConstraintSet.END
        )
        constraintSet.connect(
            binding.islandParentLayout.id, // ID of the view you're positioning
            ConstraintSet.END,
            binding.bubblePositionRight.id, // ID of the target view (or ConstraintSet.PARENT_ID)
            ConstraintSet.START
        )
        constraintSet.applyTo(binding.islandTopLayout)
        binding.rvIslandSmall.show()
        binding.rvIslandBig.hide()
        layoutParams?.height = (context.config.dynamicHeight)
        layoutParams?.width = (context.config.dynamicWidth)
        layoutParams?.x = (context.config.dynamicMarginHorizontal)

        val yM =
            if (context.config.dynamicHeight + context.config.dynamicMarginVertical > point!!.y) {
                point!!.y - context.config.dynamicHeight
            } else {
                context.config.dynamicMarginVertical
            }
        layoutParams?.y = yM
        windowManager?.updateViewLayout(binding.root, layoutParams)
    }

    fun showSmallIslandNotification() {
        Handler().postDelayed({
            if (isShowingFullIsland()) {
                closeFullNotificationIsland()
            }
            if (!isBubbleShowing && isBubbleEnabled) {
                showBubble()
            }
            stopTimeoutBubble()
            startTimeoutBubble()
            if (context.config.notificationAnimation == 1) {
                shakeAnimation?.start()
            } else {
                binding.islandParentLayout.startNeonRunner()
            }
            if (isShowingSmall()) {
                binding.islandTopLayout.show()
                binding.rvIslandSmall.show()
            } else if (!isControlEnabled) {

            } else {
                if (isShowingFullIsland()) {
                    return@postDelayed
                }
                if (!isInFullScreen) {
                    binding.islandTopLayout.visibility = View.VISIBLE
                    binding.rvIslandSmall.visibility = View.VISIBLE
                    binding.rvIslandBig.visibility = View.GONE

                    val yM =
                        if (context.config.dynamicHeight + context.config.dynamicMarginVertical > point!!.y) {
                            point!!.y - context.config.dynamicHeight
                        } else {
                            context.config.dynamicMarginVertical
                        }
                    layoutParams?.x = context.config.dynamicMarginHorizontal
                    layoutParams?.y = yM
                    layoutParams?.height = context.config.dynamicHeight
                    layoutParams?.width = context.config.dynamicWidth

                    windowManager?.updateViewLayout(
                        binding.root, layoutParams
                    )
                }
            }

        }, 300)
    }

    private fun setFullIslandMargin(z: Boolean) {
        if (z) {
            this.tempMargin = this.margin
            this.margin = (context.resources.displayMetrics.scaledDensity * 25.0f).toInt()
        } else {
            this.margin = this.tempMargin
        }
        val layoutParams = binding.islandTopLayout.layoutParams as FrameLayout.LayoutParams
        binding.islandTopLayout.setLayoutParams(layoutParams)
    }


    fun updateLayout() {
        if (isShowingFullIsland() || NotificationQueueManager.getBusy() || !isControlEnabled) {
            return
        }
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getRealSize(point)

        val yM =
            if (context.config.dynamicHeight + context.config.dynamicMarginVertical > point.y) {
                point.y - context.config.dynamicHeight
            } else {
                context.config.dynamicMarginVertical
            }

        val xM =
            if (context.config.dynamicMarginHorizontal + context.config.dynamicWidth > point.x) {
                point.x - context.config.dynamicWidth
            } else {
                context.config.dynamicMarginHorizontal
            }
        layoutParams?.apply {
            height = context.config.dynamicHeight
            width = context.config.dynamicWidth
            y = yM
            x = xM
        }
//        val layoutParams1 = binding.islandParentLayout.layoutParams
//        layoutParams1.width = context.config.dynamicWidth
//        layoutParams1.height = context.config.dynamicHeight
//        binding.islandParentLayout.layoutParams = layoutParams1
        windowManager?.updateViewLayout(binding.root, layoutParams)

    }

    fun updateLayoutBubble() {
        if (isBubbleEnabled && !context.config.showBubble) {
            stopTimeoutBubble()
            hideBubble()
            isBubbleEnabled = false
        }
        if (context.config.showBubble) {
            isBubbleEnabled = true
            if (context.config.bubbleFrequency == 1) {
                showBubble()
            } else {
                if (listNotification.isNotEmpty()) {
                    showBubble()
                    if (listNotification.map { it.template }.contains("MediaStyle")) {
                        stopTimeoutBubble()
                    } else {
                        stopTimeoutBubble()
                        startTimeoutBubble()
                    }

                } else {
                    hideBubble()
                }
            }
        }

        viewBubble?.updateBackgroundForBubble()
        viewBubble2?.updateBackgroundForBubble()


    }

    fun startTimeoutBubble() {
        if (context.config.bubbleFrequency == 0) {
            when (context.config.bubbleDisplayTime) {
                0 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 3000)
                }

                1 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 10000)
                }

                2 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 30000)
                }

                3 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 60000)
                }

                4 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 300000)
                }

                5 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 600000)
                }

                6 -> {
                    handlerTimeShowBubble.postDelayed({ hideBubble() }, 3000)
                }
            }
        }
    }

    fun stopTimeoutBubble() {
        runnableTimeShowBubble?.let { handlerTimeShowBubble?.removeCallbacks(it) }
    }


    fun showBubble() {
        isBubbleShowing = true
        viewBubble?.setListener(object : IClickBubbleListener {
            override fun onClick(
                notification: Notification?, position: Int?
            ) {
                Log.d("TAG", "onClick: ")
                notification?.let { showFullIslandNotification(it) }
            }

            override fun onLongClick(
                notification: Notification?, position: Int?
            ) {
            }

        })
        viewBubble2?.setListener(object : IClickBubbleListener {
            override fun onClick(
                notification: Notification?, position: Int?
            ) {
                Log.d("TAG", "onClick: ")
                notification?.let { showFullIslandNotification(it) }
            }

            override fun onLongClick(
                notification: Notification?, position: Int?
            ) {
            }

        })

        if (context.config.bubbleLocation == 0) {
            binding.bubblePositionLeft.show()
            binding.bubblePositionRight.hide()

            binding.bubblePositionLeft.translationX = 300f
            binding.bubblePositionLeft.alpha = 0f
            binding.bubblePositionLeft.animate().alpha(1f).translationX(0f)
                .setDuration(400) // 300ms animation
                .start()


        } else {
            binding.bubblePositionLeft.hide()
            binding.bubblePositionRight.show()
            binding.bubblePositionRight.translationX = -300f
            binding.bubblePositionRight.alpha = 0f
            binding.bubblePositionRight.animate().alpha(1f).translationX(0f)
                .setDuration(400) // 300ms animation
                .start()

        }

        viewBubble?.updateBackgroundForBubble()
        viewBubble2?.updateBackgroundForBubble()

    }

    fun hideBubble() {
        isBubbleShowing = false
        if (context.config.bubbleLocation == 0) {
            binding.bubblePositionLeft.hide()
        } else {
            binding.bubblePositionRight.hide()
        }
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

    fun hideSmallDynamicIsland() {
        binding.root.hide()
    }

    fun showFullIslandNotification(notification: Notification) {
        binding.bubblePositionLeft.hide()
        binding.bubblePositionRight.hide()
        currentNotification = notification
        binding.islandTopLayout.show()
        setBackgroundNotificationColor()
        binding.rvIslandSmall.visibility = View.GONE
        if (isShowingFullIsland()) {
            binding.rvIslandBig.setNotification(notification)
            return
        }

        if (notification.template != null && notification.template.contains("MediaStyle")) {
            setMediaUpdateHandler()
        }
        setFullIslandMargin(true)
        setKeyboardFlag(true)
        windowManager?.updateViewLayout(binding.root, layoutParamsFull)
        val layoutParams1 = binding.islandParentLayout.layoutParams as ConstraintLayout.LayoutParams
        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams1.height = -2

        binding.islandParentLayout.layoutParams = layoutParams1
        binding.rvIslandBig.show()
        binding.rvIslandBig.setNotification(notification)

//        }
    }


    fun isShowingFullIsland(): Boolean {
        return layoutParams?.height == -1 && binding.rvIslandBig.isVisible
    }

    fun isShowingSmall(): Boolean {
        return binding.rvIslandSmall.isVisible


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
        val layoutParams1 = binding.islandParentLayout.layoutParams
        layoutParams1?.width = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        layoutParams1?.height = (convertDpToPixel(
            1.0f, context
        ) * context.resources.displayMetrics.scaledDensity).toInt()
        binding.islandParentLayout.setLayoutParams(layoutParams1)
        setKeyboardFlag(false)
    }

    private fun setMediaUpdateHandler() {
        mediaHandler.postDelayed(this.mediaUpdateRunnable, 1000)
    }

    fun closeAll() {
        layoutParams?.width = 0
        layoutParams?.height = 0
        binding.rvIslandSmall.hide()
        binding.rvIslandBig.hide()
        binding.root.hide()
        windowManager?.updateViewLayout(binding.root, layoutParams)
        setKeyboardFlag(false)
    }

    fun showAgain() {
        binding.root.show()
        layoutParams?.width = context.config.dynamicWidth
        layoutParams?.height = context.config.dynamicHeight
        layoutParams?.x = context.config.dynamicMarginHorizontal
        layoutParams?.y = context.config.dynamicMarginVertical
        windowManager?.updateViewLayout(binding.root, layoutParams)
        binding.rvIslandSmall.show()

    }


    fun setBackgroundNotificationColor() {
        binding.islandParentLayout.backgroundTintList =
            ColorStateList.valueOf(context.config.backgroundDynamicColor.toColorInt())
    }

    fun setBackGroundNotification() {
//        if(context.config.notchStyle == 0){
//            binding.islandParentLayout.setBackgroundResource(R.drawable.rounded_rectangle_notification)
//        }else{
//            binding.islandParentLayout.setBackgroundResource(R.drawable.rounded_rectangle_notification_notch)
//        }

        binding.islandParentLayout.invalidate()
    }

    fun updateAdjustVibration() {
//        binding.rvIslandSmall.setAdjustVolumeConfig()
    }

    fun updateAdjustVolume() {
        binding.rvIslandSmall.setAdjustVolumeConfig()
    }

    fun updateLang() {

    }


}