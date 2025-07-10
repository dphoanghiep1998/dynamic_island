package com.neko.hiepdph.dynamicislandvip.common.notification

import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.LinkedList

object NotificationQueueManager {
    private val queue = LinkedList<StatusBarNotification>()
    private var isBusy = false

    fun setBusy(busy: Boolean) {
//        Log.d("TAG", "setBusy: "+busy)
        isBusy = busy
        if (!busy) {
            processNext()
        }
    }


    fun getBusy(): Boolean {
        return isBusy
    }

    fun addNotification(sbn: StatusBarNotification?, process: (StatusBarNotification?) -> Unit) {
        if (isBusy) {
            sbn?.let { queue.add(it) }
        } else {
            process(sbn)
        }
    }

    private fun processNext() {
        while (!isBusy && queue.isNotEmpty()) {
            val next = queue.poll()
            // Giả sử processNotification là callback xử lý notification
            processNotification(next)
        }
    }

    lateinit var processNotification: (StatusBarNotification?) -> Unit
}