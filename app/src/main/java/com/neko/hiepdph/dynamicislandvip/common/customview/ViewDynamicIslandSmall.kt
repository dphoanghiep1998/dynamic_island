package com.neko.hiepdph.dynamicislandvip.common.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutSmallItemBinding
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class ViewDynamicIslandSmall @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var binding: LayoutSmallItemBinding
    private var listener: IClickListener? = null
    private var listNotification: MutableList<Notification> = mutableListOf()
    private var currentNotification: Notification? = null
    private var startY = 0f
    private var maxVolume = 0f
    private var audioManager: AudioManager? = null

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.toFloat() ?: 0f
        binding = LayoutSmallItemBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
        setAdjustVolumeConfig()
    }

    fun setAdjustVolumeConfig() {
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        if (context.config.adjustVolume) {
            var isSwipe = false
            binding.root.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = event.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val currentY = event.y
                        val deltaY = currentY - startY
                        if (abs(deltaY) > touchSlop + 10) {
                            val percent = currentY / height
                            val delta = maxVolume * percent
                            val volume = delta.roundToInt()
                            audioManager?.setStreamVolume(
                                AudioManager.STREAM_MUSIC, (maxVolume - volume).toInt(), 0
                            )
                            isSwipe = true
                        }
                    }

                    MotionEvent.ACTION_UP -> {
//                    binding.llVolume.gone()
                        if (isSwipe) {
                            isSwipe = false
                        } else {
//                            Log.d("TAG", "setAdjustVolumeConfig: ")
                            performClick()
                        }
                    }
                }
                false
            }
        } else {
            binding.root.setOnTouchListener(null)
        }
    }

    fun setListener(mListener: IClickListener) {
        listener = mListener
    }

    fun setNotification(lst: ArrayDeque<Notification>) {
        listNotification.clear()
        listNotification.addAll(lst)

        if (listNotification.isNotEmpty()) {
            currentNotification = listNotification[0]
            currentNotification?.let { assign(it) }
        } else {
            currentNotification = null
            reset()
        }

    }


    fun reset() {
        binding.rightLottie.hide()
        binding.rightLottie.pauseAnimation()
        binding.iconLeft.apply {
            setImageResource(0)
            show()
        }
        binding.iconRight.apply {
            setImageResource(0)
            show()
        }
        binding.textLeft.text = ""
        binding.textRight.text = ""
        binding.textRightSpecial.text = ""
        binding.iconRightSpecial.setImageResource(0)
        binding.textLeftSpecial.text = ""
        binding.iconLeftSpecial.setImageResource(0)

        binding.root.clickWithDebounce {

        }

        binding.root.setOnLongClickListener {
//            if (context.config.clickMode == 0) {
//                listener?.onLongClick(null)
//                return@setOnLongClickListener false
//            } else {
                currentNotification?.let { it1 -> listener?.onLongClick(it1) }
                return@setOnLongClickListener false
//            }


        }
    }

    fun assign(notification: Notification) {
        if (notification.template != null && notification.template.contains("MediaStyle") && context.config.showBubble) {
            return
        }
        binding.chronometer.hide()
        reset()

        if (!notification.isChronometerRunning) {
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
        }

        binding.root.setOnLongClickListener(null)
        binding.root.isLongClickable = false

        binding.rightLottie.hide()
        binding.rightLottie.pauseAnimation()

        binding.iconLeft.apply {
            setImageResource(0)
            show()
        }
        binding.iconRight.apply {
            setImageResource(0)
            show()
        }

        binding.textLeft.show()
        binding.textRight.show()
        binding.textLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.textRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        binding.textRight.setTextColor(Color.WHITE)
        binding.textLeft.setTextColor(Color.WHITE)

        var useDefaultLayout = true

        when (notification.type.lowercase()) {
            Constant.TYPE_AIRBUDS.lowercase() -> {
                binding.iconLeftSpecial.setImageResource(R.drawable.ic_ear_bud)
                val airpodsBattery = Utils.getAirpodsBattery(context)
                if (airpodsBattery != -1) {
                    binding.textRightSpecial.text = "${Utils.getAirPodLevel(context)}%"
                } else {
                    binding.textRightSpecial.setText(R.string.airpods)
                }
                binding.iconLeftSpecial.show()
                binding.textRightSpecial.show()

                binding.textRight.hide()
                binding.textLeft.hide()
                binding.iconRight.hide()
                useDefaultLayout = false
            }

            Constant.TYPE_CHARGING.lowercase() -> {
                binding.iconLeftSpecial.hide()
                binding.iconLeft.hide()
                binding.iconRight.hide()
                binding.textRight.hide()

                binding.textRightSpecial.apply {
                    setTextColor(notification.color)
                    text = notification.text

                }
                binding.textLeftSpecial.apply {
                    setTextColor(Color.WHITE)
                    text = notification.title
                }
                binding.iconRightSpecial.show()
                binding.iconRightSpecial.setImageResource(
                    Utils.getBatteryImage(context)
                )
                useDefaultLayout = false
            }

            Constant.TYPE_SILENT.lowercase() -> {

                binding.iconLeft.apply {
                    clearColorFilter()
                    setImageResource(notification.local_left_icon)
                }

                binding.textLeft.text = ""
                binding.textRight.apply {
                    text = notification.title
                    setTextColor(notification.color)
                }

                binding.iconRight.setImageResource(0)
                binding.iconRight.visibility = GONE
                useDefaultLayout = false
            }
        }
        if(context.config.notificationDisplay){
            useDefaultLayout = true
        }
        if (useDefaultLayout) {

            notification.icon?.let {
                binding.iconLeft.setImageBitmap(it)

            } ?: run {
                binding.iconLeft.setImageBitmap(null)
            }
            if (notification.showChronometer) {
                binding.chronometer.visibility = VISIBLE
                binding.chronometer.start()
                notification.isChronometerRunning = true

                binding.textLeft.text = ""
//                binding.iconLeft.setColorFilter(context.getColor(R.color.green_500))
            } else {
                binding.textLeft.text = Utils.getFormattedDate(notification.postTime)
            }

            if (notification.senderIcon == null) {
                binding.iconRight.setImageResource(0)
                binding.iconRight.visibility = View.GONE

                if (notification.title.isNullOrEmpty()) {
                    binding.textRight.visibility = View.GONE
                } else {
                    binding.textRight.visibility = View.VISIBLE
                    binding.textRight.text = notification.title.split(" ").firstOrNull().orEmpty()
                }
            } else if (notification.showChronometer && notification.category.contains(
                    NotificationCompat.CATEGORY_CALL
                ) && notification.isOngoing
            ) {
                binding.textRight.visibility = GONE
                binding.iconRight.visibility = GONE
                binding.textRight.text = ""

                binding.rightLottie.setAnimation(R.raw.wave_call01)
                binding.rightLottie.visibility = VISIBLE
                if (!binding.rightLottie.isAnimating) {
                    binding.rightLottie.playAnimation()
                }
            } else if (!notification.template.contains("MediaStyle") || notification.isClearable) {
                binding.textRight.visibility = GONE
                binding.iconRight.setImageBitmap(notification.senderIcon)
            } else {
                binding.rightLottie.setAnimation(R.raw.music_wave)
                binding.rightLottie.visibility = VISIBLE
                if (!binding.rightLottie.isAnimating) {
                    binding.rightLottie.playAnimation()
                }
                binding.textRight.visibility = GONE
                binding.iconRight.visibility = GONE
            }

            binding.root.setOnClickListener {
                listener?.onClick(notification)

            }
            binding.root.setOnLongClickListener {
                listener?.onLongClick(notification)

                true
            }


        }
    }


}


interface IClickListener {
    fun onClick(notification: Notification, position: Int? = null)
    fun onLongClick(notification: Notification?, position: Int? = null)
}