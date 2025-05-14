package com.neko.hiepdph.dynamicislandvip.common.notification

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion24
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion28
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion29
import com.neko.hiepdph.dynamicislandvip.common.config
import java.io.ByteArrayOutputStream


class NotificationListener : NotificationListenerService() {
    private var handler: Handler? = null

    companion object {
        lateinit var instance: NotificationListener
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        instance = this
    }

    fun cancelNotificationById(str: String?) {
        try {
            cancelNotification(str)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        handler?.postDelayed({
            sbn?.let { sendNotification(it, true) }
        }, 100)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
//        handler?.postDelayed({
//            sbn?.let { sendNotification(it, true) }
//        }, 100)
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        handler?.postDelayed({
            sbn?.let { sendNotification(it, false) }
        }, 100)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        requestRebind(
            ComponentName(
                this, NotificationListenerService::class.java
            )
        )
    }

    private fun sendNotification(
        sbn: StatusBarNotification, isNotificationAdded: Boolean
    ) {
        var appName: String
        val category: String
        val maxProgress: Int
        val currentProgress: Int
        val isProgressIndeterminate: Boolean
        val showChronometer: Boolean
        val isGroupConversation: Boolean
        val title: String
        val subTitle: String
        val summaryText: String
        val bigText: String
        val tickerText: String?
        var largeIconUrl: String
        val extraInfoText: String
        val notificationTitle: CharSequence
        var extraImageBitmap: Bitmap? = null
        val notificationIntent: Intent

        try {
            appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    sbn.packageName, PackageManager.GET_META_DATA
                )
            ) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            appName = ""
        }


