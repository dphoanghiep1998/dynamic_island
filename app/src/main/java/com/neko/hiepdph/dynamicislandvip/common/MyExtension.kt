package com.neko.hiepdph.dynamicislandvip.common

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neko.hiepdph.dynamicislandvip.common.config.MainConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale

val Context.config: MainConfig get() = MainConfig.newInstance(this)


fun Context.createContext(newLocale: Locale): Context = if (buildMaxVersionN()) {
    createContextAndroidN(newLocale)
} else {

    createContextLegacy(newLocale)
}
fun Context.getLocalizedContext(): Context {


    val config = Configuration(resources.configuration)
    val locale = Locale(this.config.savedLanguage)
    Locale.setDefault(locale)
    config.setLocale(locale)

    return createConfigurationContext(config)
}

fun String.getFileNameFromUrl(): String {
    val uri = Uri.parse(this)
    return uri.lastPathSegment ?: ""
}

private fun Context.createContextAndroidN(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(newLocale)
    return createConfigurationContext(configuration)
}

private fun Context.createContextLegacy(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.locale = newLocale
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return this
}

fun View.clickWithDebounce(debounceTime: Long = 400L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}
fun getFormattedTime(j: Long): String {
    val j2 = j / 1000
    val decimalFormat = DecimalFormat("00")
    val format = decimalFormat.format(j2 / 60)
    return format + ":" + decimalFormat.format(j2 % 60)
}

fun convertDpToPixel(f: Float, context: Context?): Float {
    val resources = if (context == null) {
        Resources.getSystem()
    } else {
        context.resources
    }
    return f * ((resources.displayMetrics.densityDpi.toFloat()) / 160.0f)
}
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        ) -> true

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        ) -> true

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        ) -> true

        else -> false
    }

}

fun Context.openLink(strUri: String?) {
    try {
        val uri = Uri.parse(strUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block()
            this@launch.cancel()
        }
    }
}

fun <T> Context.isAccessibilityServiceRunning(service: Class<T>): Boolean {
    val expectedComponentName = ComponentName(this, service)
    val enabledServicesSetting = Settings.Secure.getString(
        contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        val componentName = ComponentName.unflattenFromString(colonSplitter.next())
        if (componentName == expectedComponentName) {
            return true
        }
    }

    return false
}



fun Context.toPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
)



