package com.neko.hiepdph.dynamicislandvip.view.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityMainBinding
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogExit
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        changeBackPressCallBack {
            val dialogExit = DialogExit(this, {
            }, {
                finish()
            })
            dialogExit.show()
        }
    }

    override val rootDes: Int? by lazy { R.id.homeFragment }

    override val navHostId: Int? by lazy { binding.navHostFragment.id }

    override val graphId: Int? by lazy { R.navigation.main_nav }

    override fun initButton() {
        binding.btnHome.clickWithDebounce {
            binding.tvConfig.hide()
            binding.tvModule.hide()
            binding.tvHome.show()
            binding.btnConfig.setBackgroundResource(R.drawable.bg_home_inactive)
            binding.btnModule.setBackgroundResource(R.drawable.bg_home_inactive)
            binding.btnHome.setBackgroundResource(R.drawable.bg_home_active)
            pushTo(R.id.actionHome)
        }

        binding.btnConfig.clickWithDebounce {
            binding.tvHome.hide()
            binding.tvModule.hide()
            binding.tvConfig.show()
            binding.btnConfig.setBackgroundResource(R.drawable.bg_home_active)
            binding.btnModule.setBackgroundResource(R.drawable.bg_home_inactive)
            binding.btnHome.setBackgroundResource(R.drawable.bg_home_inactive)
            pushTo(R.id.actionConfig)

        }

        binding.btnModule.clickWithDebounce {
            binding.tvHome.hide()
            binding.tvModule.show()
            binding.tvConfig.hide()
            binding.btnConfig.setBackgroundResource(R.drawable.bg_home_inactive)
            binding.btnModule.setBackgroundResource(R.drawable.bg_home_active)
            binding.btnHome.setBackgroundResource(R.drawable.bg_home_inactive)
            pushTo(R.id.actionModule)

        }

    }


}