        if (sbn.notification != null) {

            val packageName = sbn.packageName
            if(packageName == "com.android.systemui") {
                return
            }
            category = if ((sbn.notification.category != null)) sbn.notification.category else ""
            if (!config.directEnable && category == "navigation") {
                return
            }
            tickerText =
                if ((sbn.notification.tickerText != null)) sbn.notification.tickerText.toString() else null
            val drawableToBmp: Bitmap? = if ((sbn.notification.getLargeIcon() == null)) null
            else drawableToBmp(
                applicationContext,
                sbn.notification.getLargeIcon().loadDrawable(applicationContext),
                50
            )


            if (sbn.notification.extras != null) {
                val extras = sbn.notification.extras

                // Extract necessary data from notification extras
                extraInfoText = extras.getString(NotificationCompat.EXTRA_INFO_TEXT, "")
                notificationTitle = extras.getCharSequence(NotificationCompat.EXTRA_TITLE, "")
                maxProgress = extras.getInt(NotificationCompat.EXTRA_PROGRESS_MAX, 0)
                currentProgress = extras.getInt(NotificationCompat.EXTRA_PROGRESS, 0)
                isProgressIndeterminate = extras.getBoolean(
                    NotificationCompat.EXTRA_PROGRESS_INDETERMINATE, false
                )
                showChronometer = extras.getBoolean(
                    NotificationCompat.EXTRA_SHOW_CHRONOMETER, false
                )

                // Extract additional texts
                summaryText =
                    extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT, "").toString()
                title = extras.getCharSequence(NotificationCompat.EXTRA_TITLE_BIG, "").toString()
                subTitle = extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT, "").toString()
                bigText = extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT, "").toString()
                val titleBig =
                    extras.getCharSequence(NotificationCompat.EXTRA_TITLE_BIG, "").toString()
                val substName = extras.getString("android.substName", "")
                val progressIndeterminate =
                    extras.getBoolean(NotificationCompat.EXTRA_PROGRESS_INDETERMINATE, false)

                val text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT, "").toString()
                val extraTitle = extras.getCharSequence(
                    NotificationCompat.EXTRA_TITLE, ""
                ).toString()
                // Set group conversation details if SDK version supports it
                appName = "$appName . " + extras.getCharSequence(
                    NotificationCompat.EXTRA_CONVERSATION_TITLE, ""
                )
                extraImageBitmap =
                    extras.getParcelable<Parcelable>(NotificationCompat.EXTRA_PICTURE) as Bitmap?

                val subText =
                    extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT, "").toString()
                // Check if the notification is part of a group conversation
                isGroupConversation =
                    extras.getBoolean(NotificationCompat.EXTRA_IS_GROUP_CONVERSATION, false)

                // Create the notification intent and send broadcast
                notificationIntent =
                    Intent("from_notification_service" + applicationContext.packageName)
                notificationIntent.putExtra("isGroupConversation", isGroupConversation)
                notificationIntent.putExtra("isOngoing", sbn.isOngoing)
                notificationIntent.putExtra("tag", sbn.tag)
                notificationIntent.putExtra("category", category)
                val template =
                    if ((extras.containsKey(NotificationCompat.EXTRA_TEMPLATE))) extras.getString(
                        NotificationCompat.EXTRA_TEMPLATE, ""
                    ) else ""
                notificationIntent.putExtra(
                    "template", template
                )
                if (!config.musicEnable && template.contains("MediaStyle")) {
                    return
                }

                notificationIntent.putExtra("group_key", sbn.groupKey)
                notificationIntent.putExtra("key", sbn.key)
                notificationIntent.putExtra("package", packageName)
                notificationIntent.putExtra("ticker", tickerText)
                notificationIntent.putExtra("appName", appName)
                notificationIntent.putExtra("title", title)
                notificationIntent.putExtra("subTitle", subTitle)
                notificationIntent.putExtra("summaryText", summaryText)
                notificationIntent.putExtra("infoText", extraInfoText)
                notificationIntent.putExtra("progressMax", maxProgress)
                notificationIntent.putExtra("progress", currentProgress)
                notificationIntent.putExtra("progressIndeterminate", progressIndeterminate)
                notificationIntent.putExtra("postTime", sbn.notification.`when`)
                notificationIntent.putExtra("text", text)
                notificationIntent.putExtra("isClearable", sbn.isClearable)
                notificationIntent.putExtra("color", sbn.notification.color)
                notificationIntent.putExtra("substName", substName)
                notificationIntent.putExtra("subText", subText)
                notificationIntent.putExtra("titleBig", titleBig)
                notificationIntent.putExtra("contentIntent", sbn.notification.contentIntent)

                notificationIntent.putExtra("progressIndeterminate", isProgressIndeterminate)
                notificationIntent.putExtra("showChronometer", showChronometer)
                notificationIntent.putExtra("bigText", bigText)
                notificationIntent.putExtra("isAdded", isNotificationAdded)
                notificationIntent.putExtra("picture", getByteArrayFromBitmap2(extraImageBitmap))
                notificationIntent.putExtra("extraTitle", extraTitle)

                var bitmap2: Bitmap? = null
                try {
                    var drawable: Drawable? = null
                    if (Build.VERSION.SDK_INT >= 26) {
                        val launcherApps =
                            applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                        for (userHandle in launcherApps.profiles) {
                            for (launcherActivityInfo in launcherApps.getActivityList(
                                null, userHandle
                            )) {
                                val pkg = launcherActivityInfo.componentName.packageName
                                if (pkg == packageName) {
                                    drawable = launcherActivityInfo.getIcon(0)
                                }
                            }
                        }
                    } else {
                        val intent = Intent("android.intent.action.MAIN")
                        intent.addCategory("android.intent.category.LAUNCHER")
                        for (resolveInfo in applicationContext.packageManager.queryIntentActivities(
                            intent, 0
                        )) {
                            val str3 = resolveInfo.activityInfo.packageName
                            if (str3 == packageName) {
                                drawable = resolveInfo.loadIcon(applicationContext.packageManager)
                            }
                        }
                    }

                    if (drawable != null) {
                        try {
                            bitmap2 = drawableToBmp(applicationContext, drawable, 20);
                        } catch (e: Exception) {

                        }
                    }

                    if (bitmap2 == null) {
                        notificationIntent.putExtra(
                            "icon", getByteArrayFromBitmap(
                                drawableToBmp(
                                    null as Context?, ContextCompat.getDrawable(
                                        applicationContext, R.drawable.android_icon
                                    ), 20
                                )
                            )
                        )
                    } else {
                        notificationIntent.putExtra("icon", getByteArrayFromBitmap(bitmap2))
                    }

                    // Add image data (large icon or extra picture)
                    if (drawableToBmp != null) {
                        notificationIntent.putExtra(
                            "largeIcon", getByteArrayFromBitmap(drawableToBmp)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                if (sbn.notification.actions != null && sbn.notification.actions.isNotEmpty()) {
                    notificationIntent.putExtra(
                        "actions", getParsableActions(sbn.notification.actions)
                    )
                }
                Log.d("TAG1", "appName: $appName")
                Log.d("TAG1", "getPackageName: " + sbn.getPackageName())
                Log.d("TAG1", "title: " + title)
                Log.d("TAG1", "titleBig: " + titleBig)
                Log.d("TAG1", "text: " + text)
                Log.d("TAG1", "bigText: " + bigText)
                Log.d("TAG1", "subTitle: " + subTitle)
                Log.d("TAG1", "subText: " + subText)
                Log.d("TAG1", "category: " + category)
                Log.d("TAG1", "summaryText: " + summaryText)
                Log.d("TAG1", "extraInfoText: " + extraInfoText)
                Log.d("TAG1", "chrono: " + showChronometer)
                Log.d("TAG1", "progress: " + currentProgress)
                Log.d("TAG1", "action: " + currentProgress)
                Log.d("TAG1", "extraTitle: " + extraTitle)
                Log.d("TAG1", "key: " + sbn.key)

                // Send the intent as a broadcast
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(notificationIntent)
            }
        }
    }

    private fun getByteArrayFromBitmap2(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun drawableToBmp(context: Context?, drawable: Drawable?, sizeDp: Int): Bitmap? {
        if (drawable == null) return null

        val bitmap: Bitmap = when {
            drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0 -> {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // ARGB_8888 for better quality
            }

            sizeDp <= 0 -> {
                return getCroppedBitmap(drawable)
            }

            else -> {
                val sizePx = convertDpToPixel(sizeDp.toFloat(), context)
                Bitmap.createBitmap(sizePx.toInt(), sizePx.toInt(), Bitmap.Config.ARGB_8888)
            }
        }

        // Draw the drawable onto the bitmap
        Canvas(bitmap).apply {
            drawable.setBounds(0, 0, width, height)
            drawable.draw(this)
        }

        return bitmap
    }

    private fun getCroppedBitmap(drawable: Drawable): Bitmap? {
        return try {
            // Get display metrics
            val displayMetrics = DisplayMetrics()
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
                displayMetrics
            )

            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Create bitmap from drawable
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888 // ARGB_8888 is preferable for quality
            )

            // Draw the drawable onto the canvas
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            // Scale the bitmap based on screen size
            if (bitmap.width <= screenWidth || bitmap.height <= screenHeight) {
                scaleUpImage(bitmap, screenWidth, screenHeight)
            } else {
                scaleDownImage(bitmap, screenWidth, screenHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleDownImage(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        return try {
            val originalWidth = bitmap.width.toFloat()
            val originalHeight = bitmap.height.toFloat()

            if (originalWidth <= maxWidth) return bitmap

            val aspectRatio = originalWidth / originalHeight
            val targetRatio = maxWidth.toFloat() / maxHeight.toFloat()

            val (scaledWidth, scaledHeight) = if (originalHeight <= originalWidth) {
                if (targetRatio < 1.0f) {
                    maxWidth to (maxWidth / aspectRatio).toInt()
                } else {
                    (maxHeight * aspectRatio).toInt() to maxHeight
                }
            } else {
                if (targetRatio > 1.0f) {
                    (maxHeight * aspectRatio).toInt() to maxHeight
                } else {
                    maxWidth to (maxWidth / aspectRatio).toInt()
                }
            }

            Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleUpImage(bitmap: Bitmap?, targetWidth: Int, targetHeight: Int): Bitmap? {
        if (bitmap == null) return null

        return try {
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height

            val croppedBitmap = when {
                originalWidth > targetWidth -> {
                    Bitmap.createBitmap(
                        bitmap,
                        (originalWidth / 2) - (targetWidth / 2),
                        0,
                        targetWidth,
                        originalHeight
                    ).also {
                        bitmap.recycle()
                    }
                }

                originalHeight > targetHeight -> {
                    Bitmap.createBitmap(bitmap, 0, 0, originalWidth, targetHeight).also {
                        bitmap.recycle()
                    }
                }

                else -> bitmap
            }

            when {
                croppedBitmap.width <= targetWidth -> {
                    Bitmap.createScaledBitmap(
                        croppedBitmap,
                        targetWidth,
                        (croppedBitmap.height.toFloat() / croppedBitmap.width.toFloat() * targetWidth).toInt(),
                        true
                    )
                }

                croppedBitmap.height < targetHeight -> {
                    Bitmap.createScaledBitmap(
                        croppedBitmap,
                        (croppedBitmap.width.toFloat() / croppedBitmap.height.toFloat() * targetHeight).toInt(),
                        targetHeight,
                        true
                    )
                }

                else -> null
            }?.also {
                croppedBitmap.recycle()
            }
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    fun convertDpToPixel(dp: Float, context: Context?): Float {
        val resources = context?.resources ?: Resources.getSystem()
        return dp * (resources.displayMetrics.densityDpi / 160f)
    }

    fun getParsableActions(actions: Array<Notification.Action>): ArrayList<ActionParsable> {
        val parsableActions = ArrayList<ActionParsable>(3)
        val actionsCount = actions.size
        val defaultSemanticAction = 0 // Default value for semantic action
        var allowGeneratedReplies = false
        var isContextualAction = false

        for (i in 0 until actionsCount) {
            val action = actions[i]

            allowGeneratedReplies = if (buildMinVersion24()) {
                action.allowGeneratedReplies
            } else {
                try {
                    action.extras?.getBoolean("android.support.allowGeneratedReplies") ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            val semanticAction = if (buildMinVersion28()) {
                action.semanticAction
            } else {
                action.extras?.getInt(
                    "android.support.action.semanticAction", defaultSemanticAction
                ) ?: defaultSemanticAction
            }

            val actionIcon = action.icon
            val actionTitle = action.title
            val actionIntent = action.actionIntent
            val actionExtras = action.extras
            val remoteInputs = action.remoteInputs

            if (buildMinVersion29()) {
                isContextualAction = action.isContextual
                parsableActions.add(
                    ActionParsable(
                        actionTitle,
                        actionIntent,
                        actionExtras,
                        remoteInputs,
                        allowGeneratedReplies,
                        semanticAction,
                        isContextualAction,
                        actionIcon
                    )
                )
            } else {
                val actionParsable = ActionParsable(
                    actionTitle,
                    actionIntent,
                    actionExtras,
                    remoteInputs,
                    allowGeneratedReplies,
                    semanticAction,
                    isContextualAction,
                    actionIcon
                )
                parsableActions.add(actionParsable)
            }
        }

        return parsableActions
    }

}