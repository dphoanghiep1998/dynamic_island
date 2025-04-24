package com.neko.hiepdph.dynamicislandvip.common.notification

import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Utils
import com.neko.hiepdph.dynamicislandvip.common.Utils.convertDpToPixel
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.getFormattedTime
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutCallItemBinding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutListItemsBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Objects


class CustomNotificationAdapter(
    val context: MyAccessService,
    val notifications: ArrayList<Notification>,
    val onItemClicked: (Notification, i: Int?) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mediaViewHolder: ViewHolder? = null

    fun setListNotification(listNotification: ArrayList<Notification>) {
        this.listNotification = listNotification
        notifyDataSetChanged()
    }

    private var listNotification = arrayListOf<Notification>()
    private var acceptIndex: Int = 1
    private var declineIndex: Int = 0

    inner class NotificationCallViewHolder(val binding: LayoutCallItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun assign(notification: Notification) {
            binding.tvAppTitle.text = notification.app_name
            binding.tvText.text = notification.text
            if (notification.senderIcon != null) {
                binding.iconSender.setImageBitmap(notification.senderIcon)
            }

            binding.root.clickWithDebounce {
                try {
                    if (notification.pendingIntent != null) {
                        notification.pendingIntent.send()
                        onItemClicked.invoke(notification, null)
                    }
                    if (notification.isClearable) {
                        listNotification.removeAt(adapterPosition)
                        notifyDataSetChanged()
                    }
                    context.closeFullIsLandNotification()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            if (notification.actions != null && notification.actions.isNotEmpty()) {
                addViewToCallActionContainer(notification, binding)
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
                    ) || context.isCallPkgFound(notification.pack)
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
    }

    inner class ViewHolder(val binding: LayoutListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            mediaViewHolder = this

            binding.root.setOnLongClickListener {
                return@setOnLongClickListener false
            }
            if (notification.picture == null || notification.keyMap.size != 0) {
                binding.notificationPicture.setImageBitmap(null as Bitmap?)
            } else {
                binding.notificationPicture.setImageBitmap(notification.picture)
            }
            binding.tvAppTitle.text = notification.app_name
            binding.tvAppTitle.tag = java.lang.Boolean.valueOf(notification.isClearable)
            binding.tvText.text = notification.extraTitle
            binding.subTvText.text = notification.text.toString()
            binding.groupMessageParent.removeAllViews()
            addTitleAndTextSubItems(
                notification, binding.groupMessageParent
            )
            binding.notificationActionContainer.visibility = View.GONE
            binding.notificationMaterialReplyContainer.visibility = View.GONE
            if (notification.icon != null) {
                binding.ivAppIcon.visibility = View.VISIBLE
                binding.ivAppIcon.setImageBitmap(notification.icon)
            }
            if (notification.senderIcon != null) {
                binding.civSenderIcon2.visibility = View.INVISIBLE
                binding.civSenderIcon.visibility = View.VISIBLE
                binding.civSenderIcon.setImageBitmap(notification.senderIcon)
                binding.civSenderIcon.colorFilter = null
            } else {
                binding.civSenderIcon2.setImageBitmap(notification.icon)
                binding.civSenderIcon2.setColorFilter(-1)
                binding.civSenderIcon2.visibility = View.VISIBLE
                binding.civSenderIcon.visibility = View.INVISIBLE
                binding.ivAppIcon.visibility = View.INVISIBLE
            }
            if (notification.progressMax > 0) {
                if (notification.showChronometer) {
                    binding.chronometer.visibility = View.VISIBLE
                    binding.chronometer.start()
                } else {
                    binding.chronometer.visibility = View.GONE
                    binding.chronometer.setBase(SystemClock.elapsedRealtime())
                    binding.chronometer.stop()
                }
                binding.subProgressbar.visibility = View.VISIBLE
                binding.subProgressbar.setMax(notification.progressMax)
                binding.subProgressbar.progress = notification.progress
                binding.subProgressbar.isIndeterminate = notification.progressIndeterminate
            } else {
                binding.subProgressbar.visibility = View.GONE
                binding.chronometer.visibility = View.GONE
                binding.chronometer.setBase(SystemClock.elapsedRealtime())
                binding.chronometer.stop()
            }
            binding.root.setOnClickListener {
                try {
                    if (notification.pendingIntent != null) {
                        notification.pendingIntent.send()
                        onItemClicked(
                            notification, adapterPosition
                        )
                    }
                    if (notification.isClearable) {
//                        arrayList.remove(this@ViewHolder.holder.getAbsoluteAdapterPosition())
                        notifyDataSetChanged()
                    }
                    context.closeFullIsLandNotification()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
//                binding.arrowIv.setImageResource(R.drawable.arrow_down)
                binding.notificationActionContainer.visibility = View.GONE
            }
            if ((notification.keyMap.size > 0) || ((notification.bigText.toString()
                    .isNotEmpty() && (notification.bigText.toString() != notification.text.toString())) || (notification.actions != null))
            ) {

            }
            if (notification.actions == null || notification.actions.size <= 0) {
                binding.mediaDurationText.visibility = View.GONE
                binding.mediaPosText.visibility = View.GONE
            } else {
                binding.notificationActionContainer.visibility = View.VISIBLE
                binding.notificationActionContainer.removeAllViews()
                if(context.config.notificationShowAction){
                    addViewToActionContainer(
                        notification, binding.notificationActionContainer
                    )
                }

                if (notification.template.contains("MediaStyle")) {
                    binding.mediaDurationText.visibility = View.VISIBLE
                    binding.mediaPosText.visibility = View.VISIBLE
                    val durationTime = getFormattedTime(notification.duration)
                    val positionTime = getFormattedTime(notification.position)
                    binding.mediaDurationText.text = durationTime
                    binding.mediaPosText.text = positionTime
                } else {
                    binding.mediaDurationText.visibility = View.GONE
                    binding.mediaPosText.visibility = View.GONE
                }
            }

            if (notification.bigText.toString().isNotEmpty()) {
                binding.subTvText.text = notification.bigText.toString()
            }
            binding.notificationActionContainer.visibility = View.VISIBLE
            binding.notificationActionContainer.removeAllViews()
            binding.groupMessageParent.removeAllViews()
            if (notification.actions != null) {
                if(context.config.notificationShowAction){
                    addViewToActionContainer(
                        notification, binding.notificationActionContainer
                    )
                }

                binding.notificationActionContainer.setPadding(
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
            binding.notificationActionContainer.setPadding(0, 0, 0, 0)
//                }
            binding.root.setOnLongClickListener { false }

        }
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

    fun closeFull(notification: Notification, view: View?) {
        context.closeFullIsLandNotification()
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
                    Log.d("TAG", "title:qưeqweqwe " + notification2.title)
                    Log.d("TAG", "text: qưeqweqweqwe" + notification2.text)
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
        Log.d("TAG", "getTitleAndTextViewForSubItems: " + charSequence)
        Log.d("TAG", "getTitleAndTextViewForSubItems: " + charSequence2)
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
        context.closeFullIsLandNotification()
    }

    fun startActionDeclinePendingIntent(notification: Notification, view: View?) {
        if (notification.actions[declineIndex].pendingIntent != null) {
            try {
                notification.actions[declineIndex].pendingIntent.send()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        context.closeFullIsLandNotification()
    }

    fun startActionAcceptPendingIntent(notification: Notification, view: View?) {
        if (notification.actions[acceptIndex].pendingIntent != null) {
            try {
                notification.actions[acceptIndex].pendingIntent.send()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        context.closeFullIsLandNotification()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 2) {
            val binding =
                LayoutCallItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NotificationCallViewHolder(binding)
        }
        val binding =
            LayoutListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listNotification.size
    }

    override fun getItemViewType(position: Int): Int {
        if (!notifications[position].useIphoneCallDesign || notifications[position].category != NotificationCompat.CATEGORY_CALL || !notifications[position].isOngoing) {
            return super.getItemViewType(position)
        }
        return 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notification = listNotification[position]
        if (getItemViewType(position) != 2) {
            with(holder as ViewHolder) {
                bind(notification)
            }
        } else {
            with(holder as NotificationCallViewHolder) {
                assign(notification)
            }
        }
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
                context.closeFullIsLandNotification()

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (notification.category.contains(NotificationCompat.CATEGORY_CALL)) {
            context.closeFullIsLandNotification()
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
        mediaViewHolder?.binding?.apply {
            mediaDurationText.text = formattedTime
            mediaPosText.text = formattedTime2
            subProgressbar.progress = notification.progress
            subProgressbar.visibility = View.VISIBLE
            subProgressbar.setMax(notification.progressMax)
        }

    }


}