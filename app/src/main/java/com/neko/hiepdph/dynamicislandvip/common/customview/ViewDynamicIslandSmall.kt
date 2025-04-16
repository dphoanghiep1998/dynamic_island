package com.neko.hiepdph.dynamicislandvip.common.customview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutViewDynamicIslandSmallBinding

class ViewDynamicIslandSmall(
    context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0, defStyleRes: Int=0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var animRing: AnimatorSet? = null
    private var show = false
    private var isAnimRunning = false
    private var binding: LayoutViewDynamicIslandSmallBinding

    init {
        alpha = 1f
        val ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, .96f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, .96f)
        )
        ofPropertyValuesHolder.apply {
            duration = 200
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            val animatorSet = AnimatorSet()
            animRing = animatorSet
            animatorSet.play(ofPropertyValuesHolder)

        }

        binding =
            LayoutViewDynamicIslandSmallBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    fun setNotification(notification: Notification) {
        binding.root.setOnLongClickListener(null)
        binding.root.isLongClickable = false


        binding.iconLeft.apply {
            clearColorFilter()
            setImageResource(0)
            show()
        }

        binding.iconRight.apply {
            clearColorFilter()
            setImageResource(0)
            show()
        }

        binding.textLeft.show()
        binding.textRight.show()

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
            }

            Constant.TYPE_CHARGING.lowercase() -> {
                binding.iconLeft.visibility = View.GONE

                binding.textRight.apply {
                    setTextColor(notification.color)
                    text = notification.text
                    setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, Utils.getBatteryImage(context), 0
                    )
                    compoundDrawablePadding = convertDpToPixel(5f, context).toInt()
                }

                binding.textLeft.apply {
                    setTextColor(notification.color)
                    text = notification.title
                }

                binding.iconRight.visibility = View.GONE
            }

            Constant.TYPE_SILENT.lowercase() -> {
                binding.iconLeft.apply {
                    setImageResource(notification.local_left_icon)
                }

                binding.textLeft.text = ""
                binding.textRight.apply {
                    text = notification.title
                    setTextColor(notification.color)
                }

                binding.iconRight.setImageResource(0)
                binding.iconRight.visibility = View.GONE
            }
        }
    }

    fun onGone() {
        show = false
        isAnimRunning = true
        animate().alpha(0f).scaleX(.8f).scaleY(.8f).setDuration(500).withEndAction {
            isAnimRunning = false
        }.start()
    }

    fun onShow() {
        show = true
        pivotX = width / 2f
        pivotY = height / 2f
        scaleX = .8f
        scaleY = .8f
        isAnimRunning = true
        animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500).withEndAction {
            isAnimRunning = false
        }.start()
    }
}