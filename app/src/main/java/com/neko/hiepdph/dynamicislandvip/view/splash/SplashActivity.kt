package com.neko.hiepdph.dynamicislandvip.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.isAccessibilityServiceRunning
import com.neko.hiepdph.dynamicislandvip.databinding.ActivitySplashBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.language.LanguageActivity
import com.neko.hiepdph.dynamicislandvip.view.onboard.OnboardActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initView() {
        changeBackPressCallBack {  }
        if (!isAccessibilityServiceRunning(MyAccessService::class.java)) {
            config.controlEnable = false
        }
        Handler(mainLooper).postDelayed({
            lifecycleScope.launchWhenResumed {
                if(config.isPassLang){
                    startActivity(Intent(this@SplashActivity, OnboardActivity::class.java))
                }else{
                    startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
                }
                finish()
            }

        },3000)

    }
}