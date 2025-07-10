package com.neko.hiepdph.dynamicislandvip.common.customview

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutBubbleBinding

class ViewBubble @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var binding: LayoutBubbleBinding =
        LayoutBubbleBinding.inflate(LayoutInflater.from(context), this, false)
    private var listener: IClickBubbleListener? = null
    private var currentNotification: Notification? = null

    init {
        addView(binding.root)
        updateBackgroundForBubble()
    }

    fun setListener(mListener: IClickBubbleListener) {
        listener = mListener
    }

    fun setNotification(lst: Notification?) {
        currentNotification = lst
        if (currentNotification != null) {
            currentNotification?.let { assign(it) }
        } else {
//            Log.d("TAG", "reset: ")
            reset()
        }
    }

    fun reset() {
        setOnClickListener {}

        setOnLongClickListener {
            listener?.onLongClick(currentNotification)
            return@setOnLongClickListener false
        }
        updateBackgroundForBubble()
        binding.bubbleInner.clearAnimation()
    }

    fun assign(notification: Notification) {
        Log.d("TAG", "assign: "+notification.senderIcon)
        binding.bubbleInner.setImageBitmap(notification.senderIcon)
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotate)
        binding.bubbleInner.startAnimation(animation)
        binding.root.setOnClickListener {
            listener?.onClick(currentNotification)
        }

        binding.root.setOnLongClickListener {
            listener?.onLongClick(currentNotification)
            return@setOnLongClickListener false
        }
    }

    fun updateBackgroundForBubble() {

        val colorBubbleBackground = if (context.config.showBubbleColor) {
            context.config.bubbleBackgroundColor.toColorInt()
        } else {
            "#000000".toColorInt()
        }

        val colorBubbleBorder = if (context.config.showBubbleBorder) {
            context.config.bubbleBackgroundBorder.toColorInt()
        } else {
            if(context.config.showBubbleColor){
                context.config.bubbleBackgroundColor.toColorInt()
            }else{
                "#000000".toColorInt()
            }
        }

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(colorBubbleBackground, colorBubbleBackground)
        ).apply {
            shape = GradientDrawable.OVAL
            gradientType = GradientDrawable.LINEAR_GRADIENT // or RADIAL_GRADIENT
            setStroke(
                context.config.sizeBorder,
                colorBubbleBorder,
            )
        }
        if(currentNotification == null){
            binding.bubbleInner.setImageBitmap(null)
        }
        binding.bubbleOuter.background = gradientDrawable
    }

}

interface IClickBubbleListener {
    fun onClick(notification: Notification?, position: Int? = null)
    fun onLongClick(notification: Notification?, position: Int? = null)
}