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
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutViewDynamicIslandBinding

class ViewDynamicIsland(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var animRing: AnimatorSet? = null
    private var show = false
    private var isAnimRunning = false
    private var binding: LayoutViewDynamicIslandBinding

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

        binding = LayoutViewDynamicIslandBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    fun setNotification(notification: Notification) {
        binding.root.setOnLongClickListener(null)
        binding.root.isLongClickable = false
        binding.lottieRight.hide()
        binding.lottieRight.pauseAnimation()

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

        Log.d("TAG", "setNotification: " + notification.template)


        if (notification.title.isNullOrEmpty()) {
            binding.textRight.hide()
        } else {
            binding.textRight.show()
            binding.textRight.text = notification.title.split(" ").firstOrNull().orEmpty()
        }

        if (notification.senderIcon == null) {
            if(notification.icon != null){
                binding.iconLeft.setImageBitmap(notification.icon)
                binding.iconLeft.show()
            }else{
                binding.iconLeft.setImageResource(0)
                binding.iconLeft.hide()
            }
        }else{
            notification.icon?.let {
                binding.iconRight.setImageBitmap(it)
            } ?: run {
                binding.iconRight.setImageBitmap(null)

            }
            binding.iconLeft.setImageBitmap(notification.senderIcon)
        }
        if (notification.showChronometer && notification.category.equals(
                NotificationCompat.CATEGORY_CALL, ignoreCase = true
            ) && notification.isOngoing
        ) {
            //do when call
        }else if (!notification.template.contains("MediaStyle") || notification.isClearable) {
            binding.textRight.hide()
            binding.iconLeft.setImageBitmap(notification.senderIcon)
        } else {
            binding.iconLeft.hide()
            binding.iconAlbum.setImageBitmap(notification.senderIcon)
            binding.iconAlbum.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))
            binding.lottieRight.setAnimation(R.raw.music_wave)
            binding.lottieRight.show()
            if (!binding.lottieRight.isAnimating) {
                binding.lottieRight.playAnimation()
            }
            binding.iconRight.hide()
            binding.textRight.hide()
        }

        binding.root.setOnClickListener {
//                    onItemClicked.invoke(notification)
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