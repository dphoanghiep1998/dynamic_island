package com.neko.hiepdph.dynamicislandvip.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppDetail(
    var label: String? = null,
    var pkg: String? = null,
    var activityInfoName: String? = null,
    var image: Int = 0,
    var isSorted: Boolean = false,
    var isSelected: Boolean = false,
    var isCurrentUser: Boolean = true
) : Parcelable {
}