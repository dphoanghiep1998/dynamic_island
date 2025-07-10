package com.neko.hiepdph.dynamicislandvip.common.config

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.common.AppSharePreference
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.toPx
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import java.util.Locale

class MainConfig(private val context: Context) {
    companion object {
        fun newInstance(context: Context) = MainConfig(context)
    }


    var isUserRated: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_USER_RATED, false)
        set(isUserRated) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_USER_RATED, isUserRated)

    var isPassLang: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_PASS_LANG, false)
        set(isPassLang) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_PASS_LANG, isPassLang)

    var controlEnable: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_ENABLED, false)
        set(controlEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_ENABLED, controlEnable)
    var vibration: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_VIBRATION, false)
        set(vibration) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_VIBRATION, vibration)

    var timeShowRate: Long
        get() = AppSharePreference.getInstance(context).getLong(Constant.TIME_SHOW_RATE, 0)
        set(timeShowRate) = AppSharePreference.getInstance(context)
            .saveLong(Constant.TIME_SHOW_RATE, timeShowRate)

    var savedLanguage: String
        get() = AppSharePreference.getInstance(context)
            .getString(Constant.KEY_LANGUAGE, Locale.getDefault().language)
        set(savedLanguage) = AppSharePreference.getInstance(context)
            .saveString(Constant.KEY_LANGUAGE, savedLanguage)

    var notificationAnimation: Int
        get() = AppSharePreference.getInstance(context)
            .getInt(Constant.KEY_NOTIFICATION_ANIMATION, 0)
        set(notificationAnimation) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_NOTIFICATION_ANIMATION, notificationAnimation)

    var displayMode: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_DISPLAY_MODE, 0)
        set(displayMode) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_DISPLAY_MODE, displayMode)

    var notificationEnable: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_NOTIFICATION_ENABLE, true)
        set(notificationEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_NOTIFICATION_ENABLE, notificationEnable)

    var notificationDisplay: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_NOTIFICATION_QUICK_PLAY, true)
        set(notificationDisplay) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_NOTIFICATION_QUICK_PLAY, notificationDisplay)

    var notificationHideOnLockScreen: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_NOTIFICATION_HIDE_ON_LOCK_SCREEN, true)
        set(notificationHideOnLockScreen) = AppSharePreference.getInstance(context).saveBoolean(
            Constant.KEY_NOTIFICATION_HIDE_ON_LOCK_SCREEN, notificationHideOnLockScreen
        )

    var notificationShowAction: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_NOTIFICATION_SHOW_ACTION, true)
        set(notificationShowAction) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_NOTIFICATION_SHOW_ACTION, notificationShowAction)

    var notificationIcon: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_NOTIFICATION_ICON, 0)
        set(notificationIcon) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_NOTIFICATION_ICON, notificationIcon)


    var musicEnable: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_MUSIC_ENABLE, true)
        set(musicEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_MUSIC_ENABLE, musicEnable)

    var earphonesEnable: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_EARPHONE_ENABLE, true)
        set(earphonesEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_EARPHONE_ENABLE, earphonesEnable)

    var batteryChargeEnable: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_BATTERY_CHARGE_ENABLE, true)
        set(batteryChargeEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_BATTERY_CHARGE_ENABLE, batteryChargeEnable)

    var ringingStateEnable: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_RINGING_STATE_ENABLE, true)
        set(ringingStateEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_RINGING_STATE_ENABLE, ringingStateEnable)

    var directEnable: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_DIRECT_ENABLE, true)
        set(directEnable) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_DIRECT_ENABLE, directEnable)

    var incomingCall: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_INCOMING_CALL, true)
        set(incomingCall) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_INCOMING_CALL, incomingCall)

    var showDefault: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_SHOW_DEFAULT, true)
        set(showDefault) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_SHOW_DEFAULT, showDefault)

    var callTimer: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_CALL_TIMER, true)
        set(callTimer) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_CALL_TIMER, callTimer)

    var clickMode: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_CLICK_MODE, 0)
        set(clickMode) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_CLICK_MODE, clickMode)

    var alphaValueAnim: Float
        get() = AppSharePreference.getInstance(context).getFloat(Constant.KEY_ALPHA_VAL, 1f)
        set(alphaValueAnim) = AppSharePreference.getInstance(context)
            .saveFloat(Constant.KEY_ALPHA_VAL, alphaValueAnim)

    var animColor: String
        get() = AppSharePreference.getInstance(context)
            .getString(Constant.KEY_COLOR_ANIM, "#000000")
        set(animColor) = AppSharePreference.getInstance(context)
            .saveString(Constant.KEY_COLOR_ANIM, animColor)

    var showBubble: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_SHOW_BUBBLE, true)
        set(showBubble) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_SHOW_BUBBLE, showBubble)

    var showBubbleColor: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_SHOW_BUBBLE_COLOR, true)
        set(showBubbleColor) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_SHOW_BUBBLE_COLOR, showBubbleColor)

    var showBubbleBorder: Boolean
        get() = AppSharePreference.getInstance(context)
            .getBoolean(Constant.KEY_SHOW_BUBBLE_BORDER, true)
        set(showBubbleBorder) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_SHOW_BUBBLE_BORDER, showBubbleBorder)

    var bubbleFrequency: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_BUBBLE_FREQ, 0)
        set(bubbleFrequency) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_BUBBLE_FREQ, bubbleFrequency)


    var bubbleLocation: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_BUBBLE_LOCATION, 0)
        set(bubbleLocation) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_BUBBLE_LOCATION, bubbleLocation)

    var bubbleDisplayTime: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_BUBBLE_DISPLAY_TIME, 0)
        set(bubbleLocation) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_BUBBLE_DISPLAY_TIME, bubbleLocation)

    var alphaValueBubbleBackground: Float
        get() = AppSharePreference.getInstance(context)
            .getFloat(Constant.KEY_ALPHA_VAL_BACKGROUND, 1f)
        set(alphaValueBubbleBackground) = AppSharePreference.getInstance(context)
            .saveFloat(Constant.KEY_ALPHA_VAL_BACKGROUND, alphaValueBubbleBackground)

    var alphaValueBubbleBorder: Float
        get() = AppSharePreference.getInstance(context).getFloat(Constant.KEY_ALPHA_VAL_BORDER, 1f)
        set(alphaValueBubbleBorder) = AppSharePreference.getInstance(context)
            .saveFloat(Constant.KEY_ALPHA_VAL_BORDER, alphaValueBubbleBorder)
    var adjustVolume: Boolean
        get() = AppSharePreference.getInstance(context).getBoolean(Constant.KEY_ADJUST_VOLUME, true)
        set(adjustVolume) = AppSharePreference.getInstance(context)
            .saveBoolean(Constant.KEY_ADJUST_VOLUME, adjustVolume)

    var bubbleBackgroundColor: String
        get() = AppSharePreference.getInstance(context)
            .getString(Constant.KEY_BUBBLE_BACKGROUND_COLOR, "#000000")
        set(bubbleBackgroundColor) = AppSharePreference.getInstance(context)
            .saveString(Constant.KEY_BUBBLE_BACKGROUND_COLOR, bubbleBackgroundColor)

    var backgroundDynamicColor: String
        get() = AppSharePreference.getInstance(context)
            .getString(Constant.KEY_BACKGROUND_DYNAMIC_COLOR, "#000000")
        set(backgroundDynamicColor) = AppSharePreference.getInstance(context)
            .saveString(Constant.KEY_BACKGROUND_DYNAMIC_COLOR, backgroundDynamicColor)

    var backgroundDynamicAlpha: Float
        get() = AppSharePreference.getInstance(context)
            .getFloat(Constant.KEY_BACKGROUND_DYNAMIC_ALPHA, 1f)
        set(backgroundDynamicAlpha) = AppSharePreference.getInstance(context)
            .saveFloat(Constant.KEY_BACKGROUND_DYNAMIC_ALPHA, backgroundDynamicAlpha)

    var bubbleBackgroundBorder: String
        get() = AppSharePreference.getInstance(context)
            .getString(Constant.KEY_BUBBLE_BACKGROUND_BORDER_COLOR, "#000000")
        set(bubbleBackgroundBorder) = AppSharePreference.getInstance(context)
            .saveString(Constant.KEY_BUBBLE_BACKGROUND_BORDER_COLOR, bubbleBackgroundBorder)

    var sizeBorder: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_SIZE_BORDER, 5)
        set(sizeBorder) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_SIZE_BORDER, sizeBorder)

    var dynamicHeight: Int
        get() = AppSharePreference.getInstance(context)
            .getInt(Constant.KEY_HEIGHT, context.toPx(20).toInt())
        set(dynamicHeight) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_HEIGHT, dynamicHeight)

    var dynamicWidth: Int
        get() = AppSharePreference.getInstance(context).getInt(
            Constant.KEY_WIDTH, context.toPx(120).toInt()
        )
        set(dynamicWidth) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_WIDTH, dynamicWidth)

    var notchStyle: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_NOTCH_STYLE, 0)
        set(notchStyle) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_NOTCH_STYLE, notchStyle)

    var dynamicMarginVertical: Int
        get() = AppSharePreference.getInstance(context).getInt(Constant.KEY_MARGIN_VERTICAL, 0)
        set(dynamicMarginVertical) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_MARGIN_VERTICAL, dynamicMarginVertical)

    var dynamicMarginHorizontal: Int
        get() = AppSharePreference.getInstance(context).getInt(
            Constant.KEY_MARGIN_HORIZONTAL,
            context.resources.displayMetrics.widthPixels / 2 - context.config.dynamicWidth / 2
        )
        set(dynamicMarginHorizontal) = AppSharePreference.getInstance(context)
            .saveInt(Constant.KEY_MARGIN_HORIZONTAL, dynamicMarginHorizontal)

    var listPackageFilter: List<AppDetail>
        get() = AppSharePreference.getInstance(context)
            .getObjectFromSharePreference(Constant.KEY_LIST_PACKAGE_FILTER, mutableListOf())
        set(listPackageFilter) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_LIST_PACKAGE_FILTER, listPackageFilter)

    var listBackgroundColor: List<String>
        get() = AppSharePreference.getInstance(context).getObjectFromSharePreference(
                Constant.KEY_BACKGROUND_COLOR,
                mutableListOf("#000000", "#EF4444", "#FACC15", "#4ADE80", "#4ADE80", "#4B44BF")
            )
        set(listBackgroundColor) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_BACKGROUND_COLOR, listBackgroundColor)

    var listAnimColor: List<String>
        get() = AppSharePreference.getInstance(context).getObjectFromSharePreference(
                Constant.KEY_ANIM_COLOR,
                mutableListOf("#000000", "#EF4444", "#FACC15", "#4ADE80", "#4ADE80", "#4B44BF")
            )
        set(listAnimColor) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_ANIM_COLOR, listAnimColor)

    var listBubbleColor: List<String>
        get() = AppSharePreference.getInstance(context).getObjectFromSharePreference(
                Constant.KEY_BUBBLE_COLOR,
                mutableListOf("#000000", "#EF4444", "#FACC15", "#4ADE80", "#4ADE80", "#4B44BF")
            )
        set(listBubbleColor) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_BUBBLE_COLOR, listBubbleColor)

    var listBubbleColorBorder: List<String>
        get() = AppSharePreference.getInstance(context).getObjectFromSharePreference(
                Constant.KEY_BUBBLE_COLOR_BORDER,
                mutableListOf("#000000", "#EF4444", "#FACC15", "#4ADE80", "#4ADE80", "#4B44BF")
            )
        set(listBubbleColorBorder) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_BUBBLE_COLOR_BORDER, listBubbleColorBorder)

}