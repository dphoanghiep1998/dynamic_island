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
    fun getAirpodsBattery(context: Context): Float {
        val airPodLevel: Int = getAirPodLevel(context)
        if (airPodLevel == -1) {
            return -1f
        }
        return airPodLevel / 10 * 100f

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