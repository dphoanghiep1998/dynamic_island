package com.neko.hiepdph.dynamicislandvip.common.notification

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.NotificationCallItemsBinding
import com.neko.hiepdph.dynamicislandvip.databinding.NotificationListItemsBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import java.lang.Boolean
import kotlin.Int
import kotlin.Unit
import kotlin.apply
import kotlin.with

class CustomNotificationAdapter(
    val context: MyAccessService,
    val notifications: ArrayList<Notification>,
    val onItemClicked: (Notification, i: Int?) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listNotification = arrayListOf<Notification>()
    private var acceptIndex: Int = 1
    private var declineIndex: Int = 0

    inner class NotificationCallViewHolder(val binding: NotificationCallItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {

        }
    }

    inner class ViewHolder(val binding: NotificationListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.root.setOnLongClickListener(null)
            binding.root.isLongClickable = false

            binding.tvAppTitle.text = notification.app_name.toString()
            binding.tvAppTitle.tag = Boolean.valueOf(notification.isClearable)
            binding.tvText.text = notification.tv_title.toString()
            binding.subTvText.text = notification.tv_text.toString()
            binding.groupMessageParent.removeAllViews()

            if (notification.picture == null && notification.keyMap.size == 0) {
                binding.notificationPicture.setImageBitmap(null)
            } else {
                binding.notificationPicture.setImageBitmap(notification.picture)
            }

            addTitleAndTextSubItem(notification, binding.groupMessageParent)
            binding.notificationActionContainer.hide()
            binding.notificationMaterialReplyContainer.hide()
            if (notification.senderIcon != null) {
                binding.civSenderIcon.visibility = View.INVISIBLE
                binding.civSenderIcon.show()
                binding.civSenderIcon.setImageBitmap(notification.senderIcon)
                binding.civSenderIcon.colorFilter = null
            } else {
                binding.civSenderIcon2.setImageBitmap(notification.icon)
                binding.civSenderIcon2.setColorFilter(-1)
                binding.civSenderIcon2.show()
                binding.civSenderIcon.visibility = View.INVISIBLE
                binding.ivAppIcon.visibility = View.INVISIBLE
            }

            if (notification.progressMax > 0) {
                if (notification.showChronometer) {
                    binding.chronometer.apply {
                        show()
                        start()
                    }
                } else {
                    binding.chronometer.apply {
                        hide()
                        base = SystemClock.elapsedRealtime()
                        stop()
                    }
                }
                binding.subProgressbar.apply {
                    show()
                    max = notification.progressMax
                    progress = notification.progress
                    isIndeterminate = notification.progressIndeterminate
                }


            } else {
                binding.subProgressbar.visibility = View.INVISIBLE
                binding.chronometer.visibility = View.INVISIBLE
                binding.chronometer.setBase(SystemClock.elapsedRealtime())
                binding.chronometer.stop()
            }

            binding.root.clickWithDebounce {
                try {
                    if(notification.pendingIntent != null){
                        notification.pendingIntent.send()
                        onItemClicked.invoke(notification,null)
                    }
                    if(notification.isClearable){
                        listNotification.removeAt(adapterPosition)
                    }
                }catch (e:Exception){

                }
            }

        }
    }

    fun addViewToCallActionContainer(
        notification: Notification, binding: NotificationListItemsBinding
    ) {
        val actions = notification.actions ?: return

        var cancelVisible = false
        var acceptVisible = false

        // Determine index positions for accept/decline
        if (notification.pack.equals(
                "com.skype.raider", ignoreCase = true
            ) || context.isCallPkgFound(notification.pack)
        ) {
            declineIndex = 1
            acceptIndex = 0
        } else {
            declineIndex = 0
            acceptIndex = 1
        }

        when (actions.size) {
            2 -> {
                binding.linearLayout.findViewById<View>(R.id.action_cancel)?.apply {
                    visibility = View.VISIBLE
                    setOnClickListener { onCancelClicked(notification) }
                    cancelVisible = true
                }

                linearLayout.findViewById<View>(R.id.action_accept)?.apply {
                    visibility = View.VISIBLE
                    setOnClickListener { onAcceptClicked(notification) }
                    acceptVisible = true
                }
            }

            1 -> {
                linearLayout.findViewById<View>(R.id.action_cancel)?.apply {
                    visibility = View.VISIBLE
                    setOnClickListener { onSingleActionClicked(notification) }
                    cancelVisible = true
                }
            }
        }

        if (!cancelVisible) {
            linearLayout.findViewById<View>(R.id.action_cancel)?.visibility = View.GONE
        }
        if (!acceptVisible) {
            linearLayout.findViewById<View>(R.id.action_accept)?.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 2) {
            val binding =
                NotificationCallItemsBinding.inflate(LayoutInflater.from(context), parent, false)
            return NotificationCallViewHolder(binding)
        }
        val binding =
            NotificationListItemsBinding.inflate(LayoutInflater.from(context), parent, false)
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
        if (getItemViewType(position) == 2) {
            with(holder as ViewHolder) {
                bind(notification)
            }
        } else {
            with(holder as NotificationCallViewHolder) {
                bind(notification)
            }
        }
    }


}