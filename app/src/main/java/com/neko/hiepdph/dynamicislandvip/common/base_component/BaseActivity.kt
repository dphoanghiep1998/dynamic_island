package com.neko.hiepdph.mypiano.common.base_component

//import com.example.baseproject.BuildConfig
//import com.example.baseproject.common.InterAdsEnum
//import com.example.baseproject.common.isInternetAvailable
//import com.neko.hiepdph.mypiano.view.dialog.DialogFragmentLoadingInterAds
//import com.gianghv.admob.InterstitialSingleReqAdManager
//import com.gianghv.libads.max.MaxInterstitialSingleReqAdManager
//import com.example.baseproject.common.ads.utils.AdsConfigUtils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion30
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.createContext
import java.util.Locale

abstract class BaseActivity<VB : ViewBinding>() : AppCompatActivity() {
    lateinit var binding: VB
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        if (true) {
//            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
        }
        super.onCreate(savedInstanceState)
        if (buildMinVersion30()) {
            window.setDecorFitsSystemWindows(false)
        }
        binding = getViewBinding()
        setContentView(binding.root)
        hideNavigationBar()
        initView()
        initButton()
        setupObserver()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        receiver = object : BroadcastReceiver() {
//            override fun onReceive(p0: Context?, mItent: Intent?) {
//                config.isVolumeUserChanged = true
//            }
//        }
//        val intentFilter = IntentFilter()
//        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION")
//        registerReceiver(receiver, intentFilter)

    }

    override fun onPause() {
        super.onPause()
//        MainApplication.app.liveDataShowOpenAds.removeObservers(this)
    }


    protected fun changeBackPressCallBack(action: () -> Unit) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action.invoke()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    protected open fun initButton() {}
    protected open fun setupObserver() {}

    @IdRes
    open val rootDes: Int? = null

    @IdRes
    open val navHostId: Int? = null

    @IdRes
    open val graphId: Int? = null

    val navContainer: NavController? by lazy {
        (navHostId?.let {
            supportFragmentManager.findFragmentById(
                it
            )
        } as? NavHostFragment)?.navController
    }

    @MainThread
    fun pushTo(
        @IdRes resId: Int,
        args: Bundle? = null,
        isAddNew: Boolean = false
    ) {

        val currentDes = navContainer?.currentDestination

        val destinationId = currentDes?.getAction(resId)?.destinationId

        if (currentDes?.id == destinationId && !isAddNew) return

        if (destinationId != null && navContainer?.popBackStack(destinationId, false) == true) {
            return
        }

        currentDes?.getAction(resId)?.navOptions?.let {
            navContainer?.navigate(
                resId,
                args,
                navOptions {
                    anim {
                        enter = R.anim.fade_in
                        exit = R.anim.fade_out
                        popEnter = R.anim.fade_in
                        popExit = R.anim.fade_out
                    }
                    popUpTo(it.popUpToId) {
                        inclusive = it.isPopUpToInclusive()
                    }

                    launchSingleTop = it.shouldLaunchSingleTop()

                    restoreState = it.shouldRestoreState()
                }

            )
        }
    }

    @MainThread
    fun popTo(@IdRes destinationId: Int? = null, inclusive: Boolean = false) {
        navContainer?.apply {
            if (destinationId == null) popBackStack()
            else popBackStack(destinationId, inclusive)
        }
    }

    fun popToRoot() {
        rootDes?.let { popTo(it, false) }
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(config.savedLanguage)
        )
    )

    protected fun toast(res: Int) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.layout_toast, null)
        val toast = Toast.makeText(
            this@BaseActivity, res, Toast.LENGTH_SHORT
        )
        toast.view = layout
        val textview = layout.findViewById(R.id.tvToast) as TextView
        textview.text = getString(res)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 600)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1400)
    }

    private fun hideNavigationBar() {
        val decorView: View = window.decorView

        val uiOptions: Int =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        decorView.systemUiVisibility = uiOptions
    }

    abstract fun getViewBinding(): VB

    abstract fun initView()
    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            unregisterReceiver(it)
        }
    }

}