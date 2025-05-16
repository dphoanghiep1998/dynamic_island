package com.neko.hiepdph.dynamicislandvip.common.customview

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutSmallItemBinding
import java.util.ArrayDeque

class ViewDynamicIslandSmall @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var binding: LayoutSmallItemBinding
    private var listener: IClickListener? = null
    private var listNotification: MutableList<Notification> = mutableListOf()
    private var currentNotification: Notification? = null

    init {
        binding = LayoutSmallItemBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
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
        binding.root.setOnClickListener {

        }

        binding.root.setOnLongClickListener {
            listener?.onLongClick(null)
            return@setOnLongClickListener false
        }
    }

    fun assign(notification: Notification) {
        if (notification.template.contains("MediaStyle") && context.config.showBubble) {
            return
        }
        binding.chronometer.hide()

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
                binding.iconLeft.setImageResource(R.drawable.ic_ear_bud)
                binding.textLeft.setTextColor(notification.color)

                val airpodsBattery = Utils.getAirpodsBattery(context)
                if (airpodsBattery != -1) {
                    binding.textRight.text = "${Utils.getAirPodLevel(context)}%"
                } else {
                    binding.textRight.setText(R.string.airpods)
                }

                binding.textLeft.hide()
                binding.iconRight.hide()
                useDefaultLayout = false
            }

            Constant.TYPE_CHARGING.lowercase() -> {
                binding.iconLeft.visibility = View.GONE

                binding.textRight.apply {
                    setTextColor(notification.color)
                    text = notification.text

                }



                binding.textLeft.apply {
                    setTextColor(notification.color)
                    text = notification.title
                }

                binding.iconRight.visibility = View.VISIBLE
                binding.iconRight.setImageResource(
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
                binding.iconRight.visibility = View.GONE
                useDefaultLayout = false
            }
        }

        if (useDefaultLayout) {
            notification.icon?.let {
                binding.iconLeft.setImageBitmap(it)

                if (notification.showChronometer) {
                    binding.chronometer.visibility = View.VISIBLE
                    binding.chronometer.start()
                    notification.isChronometerRunning = true

                    binding.textLeft.text = ""
                    binding.iconLeft.setColorFilter(context.getColor(R.color.green_500))
                } else {
                    binding.textLeft.text = Utils.getFormattedDate(notification.postTime)
                }
            } ?: run {
                binding.iconLeft.setImageBitmap(null)
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
            } else if (notification.showChronometer && notification.category.equals(
                    NotificationCompat.CATEGORY_CALL, ignoreCase = true
                ) && notification.isOngoing
            ) {
                binding.textRight.visibility = View.GONE
                binding.iconRight.visibility = View.GONE
                binding.textRight.text = ""

                binding.rightLottie.setAnimation(R.raw.wave_call01)
                binding.rightLottie.visibility = View.VISIBLE
                if (!binding.rightLottie.isAnimating) {
                    binding.rightLottie.playAnimation()
                }
            } else if (!notification.template.contains("MediaStyle") || notification.isClearable) {
                binding.textRight.visibility = View.GONE
                binding.iconRight.setImageBitmap(notification.senderIcon)
            } else {
                binding.rightLottie.setAnimation(R.raw.music_wave)
                binding.rightLottie.visibility = View.VISIBLE
                if (!binding.rightLottie.isAnimating) {
                    binding.rightLottie.playAnimation()
                }
                binding.textRight.visibility = View.GONE
                binding.iconRight.visibility = View.GONE
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