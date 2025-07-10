package com.neko.hiepdph.dynamicislandvip.view.setting

import android.content.Intent
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.openLink
import com.neko.hiepdph.dynamicislandvip.databinding.ActivitySettingsBinding
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogFeedBack
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogRateUs
import com.neko.hiepdph.dynamicislandvip.view.language.LanguageActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    override fun getViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun initButton() {
        super.initButton()
        binding.btnBack.clickWithDebounce {
            finish()
        }

        binding.containerLanguage.clickWithDebounce {
            startActivity(
                Intent(
                    this, LanguageActivity::class.java
                ).putExtra(Constant.KEY_FROM_MAIN, true)
            )
        }

        binding.containerRate.clickWithDebounce {
            val dialogRateUs = DialogRateUs().onCreateDialog(this)
            dialogRateUs.show()
        }

        binding.containerShare.clickWithDebounce {
            shareApp()
        }

        binding.containerPolicy.clickWithDebounce {
            openLink(Constant.URL_PRIVACY)
        }

        binding.containerDownload.clickWithDebounce {

        }

        binding.containerFeedBack.clickWithDebounce {
            val dialogFeedBack = DialogFeedBack(this)
            dialogFeedBack.show()
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Constant.URL_APP)
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}