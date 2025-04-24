package com.neko.hiepdph.dynamicislandvip.common.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Notification {

    // Core details
    public String key;
    public String tag;
    public String category;
    public String app_name;
    public String groupKey;
    public String template;

    public String pack;
    public int uId;

    // Display
    public Bitmap icon;
    public Bitmap senderIcon;
    public Bitmap picture;
    public CharSequence title;
    public CharSequence text;
    public CharSequence bigText;
    public CharSequence subText;
    public CharSequence summaryText;
    public CharSequence titleBig;
    public CharSequence info_text;
    public CharSequence substName;
    public int color;

    // Timing
    public long postTime;
    public long duration;
    public long position;

    // Progress
    public int progress;
    public int progressMax;
    public boolean progressIndeterminate;

    // Grouping
    public boolean isGroup;
    public boolean isGroupConversation;
    public boolean isAppGroup;

    // State flags
    public boolean isOngoing;
    public boolean isClearable;
    public boolean isLocal;
    public boolean isChronometerRunning;
    public boolean showChronometer;
    public boolean useIphoneCallDesign;
    public CharSequence extraTitle;

    // Misc
    public int count;
    public int local_left_icon;
    public int local_right_icon;
    public PendingIntent pendingIntent;
    public ArrayList<ActionParsable> actions; // ✅ compatible with Kotlin

    public String type;

    public HashMap<String, Notification> keyMap = new HashMap<>();

    // Full constructor
    public Notification(Bitmap icon, Bitmap senderIcon, CharSequence title, CharSequence text, int count,String pack, long postTime, PendingIntent pendingIntent, ArrayList<ActionParsable> actions, CharSequence bigText, String app_name, boolean isClearAble, int color, Bitmap picture, String groupKey, String key, boolean isGroupConversation, boolean isAppGroup, boolean isGroup, boolean isOngoing, String tag, int uId, String template, CharSequence substName, CharSequence subText, CharSequence titleBig, CharSequence info_text, int progressMax, int progress, boolean progressIndeterminate, CharSequence summaryText, boolean showChronometer, String category, String extraTitle) {
        this.icon = icon;
        this.senderIcon = senderIcon;
        this.title = title;
        this.text = text;
        this.count = count;
        this.postTime = postTime;
        this.pack = pack;
        this.pendingIntent = pendingIntent;
        this.actions = actions;
        this.bigText = bigText;
        this.app_name = app_name;
        this.isClearable = isClearAble;
        this.color = color;
        this.picture = picture;
        this.groupKey = groupKey;
        this.isGroupConversation = isGroupConversation;
        this.isAppGroup = isAppGroup;
        this.isGroup = isGroup;
        this.isOngoing = isOngoing;
        this.tag = tag;
        this.uId = uId;
        this.template = template;
        this.substName = substName;
        this.subText = subText;
        this.titleBig = titleBig;
        this.info_text = info_text;
        this.progressMax = progressMax;
        this.progress = progress;
        this.progressIndeterminate = progressIndeterminate;
        this.summaryText = summaryText;
        this.showChronometer = showChronometer;
        this.category = category;
        this.type = "";
        this.isLocal = false;
        this.useIphoneCallDesign = false;
        this.isChronometerRunning = false;
        this.keyMap = new HashMap<>();
        this.extraTitle = extraTitle;
        this.key = key;
    }

    // Local notification constructor
    public Notification(String type, int localLeftIcon, int localRightIcon) {
        this.type = type;
        this.local_left_icon = localLeftIcon;
        this.local_right_icon = localRightIcon;
        this.isLocal = true;
        this.useIphoneCallDesign = false;
        this.isChronometerRunning = false;
        this.keyMap = new HashMap<>();
    }
}
