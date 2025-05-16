package com.neko.hiepdph.dynamicislandvip.common.viewmanager

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.graphics.PixelFormat
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
import androidx.core.view.isVisible
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
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutViewDynamicIslandBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import java.util.ArrayDeque

class ViewManager(
    private val context: MyAccessService
) {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isInFullScreen = false
    private lateinit var binding: LayoutViewDynamicIslandBinding
    var margin: Int = 25

    var shakeAnimation: ObjectAnimator? = null
    var isControlEnabled: Boolean = true
    var tempMargin = 0
    var pos = 2
    var flagKeyBoard: Int = 8913696
    var flagNormal: Int = 8913704
    var mediaHandler: Handler = Handler()
    var currentNotification: Notification? = null
    var viewBubble: ViewBubble? = null

    var isBubbleEnabled = true

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
        addDynamicView()
        initAction()
    }


    @SuppressLint("WrongConstant")
    private fun addDynamicView() {
        binding = LayoutViewDynamicIslandBinding.inflate(LayoutInflater.from(context), null, false)
        binding.statusbarParent.clickWithDebounce {
            closeFullNotificationIsland()
        }
        binding.rvIslandBig.setAccessService(context)
        shakeAnimation = ObjectAnimator.ofFloat(
            binding.root, "translationX", 0f, 25f, -25f, 25f, -25f, 6f, -6f, 0f
        )
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        setFullIslandMargin(false)
        setKeyboardFlag(false)
//        val layoutParamsParent = binding.islandParentLayout.layoutParams
//        layoutParamsParent.width = (context.config.dynamicWidth)
//        layoutParamsParent.height = (context.config.dynamicHeight)
//        binding.islandParentLayout.setLayoutParams(layoutParamsParent)
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
        if (context.config.showBubble) {
            isBubbleEnabled = true
            showBubble()
        } else {
            isBubbleEnabled = false
        }

        try {
            windowManager?.addView(binding.root, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "LMAOOO loi~ roi", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initAction() {
        binding.rvIslandSmall.setListener(object : IClickListener {
            override fun onClick(notification: Notification, position: Int?) {
                if (context.config.notificationDisplay) {
                    showFullIslandNotification(notification)
                }
            }

            override fun onLongClick(notification: Notification?, position: Int?) {
                if (notification != null) {
                    notification.contentIntent?.send()
                } else {
                }
            }
        })
    }


    fun setNotification(lst: ArrayDeque<Notification>) {
        binding.rvIslandSmall.setNotification(lst)
        val listTemplate = lst.map { it.template }
        var isChecked = false
        for (i in listTemplate) {
            if (i.contains("MediaStyle")) {
                val index = listTemplate.indexOf(i)
                val itemNotification = lst.elementAt(index)
                Log.d("TAG", "setNotification: " + index)
                if (context.config.showBubble) {
                    if (isBubbleEnabled) {
                        viewBubble?.setNotification(itemNotification)
                    }
                }
                 isChecked = true
            }

        }
        if(!isChecked){
            viewBubble?.setNotification(null)
        }
    }


    fun closeFullNotificationIsland() {
        mediaHandler.removeCallbacks(mediaUpdateRunnable)
        setFullIslandMargin(false)
        setKeyboardFlag(false)
        layoutParams?.height = (context.config.dynamicHeight)
        layoutParams?.width = (context.config.dynamicWidth)
        layoutParams?.x = (context.config.dynamicMarginHorizontal)

//        val layoutParamsParent = binding.islandParentLayout.layoutParams
//        layoutParamsParent.width =  (context.config.dynamicWidth)
//        binding.islandParentLayout.setLayoutParams(layoutParamsParent)

//        Handler().postDelayed({
        val layoutParamsParent = binding.islandParentLayout.layoutParams
        layoutParamsParent.width = 0
        layoutParamsParent.height = -1
        binding.islandParentLayout.setLayoutParams(layoutParamsParent)
        if (context.config.bubbleFrequency == 0) {

        } else {
            if (context.config.bubbleLocation == 0) {
                binding.bubblePositionLeft.show()
                binding.bubblePositionRight.hide()

            } else {
                binding.bubblePositionLeft.hide()
                binding.bubblePositionRight.show()
            }
        }
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
        windowManager?.updateViewLayout(binding.root, layoutParams)

//        }, 300)

        binding.rvIslandSmall.visibility = View.VISIBLE
        binding.rvIslandBig.visibility = View.GONE
    }

    fun showSmallIslandNotification() {
        Handler().postDelayed({
            if (isShowingFullIsland()) {
                closeFullNotificationIsland()
            }
//                if (isShowingSmall()) {
//                    closeSmallIslandNotification()
//                }

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
                    layoutParams?.x = context.config.dynamicMarginHorizontal
                    layoutParams?.height = context.resources.displayMetrics.heightPixels
                    layoutParams?.width = context.resources.displayMetrics.widthPixels

                    windowManager?.updateViewLayout(
                        binding.root, layoutParams
                    )
//                    val layoutParams2 =
//                        binding.islandParentLayout.layoutParams as LinearLayout.LayoutParams
//                    layoutParams2.width =  context.resources.displayMetrics.widthPixels
//                    layoutParams2.height =  context.resources.displayMetrics.heightPixels
//                    binding.islandParentLayout.setLayoutParams(layoutParams2)


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


    fun updateLayout() {
        layoutParams?.apply {
            height = context.config.dynamicHeight
            width = context.config.dynamicWidth
            y = context.config.dynamicMarginVertical
            x = context.config.dynamicMarginHorizontal
        }
//        val layoutParams1 = binding.islandParentLayout.layoutParams
//        layoutParams1.width = context.config.dynamicWidth
//        layoutParams1.height = context.config.dynamicHeight
//        binding.islandParentLayout.layoutParams = layoutParams1
        windowManager?.updateViewLayout(binding.root, layoutParams)

    }

    fun updateLayoutBubble() {
        if (isBubbleEnabled && !context.config.showBubble) {
            hideBubble()
            isBubbleEnabled = false
        }
        if (context.config.showBubble) {
            showBubble()
            isBubbleEnabled = true
        }

        viewBubble?.updateBackgroundForBubble()


    }

    fun showBubble() {
        viewBubble = ViewBubble(context)
        viewBubble?.setListener(object : IClickBubbleListener{
            override fun onClick(
                notification: Notification?,
                position: Int?
            ) {
                Log.d("TAG", "onClick: ")
                if (context.config.notificationDisplay) {
                    notification?.let { showFullIslandNotification(it) }
                }
            }

            override fun onLongClick(
                notification: Notification?,
                position: Int?
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
            binding.bubblePositionLeft.addView(
                viewBubble, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

        } else {
            binding.bubblePositionLeft.hide()
            binding.bubblePositionRight.show()
            binding.bubblePositionRight.translationX = -300f
            binding.bubblePositionRight.alpha = 0f
            binding.bubblePositionRight.animate().alpha(1f).translationX(0f)
                .setDuration(400) // 300ms animation
                .start()
            binding.bubblePositionRight.addView(
                viewBubble, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        viewBubble?.updateBackgroundForBubble()

    }

    fun hideBubble() {
        if (context.config.bubbleLocation == 0) {
            binding.bubblePositionLeft.hide()
            binding.bubblePositionLeft.removeAllViews()

        } else {
            binding.bubblePositionRight.hide()
            binding.bubblePositionRight.removeAllViews()
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
        Log.d("TAG", "showFullIslandNotification: " + notification.isLocal)
//        if (!notification.isLocal) {
        binding.islandTopLayout.show()
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

        layoutParams?.height = -1
        layoutParams?.width = -1
        layoutParams?.x = 0
        windowManager?.updateViewLayout(binding.root, layoutParams)
        val layoutParams1 = binding.islandParentLayout.layoutParams as ConstraintLayout.LayoutParams
        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams1.height = -2

        binding.islandParentLayout.layoutParams = layoutParams1
        binding.rvIslandBig.show()
        binding.rvIslandBig.setNotification(notification)

//        }
    }

    fun hideSmallNotificationIsland() {

    }


    fun closeHeadsUpNotification(notification: Notification?) {
        notification?.let {
            showFullIslandNotification(it)
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            val displayMetrics = context.resources.displayMetrics
//            val screenHeight = displayMetrics.heightPixels.toDouble()
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//
//            val startX = screenWidth * 0.75f  // 3/4 of screen width
//            val startY = (screenHeight * 0.1).toFloat()
//            val endY = (screenHeight * 0.01).toFloat()
//
//            val path = Path().apply {
//                moveTo(startX, startY)
//                lineTo(startX, endY)
//            }
//
//            val gesture = GestureDescription.Builder()
//                .addStroke(GestureDescription.StrokeDescription(path, 100, 50)).build()
//            Log.d("TAG", "closeHeadsUpNotification: ")
//            context.dispatchGesture(
//                gesture, object : AccessibilityService.GestureResultCallback() {
//                    override fun onCompleted(gestureDescription: GestureDescription) {
//                        super.onCompleted(gestureDescription)
//                        notification?.let{
//                        Log.d("TAG", "onCompleted: ")
//                            showFullIslandNotification(it)
//                        }
//                    }
//                }, null
//            )
//
//        }, 700)
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
        windowManager?.updateViewLayout(binding.root, layoutParams)
        setKeyboardFlag(false)

    }

    fun showAgain() {
        layoutParams?.width = context.config.dynamicWidth
        layoutParams?.height = context.config.dynamicHeight
        layoutParams?.x = context.config.dynamicMarginHorizontal
        layoutParams?.y = context.config.dynamicMarginVertical
        windowManager?.updateViewLayout(binding.root, layoutParams)
        binding.rvIslandSmall.show()
    }


}