package com.neko.hiepdph.dynamicislandvip.data.model

import android.graphics.drawable.Drawable

data class AppModel(
    var id: Long, val pkg: String, val className: String,
) {
    var icon: Drawable? = null
}