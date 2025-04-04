package com.neko.hiepdph.dynamicislandvip.data.model

import android.os.Parcel
import android.os.Parcelable

data class AppDetail(
    var label: String? = null,
    var pkg: String? = null,
    var activityInfoName: String? = null,
    var image: Int = 0,
    var isSorted: Boolean = false,
    var isSelected: Boolean = false,
    var isCurrentUser: Boolean = true
) : Parcelable {

    constructor(parcel: Parcel) : this(
        label = parcel.readString(),
        pkg = parcel.readString(),
        activityInfoName = parcel.readString(),
        image = parcel.readInt(),
        isSorted = parcel.readByte() != 0.toByte(),
        isSelected = parcel.readByte() != 0.toByte(),
        isCurrentUser = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(pkg)
        parcel.writeString(activityInfoName)
        parcel.writeInt(image)
        parcel.writeByte(if (isSorted) 1 else 0)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isCurrentUser) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AppDetail> {
        override fun createFromParcel(parcel: Parcel): AppDetail {
            return AppDetail(parcel)
        }

        override fun newArray(size: Int): Array<AppDetail?> {
            return arrayOfNulls(size)
        }
    }
}