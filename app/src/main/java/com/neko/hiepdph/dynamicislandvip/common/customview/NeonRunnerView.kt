package com.neko.hiepdph.dynamicislandvip.common.customview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.toPx

class NeonRunnerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var shouldRunAnimation = true

    private val paint = Paint()
    private val backgroundPaint = Paint().apply {
        color = Color.BLACK

    }
    private var innerRect = RectF()
    private var rect = RectF()
    private var angle = 0f
    val animator = ValueAnimator.ofFloat(0f, 360f)

    init {
        paint.style = Paint.Style.FILL
        animator.addUpdateListener {
            angle = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                shouldRunAnimation = false
                invalidate()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {
                // Gọi mỗi khi animation lặp lại
            }
        })
        animator.interpolator = AccelerateInterpolator()
        animator.duration = 300
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = 3
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = context.toPx(2)
        rect = RectF(
            0f, 0f, width.toFloat(), height.toFloat()
        )
        innerRect = RectF(
            0f + inset, 0f + inset, width.toFloat() - inset, height.toFloat() - inset
        )

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (context.config.notchStyle == 0) {
            setBackgroundResource(R.drawable.rounded_rectangle_notification_trans)
            backgroundPaint.color = convertHexToColorInt(context.config.backgroundDynamicColor)
            val colors = intArrayOf(
                convertHexToColorInt(context.config.animColor),
                convertHexToColorInt(context.config.backgroundDynamicColor),
                convertHexToColorInt(context.config.backgroundDynamicColor),
                convertHexToColorInt(context.config.animColor),
            )
            val radius = context.toPx(40)
            val positions = floatArrayOf(0f, 0.25f, 0.75f, 1f)

            val centerX = width / 2f
            val centerY = height / 2f
            if (shouldRunAnimation) {
                val shader = SweepGradient(centerX, centerY, colors, positions)
                val matrix = Matrix()
                matrix.preRotate(angle, centerX, centerY)
                shader.setLocalMatrix(matrix)
                paint.shader = shader
            } else {
                paint.shader = null
            }

            canvas.drawRoundRect(rect, radius, radius, paint)
            if (shouldRunAnimation) {
                canvas.drawRoundRect(innerRect, radius, radius, backgroundPaint)
            }else{
                canvas.drawRoundRect(rect, radius, radius, backgroundPaint)
            }
        } else {
            setBackgroundResource(R.drawable.rounded_rectangle_notification_notch)
        }


    }

    fun reset() {
        shouldRunAnimation = false
        invalidate()
    }

    fun startNeonRunner() {
        shouldRunAnimation = true
        animator.start()
        invalidate()
    }

    fun convertHexToColorInt(hex: String): Int {
        val cleanHex = hex.removePrefix("#")
        val argbHex = when (cleanHex.length) {
            6 -> "FF$cleanHex" // nếu là RRGGBB, thêm alpha FF
            8 -> cleanHex      // đã có alpha AARRGGBB
            else -> 0x00000000.toString(16) // không hợp lệ, trả về màu đen
        }
        return argbHex.toLong(16).toInt()
    }

}