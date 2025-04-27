package com.neko.hiepdph.dynamicislandvip.common.customview

import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.Utils.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.getFormattedTime
import com.neko.hiepdph.dynamicislandvip.common.notification.ActionParsable
import com.neko.hiepdph.dynamicislandvip.common.notification.Notification
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutCallItemBinding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutListItemsBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Objects

class ViewDynamicIslandBig @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val callBinding: LayoutCallItemBinding
    private val itemBinding: LayoutListItemsBinding
    private var currentNotification: Notification? = null
    private var acceptIndex: Int = 1
    private var declineIndex: Int = 0
    private var listener: IClickFullListener? = null
    private var accessService: MyAccessService? = null

    init {
        itemBinding = LayoutListItemsBinding.inflate(LayoutInflater.from(context), this, false)
        callBinding = LayoutCallItemBinding.inflate(LayoutInflater.from(context), this, false)
    }

    fun setAccessService(accessService: MyAccessService) {
        this.accessService = accessService
    }

    fun setNotification(lst: Notification?) {
        currentNotification = lst

        if (currentNotification != null) {
            currentNotification?.let {
                if (it.category != NotificationCompat.CATEGORY_CALL || !it.isOngoing) {
                    removeAllViews()
                    addView(itemBinding.root)
                    bind(it)
                } else {
                    removeAllViews()
                    addView(callBinding.root)
                    assign(it)

                }
            }

        } else {
            currentNotification = null
            reset()

        }

    }

    fun setListener(mListener: IClickFullListener) {
        listener = mListener
    }

    fun reset() {
        closeFull(null,null)
    }

    fun assign(notification: Notification) {
        callBinding.tvAppTitle.text = notification.app_name
        callBinding.tvText.text = notification.text
        if (notification.senderIcon != null) {
            callBinding.iconSender.setImageBitmap(notification.senderIcon)
        }

        callBinding.root.clickWithDebounce {
            try {
                if (notification.pendingIntent != null) {
                    notification.pendingIntent.send()
                    listener?.onClick(notification, null)
                }
                if (notification.isClearable) {
                    currentNotification = null
                }
                accessService?.closeFullIsLandNotification()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (notification.actions != null && notification.actions.isNotEmpty()) {
            addViewToCallActionContainer(notification, callBinding)
        }
    }

    fun addViewToCallActionContainer(
        notification: Notification, binding: LayoutCallItemBinding
    ) {
        var isCancelVisible: Boolean
        var isAcceptVisible: Boolean

        if (notification.actions != null) {
            var shouldShowCancel = true

            if (notification.pack.contains(
                    "com.skype.raider", ignoreCase = true
                ) || accessService?.isCallPkgFound(notification.pack) == true
            ) {
                declineIndex = 1
                acceptIndex = 0
            } else {
                declineIndex = 0
                acceptIndex = 1
            }

            if (notification.actions.size == 2) {
                isCancelVisible = false
                isAcceptVisible = false

                for (i in notification.actions.indices) {
                    if (i == 0) {
                        binding.actionCancel.visibility = View.VISIBLE
                        binding.actionCancel.clickWithDebounce {
                            startActionDeclinePendingIntent(
                                notification, null
                            )
                        }
                        isCancelVisible = true
                    }
                    if (i == 1) {
                        binding.actionAccept.visibility = View.VISIBLE
                        binding.actionAccept.clickWithDebounce {
                            startActionAcceptPendingIntent(
                                notification, null
                            )
                        }
                        isAcceptVisible = true
                    }
                }
            } else {
                isCancelVisible = false
                isAcceptVisible = false
            }

            if (notification.actions.size == 1) {
                binding.actionCancel.visibility = View.VISIBLE
                binding.actionCancel.clickWithDebounce {
                    startActionFirstPendingIntent(notification, null)
                }
            } else {
                shouldShowCancel = isCancelVisible
            }

            if (!shouldShowCancel) {
                binding.actionCancel.visibility = View.GONE
            }
            if (!isAcceptVisible) {
                binding.actionAccept.visibility = View.GONE
            }
        }
    }

    fun bind(notification: Notification) {
        itemBinding.root.setOnLongClickListener {
            return@setOnLongClickListener false
        }
        if (notification.picture == null || notification.keyMap.size != 0) {
            itemBinding.notificationPicture.setImageBitmap(null as Bitmap?)
        } else {
            itemBinding.notificationPicture.setImageBitmap(notification.picture)
        }
        itemBinding.tvAppTitle.text = notification.app_name
        itemBinding.tvAppTitle.tag = java.lang.Boolean.valueOf(notification.isClearable)
        itemBinding.tvText.text = notification.extraTitle
        itemBinding.subTvText.text = notification.text.toString()
        itemBinding.groupMessageParent.removeAllViews()
        addTitleAndTextSubItems(
            notification, itemBinding.groupMessageParent
        )
        itemBinding.notificationActionContainer.visibility = View.GONE
        itemBinding.notificationMaterialReplyContainer.visibility = View.GONE
        if (notification.icon != null) {
            itemBinding.ivAppIcon.visibility = View.VISIBLE
            itemBinding.ivAppIcon.setImageBitmap(notification.icon)
        }
        if (notification.senderIcon != null) {
            itemBinding.civSenderIcon2.visibility = View.INVISIBLE
            itemBinding.civSenderIcon.visibility = View.VISIBLE
            itemBinding.civSenderIcon.setImageBitmap(notification.senderIcon)
            itemBinding.civSenderIcon.colorFilter = null
        } else {
            itemBinding.civSenderIcon2.setImageBitmap(notification.icon)
            itemBinding.civSenderIcon2.visibility = View.VISIBLE
            itemBinding.civSenderIcon.visibility = View.INVISIBLE
            itemBinding.ivAppIcon.visibility = View.INVISIBLE
        }
        if (notification.progressMax > 0) {
            if (notification.showChronometer) {
                itemBinding.chronometer.visibility = View.VISIBLE
                itemBinding.chronometer.start()
            } else {
                itemBinding.chronometer.visibility = View.GONE
                itemBinding.chronometer.setBase(SystemClock.elapsedRealtime())
                itemBinding.chronometer.stop()
            }
            itemBinding.subProgressbar.visibility = View.VISIBLE
            itemBinding.subProgressbar.setMax(notification.progressMax)
            itemBinding.subProgressbar.progress = notification.progress
            itemBinding.subProgressbar.isIndeterminate = notification.progressIndeterminate
        } else {
            itemBinding.subProgressbar.visibility = View.GONE
            itemBinding.chronometer.visibility = View.GONE
            itemBinding.chronometer.setBase(SystemClock.elapsedRealtime())
            itemBinding.chronometer.stop()
        }
        itemBinding.root.setOnClickListener {
            try {
                if (notification.pendingIntent != null) {
                    notification.pendingIntent.send()
                    listener?.onClick(
                        notification
                    )
                }
                if (notification.isClearable) {
//                        arrayList.remove(this@ViewHolder.holder.getAbsoluteAdapterPosition())

                }
                accessService?.closeFullIsLandNotification()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
//                binding.arrowIv.setImageResource(R.drawable.arrow_down)
            itemBinding.notificationActionContainer.visibility = View.GONE
        }
        if ((notification.keyMap.size > 0) || ((notification.bigText.toString()
                .isNotEmpty() && (notification.bigText.toString() != notification.text.toString())) || (notification.actions != null))
        ) {

        }
        if (notification.actions == null || notification.actions.size <= 0) {
            itemBinding.mediaDurationText.visibility = View.GONE
            itemBinding.mediaPosText.visibility = View.GONE
        } else {
            itemBinding.notificationActionContainer.visibility = View.VISIBLE
            itemBinding.notificationActionContainer.removeAllViews()
            if (context.config.notificationShowAction) {
                addViewToActionContainer(
                    notification, itemBinding.notificationActionContainer
                )
            }

            if (notification.template.contains("MediaStyle")) {
                itemBinding.mediaDurationText.visibility = View.VISIBLE
                itemBinding.mediaPosText.visibility = View.VISIBLE
                val durationTime = getFormattedTime(notification.duration)
                val positionTime = getFormattedTime(notification.position)
                itemBinding.mediaDurationText.text = durationTime
                itemBinding.mediaPosText.text = positionTime
            } else {
                itemBinding.mediaDurationText.visibility = View.GONE
                itemBinding.mediaPosText.visibility = View.GONE
            }
        }

        if (notification.bigText.toString().isNotEmpty()) {
            itemBinding.subTvText.text = notification.bigText.toString()
        }
        itemBinding.notificationActionContainer.visibility = View.VISIBLE
        itemBinding.notificationActionContainer.removeAllViews()
        itemBinding.groupMessageParent.removeAllViews()
        if (notification.actions != null) {
            if (context.config.notificationShowAction) {
                addViewToActionContainer(
                    notification, itemBinding.notificationActionContainer
                )
            }

            itemBinding.notificationActionContainer.setPadding(
                0, convertDpToPixel(
                    10.0f, context
                ).toInt(), 0, convertDpToPixel(
                    5.0f, context
                ).toInt()
            )
        }
//            addSubItemsToGroupContainer(
//                notification, binding.groupMessageParent
//            )
        itemBinding.notificationActionContainer.setPadding(0, 0, 0, 0)
//                }
        itemBinding.root.setOnLongClickListener { false }

    }

    fun addViewToActionContainer(notification: Notification, linearLayout: LinearLayout) {
        if (notification.actions != null) {
            for (i in 0 until notification.actions.size) {
                val textView = LayoutInflater.from(context)
                    .inflate(R.layout.notification_action_item, null as ViewGroup?) as TextView

                val imageButton = LayoutInflater.from(context)
                    .inflate(R.layout.notification_action_button, null as ViewGroup?) as ImageView


                var drawable = ResourcesCompat.getDrawable(
                    context.resources, R.drawable.ic_bright, null
                )
                try {
                    drawable = ContextCompat.getDrawable(
                        context.createPackageContext(
                            notification.pack, 0
                        ), notification.actions[i].actionIcon
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                textView.text = notification.actions[i].charSequence

                textView.setTextColor(notification.color)
                if (notification.template.contains("MediaStyle")) {
//                    drawable?.setTint(notification.color)
                    imageButton.setImageDrawable(drawable)
                    val param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                    )
                    imageButton.layoutParams = param
                    imageButton.setOnClickListener {
                        m86x9ccd14b3(
                            notification, i, linearLayout
                        )
                    }
                    linearLayout.addView(imageButton)

                    linearLayout.gravity = Gravity.CENTER
                } else {
                    if (i > 0) {
                        textView.setPadding(50, 5, 5, 5)
                    }
                    linearLayout.addView(textView)
                    textView.setOnClickListener {
                        m86x9ccd14b3(
                            notification, i, linearLayout
                        )
                    }
                    linearLayout.gravity = Gravity.CENTER_VERTICAL or Gravity.END

                }

            }
            if (notification.isClearable) {
                val imageView = ImageView(context)
                val layoutParams = LinearLayout.LayoutParams(-2, -2)
                imageView.apply {
                    this.layoutParams = layoutParams
                    setImageResource(R.drawable.ic_close)
                    setColorFilter(notification.color)
                    setPadding(50, 5, 5, 5)
                    setOnClickListener {
                        closeFull(
                            notification, null
                        )
                    }
                }
                linearLayout.addView(imageView)
            }
        }
    }

    fun closeFull(notification: Notification?, view: View?) {
        accessService?.closeFullIsLandNotification()
        NotificationListener.instance.cancelNotificationById("")
    }

    fun addSubItemsToGroupContainer(notification: Notification, linearLayout: LinearLayout) {
        val notification2 = notification
        val linearLayout2 = linearLayout
        for (next in notification2.keyMap.keys) {
            if (notification2.keyMap[next] != null) {
                val inflate: View = LayoutInflater.from(context)
                    .inflate(R.layout.notification_list_sub_item, null as ViewGroup?)
                val textView = inflate.findViewById<View>(R.id.tv_app_title) as TextView
                val textView2 = inflate.findViewById<View>(R.id.tv_text) as TextView
                val textView3 = inflate.findViewById<View>(R.id.sub_tv_text) as TextView
                val circleImageView =
                    inflate.findViewById<View>(R.id.civ_senderIcon) as CircleImageView
                val linearLayout3 =
                    inflate.findViewById<View>(R.id.notification_action_container) as LinearLayout
                val relativeLayout =
                    inflate.findViewById<View>(R.id.notification_material_reply_container) as RelativeLayout
                val imageView2 = inflate.findViewById<View>(R.id.notification_picture) as ImageView
                if ((Objects.requireNonNull(notification2.keyMap[next]) as Notification).senderIcon != null) {
                    circleImageView.visibility = View.VISIBLE
                    circleImageView.setImageBitmap((Objects.requireNonNull(notification2.keyMap[next]) as Notification).senderIcon)
                } else {
                    circleImageView.setImageResource(0)
                    circleImageView.visibility = View.GONE
                }
                if ((Objects.requireNonNull(notification2.keyMap[next]) as Notification).picture != null) {
                    imageView2.setImageBitmap((Objects.requireNonNull(notification2.keyMap[next]) as Notification).picture)
                } else {
                    imageView2.setImageBitmap(null as Bitmap?)
                }
                if ((Objects.requireNonNull(notification2.keyMap[next]) as Notification).picture != null) {
                    (inflate.findViewById<View>(R.id.notification_picture) as ImageView).setImageBitmap(
                        (Objects.requireNonNull(
                            notification2.keyMap[next]
                        ) as Notification).picture
                    )
                } else {
                    (inflate.findViewById<View>(R.id.notification_picture) as ImageView).setImageBitmap(
                        null as Bitmap?
                    )
                }
                textView.text = Utils.getFormattedDate(
                    (Objects.requireNonNull(
                        notification2.keyMap[next]
                    ) as Notification).postTime
                )
                textView2.text =
                    (Objects.requireNonNull(notification2.keyMap[next]) as Notification).title
                textView3.text =
                    (Objects.requireNonNull(notification2.keyMap[next]) as Notification).text.toString()
                if ((Objects.requireNonNull(notification2.keyMap[next]) as Notification).actions != null || (Objects.requireNonNull(
                        notification2.keyMap[next]
                    ) as Notification).bigText.toString().isNotEmpty()
                ) {
                } else {
                }
                val linearLayout4 = linearLayout3
                val relativeLayout2 = relativeLayout
                val notification3 = notification
                val str = next

                if ((Objects.requireNonNull(notification3.keyMap[str]) as Notification).bigText.toString()
                        .isNotEmpty()
                ) {
                    textView3.text =
                        (Objects.requireNonNull(notification3.keyMap[str]) as Notification).bigText.toString()
                }
                linearLayout4.visibility = View.VISIBLE
                linearLayout4.removeAllViews()
                if ((Objects.requireNonNull(notification3.keyMap[str]) as Notification).actions != null) {
                    Handler().post {
                        linearLayout4.setPadding(
                            0, convertDpToPixel(
                                10.0f, context
                            ).toInt(), 0, convertDpToPixel(
                                5.0f, context
                            ).toInt()
                        )
                        notification3.keyMap[str]?.let {
                            addViewToActionContainer(
                                it, linearLayout4
                            )
                        }
                    }
                } else {
                    linearLayout4.setPadding(0, 0, 0, 0)
                }
                inflate.setOnClickListener {
                    addSubItemsToGroupContainer(
                        notification2,
                        next,
                        linearLayout2,
                        inflate,
                    )
                }
                linearLayout2.addView(inflate)
            }
        }
    }

    fun addSubItemsToGroupContainer(
        notification: Notification,
        str: String?,
        linearLayout: LinearLayout,
        view: View?,
    ) {
        try {
            if ((Objects.requireNonNull(notification.keyMap[str]) as Notification).pendingIntent != null) {
                (Objects.requireNonNull(notification.keyMap[str]) as Notification).pendingIntent.send()
                linearLayout.removeView(view)
                notification.keyMap.remove(str)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun addTitleAndTextSubItems(notification: Notification, linearLayout: LinearLayout) {
        for (str in notification.keyMap.keys) {
            val notification2 = notification.keyMap[str]
            if (notification.keyMap.size != 1) {
                notification2?.let {

                    linearLayout.addView(
                        getTitleAndTextViewForSubItems(
                            notification2.title, notification2.text
                        )
                    )
                }

            } else if (!notification.text.equals(notification2?.text)) {
                notification2?.let {
                    linearLayout.addView(
                        getTitleAndTextViewForSubItems(
                            notification2.title, notification2.text
                        )
                    )
                }
            }
        }
    }

    private fun getTitleAndTextViewForSubItems(
        charSequence: CharSequence, charSequence2: CharSequence
    ): View {

        val linearLayout = LayoutInflater.from(context)
            .inflate(R.layout.notification_min_row_item, null as ViewGroup?) as LinearLayout
        val convertDpToPixel = convertDpToPixel(5.0f, context)
        if (charSequence.toString().isEmpty()) {
            linearLayout.findViewById<View>(R.id.item_text_a).visibility = View.GONE
            linearLayout.findViewById<View>(R.id.item_text_b).setPadding(
                0, convertDpToPixel.toInt(), convertDpToPixel.toInt(), convertDpToPixel.toInt()
            )
        } else {
            (linearLayout.findViewById<View>(R.id.item_text_a) as TextView).text =
                charSequence.toString()
        }
        if (charSequence2.toString().isEmpty()) {
            linearLayout.findViewById<View>(R.id.item_text_b).visibility = View.GONE
        } else {
            (linearLayout.findViewById<View>(R.id.item_text_b) as TextView).text =
                charSequence2.toString()
        }
        return linearLayout
    }

    fun startActionFirstPendingIntent(notification: Notification, view: View?) {
        if (notification.actions[0].pendingIntent != null) {
            try {
                notification.actions[0].pendingIntent.send()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        accessService?.closeFullIsLandNotification()
    }

    fun startActionDeclinePendingIntent(notification: Notification, view: View?) {
        if (notification.actions[declineIndex].pendingIntent != null) {
            try {
                notification.actions[declineIndex].pendingIntent.send()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        accessService?.closeFullIsLandNotification()
    }

    fun startActionAcceptPendingIntent(notification: Notification, view: View?) {
        if (notification.actions[acceptIndex].pendingIntent != null) {
            try {
                notification.actions[acceptIndex].pendingIntent.send()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        accessService?.closeFullIsLandNotification()
    }

    fun m86x9ccd14b3(notification: Notification, i: Int, linearLayout: LinearLayout) {
        if (notification.actions[i].remoteInputs != null) {
            showReplyBox(linearLayout, notification.actions, i)
            return
        }
        try {
            if (notification.actions[i].pendingIntent != null) {
                notification.actions[i].pendingIntent.send()
            }
            if (!notification.template.contains("MediaStyle")) {
//                linearLayout.visibility = View.GONE
                accessService?.closeFullIsLandNotification()

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (notification.category.contains(NotificationCompat.CATEGORY_CALL)) {
            accessService?.closeFullIsLandNotification()
        }
    }

    private fun showReplyBox(view: View, arrayList: java.util.ArrayList<ActionParsable>, i: Int) {
        val childAt = (view.parent as LinearLayout).getChildAt(0)
        if (childAt != null) {
            childAt.visibility = View.VISIBLE
            childAt.findViewById<View>(R.id.iv_send_reply).visibility = View.VISIBLE
            childAt.findViewById<View>(R.id.iv_send_reply).setOnClickListener { view ->
                sendRemoteInput(
                    arrayList[i].pendingIntent,
                    arrayList[i].remoteInputs,
                    arrayList[i].remoteInputs[0],
                    (childAt.findViewById<View>(R.id.ed_reply) as EditText).text.toString()
                )
                (childAt.findViewById<View>(R.id.ed_reply) as EditText).setText(
                    ""
                )
                childAt.visibility = View.GONE
                view.visibility = View.GONE
            }
            val arrayList2 = arrayList
            val i2 = i
            val view2 = view
            (childAt.findViewById<View>(R.id.ed_reply) as EditText).setOnEditorActionListener(
                TextView.OnEditorActionListener { textView, i, keyEvent ->
                    if (i != 4) {
                        return@OnEditorActionListener false
                    }
                    sendRemoteInput(
                        arrayList2[i2].pendingIntent,
                        arrayList2[i2].remoteInputs,
                        arrayList2[i2].remoteInputs[0],
                        (childAt.findViewById<View>(R.id.ed_reply) as EditText).text.toString()
                    )
                    (childAt.findViewById<View>(R.id.ed_reply) as EditText).setText("")
                    childAt.visibility = View.GONE
                    view2.visibility = View.GONE
                    true
                })
        }
    }

    fun sendRemoteInput(
        pendingIntent: PendingIntent,
        remoteInputArr: Array<RemoteInput?>?,
        remoteInput: RemoteInput,
        str: String?
    ) {
        val bundle = Bundle()
        bundle.putString(remoteInput.resultKey, str)
        val addFlags = Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        RemoteInput.addResultsToIntent(remoteInputArr, addFlags, bundle)
        if (Build.VERSION.SDK_INT >= 28) {
            if (remoteInput.allowFreeFormInput) {
                RemoteInput.setResultsSource(addFlags, RemoteInput.SOURCE_FREE_FORM_INPUT)
            } else {
                RemoteInput.setResultsSource(addFlags, RemoteInput.SOURCE_CHOICE)
            }
        }
        try {
            pendingIntent.send(context, 0, addFlags)
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }

    fun updateMediaProgress(notification: Notification) {
        val formattedTime = getFormattedTime(notification.duration)
        val formattedTime2 = getFormattedTime(notification.position)
        itemBinding?.apply {
            mediaDurationText.text = formattedTime
            mediaPosText.text = formattedTime2
            subProgressbar.progress = notification.progress
            subProgressbar.visibility = View.VISIBLE
            subProgressbar.setMax(notification.progressMax)
        }

    }

    interface IClickFullListener {
        fun onClick(notification: Notification, index: Int? = null)
    }
}