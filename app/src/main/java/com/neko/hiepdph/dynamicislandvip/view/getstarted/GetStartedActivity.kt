package com.neko.hiepdph.dynamicislandvip.view.getstarted

import android.content.Intent
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityGetStartedBinding
import com.neko.hiepdph.dynamicislandvip.view.accesspermisison.AccessibilityPermissionActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

class GetStartedActivity : BaseActivity<ActivityGetStartedBinding>() {
    override fun getViewBinding(): ActivityGetStartedBinding {
        return ActivityGetStartedBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnGetStarted.clickWithDebounce {
            startActivity(Intent(this, AccessibilityPermissionActivity::class.java))
            finish()
        }
    }

}