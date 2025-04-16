package com.neko.hiepdph.dynamicislandvip.common

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.BatteryManager
import androidx.core.app.ActivityCompat
import com.neko.hiepdph.dynamicislandvip.R

object Utils {
    fun getBitmapFromByteArray(bArr: ByteArray?): Bitmap? {
        if (bArr != null) {
            return BitmapFactory.decodeByteArray(bArr, 0, bArr.size)
        }
        return null
    }
    fun getAirpodsBattery(context: Context): Int {
        val airPodLevel = getAirPodLevel(context)
        if (airPodLevel == -1) {
            return -1
        }
        val i = airPodLevel / 10
        var i2: Int = R.drawable.airbug_00
        if (i == 1) {
            i2 = R.drawable.airbug_01
        }
        if (i == 2) {
            i2 = R.drawable.airbug_02
        }
        if (i == 3) {
            i2 = R.drawable.airbug_03
        }
        if (i == 4) {
            i2 = R.drawable.airbug_04
        }
        if (i == 5) {
            i2 = R.drawable.airbug_05
        }
        if (i == 6) {
            i2 = R.drawable.airbug_06
        }
        if (i == 7) {
            i2 = R.drawable.airbug_07
        }
        if (i == 8) {
            i2 = R.drawable.airbug_08
        }
        if (i == 9) {
            i2 = R.drawable.airbug_09
        }
        return if (i == 10) R.drawable.airbug_10 else i2
    }

    fun getBatteryImage(context: Context): Int {
        val batteryLevel: Int = getBatteryLevel(context) / 10
        var i: Int = R.drawable.battery_00
        if (batteryLevel == 1) {
            i = R.drawable.battery_01
        }
        if (batteryLevel == 2) {
            i = R.drawable.battery_02
        }
        if (batteryLevel == 3) {
            i = R.drawable.battery_03
        }
        if (batteryLevel == 4) {
            i = R.drawable.battery_04
        }
        if (batteryLevel == 5) {
            i = R.drawable.battery_05
        }
        if (batteryLevel == 6) {
            i = R.drawable.battery_06
        }
        if (batteryLevel == 7) {
            i = R.drawable.battery_07
        }
        if (batteryLevel == 8) {
            i = R.drawable.battery_08
        }
        if (batteryLevel == 9) {
            i = R.drawable.battery_09
        }
        return if (batteryLevel == 10) R.drawable.battery_10 else i
    }

    fun getBatteryLevel(context: Context): Int {
        return (context.getSystemService("batterymanager") as BatteryManager).getIntProperty(4)
    }

    fun getAirPodLevel(context: Context): Int {
        try {
            if (BluetoothAdapter.getDefaultAdapter() == null || ActivityCompat.checkSelfPermission(
                    context, "android.permission.BLUETOOTH_CONNECT"
                ) != 0
            ) {
                return -1
            }
            var i = -1
            for (address in BluetoothAdapter.getDefaultAdapter().bondedDevices) {
                try {
                    val remoteDevice =
                        BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address.address)
                    i = (remoteDevice.javaClass.getMethod("getBatteryLevel", *arrayOfNulls(0))
                        .invoke(remoteDevice, *arrayOfNulls(0)) as Int)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return i
        } catch (unused: Exception) {
        }
        return -1
    }
    fun getFormattedDate(timestamp: Long): String? {
        val currentTime = System.currentTimeMillis()

        if (timestamp > currentTime || timestamp <= 0) return null

        val diff = currentTime - timestamp

        return when {
            diff < 60_000 -> "now"
            diff < 120_000 -> "1m"
            diff < 3_000_000 -> "${diff / 60_000} m"
            diff < 5_400_000 -> "1h"
            diff < 86_400_000 -> "${diff / 3_600_000} h"
            diff < 172_800_000 -> "1d"
            else -> "${diff / 86_400_000} d"
        }
    }


}