package com.neko.hiepdph.dynamicislandvip.common.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutCallItemBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService


class CustomNotificationAdapter(
    val context: MyAccessService,
    val notifications: ArrayList<Notification>,
    val onItemClicked: (Notification, i: Int?) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            notification: Notification,
            binding: LayoutCallItemBinding
        ) {
            var isCancelVisible: Boolean
            var isAcceptVisible: Boolean

            if (notification.actions != null) {
                var shouldShowCancel = true

                if (notification.pack.equals(
                        "com.skype.raider",
                        ignoreCase = true
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
                                    notification,
                                    null
                                )
                            }
                            isCancelVisible = true
                        }
                        if (i == 1) {
                            binding.actionAccept.visibility =
                                View.VISIBLE
                            binding.actionAccept.clickWithDebounce {
                                startActionAcceptPendingIntent(
                                    notification,
                                    null
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
                        startActionFirstPendingIntent(notification,null)
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
            val binding = LayoutCallItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return NotificationCallViewHolder(binding)
        }
        val binding = LayoutCallItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationCallViewHolder(binding)
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
//            with(holder as ViewHolder) {
//                bind(notification)
//            }
        } else {
            with(holder as NotificationCallViewHolder) {
                assign(notification)
            }
        }
    }

    fun updateMediaProgress() {

    }


}