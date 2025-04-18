package com.neko.hiepdph.dynamicislandvip.common.customview

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.getFormattedTime
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutCallItemBinding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutListItemsBinding

class ViewDynamicIslandExtend(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var callBinding: LayoutCallItemBinding
    private var itemBinding: LayoutListItemsBinding
    private var mediaHandler: Handler = Handler()

    private var notification: Notification? = null
    var mediaUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            notification?.let { notification ->
                notification.duration = getMediaDuration()
                notification.position = getMediaPosition()
                if (notification.duration > 0) {
                    notification.progressMax = 100
                    notification.progress =
                        (((notification.position.toFloat()) / (notification.duration.toFloat())) * 100.0f).toInt()
                    notification.progressIndeterminate = false
                    updateMediaProgress()
                }
                mediaHandler.postDelayed(
                    this, 1000
                )
            }

        }
    }

    fun updateMediaProgress() {
        notification?.let { notification ->
            val formattedTime: String = getFormattedTime(notification.duration)
            val formattedTime2: String = getFormattedTime(notification.position)
            itemBinding.mediaDurationText.text = formattedTime
            itemBinding.mediaPosText.text = formattedTime2
            itemBinding.subProgressbar.progress = notification.progress
            itemBinding.subProgressbar.visibility = VISIBLE
            itemBinding.subProgressbar.setMax(notification.progressMax)
        }

    }

    init {
        callBinding = LayoutCallItemBinding.inflate(LayoutInflater.from(context), this, false)
        itemBinding = LayoutListItemsBinding.inflate(LayoutInflater.from(context), this, false)
    }

    fun getMediaDuration(): Long {
        return try {
            (context.getSystemService("media_session") as MediaSessionManager).getActiveSessions(
                ComponentName(
                    context, NotificationListener::class.java
                )
            )[0].metadata!!.getLong("android.media.metadata.DURATION")
        } catch (unused: Exception) {
            1
        }
    }

    private fun setMediaUpdateHandler() {
        mediaHandler.postDelayed(this.mediaUpdateRunnable, 1000)
    }

    fun getMediaPosition(): Long {
        return try {
            (context.getSystemService("media_session") as MediaSessionManager).getActiveSessions(
                ComponentName(
                    context, NotificationListener::class.java
                )
            )[0].playbackState!!.position
        } catch (unused: java.lang.Exception) {
            0
        }
    }

    fun setNotification(notification: Notification) {
        this.notification = notification
        if (notification.template.equals("MediaStyle")) {
            setMediaUpdateHandler()
        }
        itemBinding.apply {
            tvAppTitle.text = notification.app_name
            tvText.text = notification.title
            subTvText.text = notification.text
            groupMessageParent.removeAllViews()
            addTitleAndTextSubItems(groupMessageParent)
            notificationActionContainer.hide()
            notificationMaterialReplyContainer.hide()
            if(notification.icon != null){
                ivAppIcon.show()
                ivAppIcon.setImageBitmap(notification.icon)
            }
            if(notification.senderIcon != null){
                civSenderIcon2.visibility = View.INVISIBLE
                civSenderIcon.show()
                civSenderIcon.setImageBitmap(notification.senderIcon)
            }else{
                civSenderIcon2.setImageBitmap(notification.icon)
                civSenderIcon2.show()
                civSenderIcon.visibility = View.INVISIBLE
                ivAppIcon.visibility = View.INVISIBLE
            }

            if(notification.progressMax > 0){
                if(notification.showChronometer){
                    chronometer.show()
                    chronometer.start()
                }else{
                    chronometer.hide()
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.stop()
                }
                subProgressbar.show()
                subProgressbar.max = notification.progressMax
                subProgressbar.progress = notification.progress
                subProgressbar.isIndeterminate = notification.progressIndeterminate
            }else{
                subTvText.show()
                chronometer.show()
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.stop()
            }
            setOnClickListener {
                if(notification.pendingIntent != null){
                    notification.pendingIntent.send()
//                    onClickItem(notification)
                }
                if(notification.isClearable){

                }
                arrowIv.setImageResource(R.drawable.arrow_down)
                notificationActionContainer.show()
            }

            if(notification.keyMap.size > 0|| notification.bigText.toString().isNotEmpty() && notification.bigText != notification.text.toString() || notification.actions != null ){
                arrowIv.show()
                arrowIv.setImageResource(R.drawable.arrow_down)
            }else{

            }

        }

    }

    fun addTitleAndTextSubItems(linearLayout: LinearLayout) {
        notification?.let { notification ->
            for (str in notification.keyMap.keys) {
                val newNotify = notification.keyMap[str]
                if (notification.keyMap.size != 1) {
                    newNotify?.let {
                        linearLayout.addView(
                            getTitleAndTextViewForSubItems(
                                newNotify.title, newNotify.text
                            )
                        )
                    }
                } else if (notification.text != newNotify?.text) {
                    newNotify?.let {
                        linearLayout.addView(
                            getTitleAndTextViewForSubItems(
                                newNotify.title, newNotify.text
                            )
                        )
                    }
                }
            }

        }
    }

    private fun getTitleAndTextViewForSubItems(
        charSequence: CharSequence, charSequence2: CharSequence
    ): View {
        val linearLayout = LayoutInflater.from(context)
            .inflate(R.layout.notification_min_row_item, null as ViewGroup?) as LinearLayout
        val convertDpToPixel = Utils.convertDpToPixel(5.0f, context)
        if (charSequence.toString().isEmpty()) {
            linearLayout.findViewById<View>(R.id.item_text_a).visibility = GONE
            linearLayout.findViewById<View>(R.id.item_text_b)
                .setPadding(0, convertDpToPixel.toInt(), convertDpToPixel.toInt(),
                    convertDpToPixel.toInt()
                )
        } else {
            (linearLayout.findViewById<View>(R.id.item_text_a) as TextView).text =
                charSequence.toString()
        }
        if (charSequence2.toString().isEmpty()) {
            linearLayout.findViewById<View>(R.id.item_text_b).visibility = GONE
        } else {
            (linearLayout.findViewById<View>(R.id.item_text_b) as TextView).text =
                charSequence2.toString()
        }
        return linearLayout
    }
}