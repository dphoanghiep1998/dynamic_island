package com.neko.hiepdph.dynamicislandvip.view.language

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.common.supportDisplayLang
import com.neko.hiepdph.dynamicislandvip.common.supportedLanguages
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityLanguageBinding
import com.neko.hiepdph.dynamicislandvip.view.main.MainActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

import java.util.Locale

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var currentLanguage = Locale.getDefault().language
    private var adapter: AdapterLanguage? = null
    private var currentIndex = -1
    override fun getViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initView() {
        currentLanguage = config.savedLanguage
        if (currentLanguage !in supportedLanguages().map { it.language }) {
            currentLanguage = "en"
            config.savedLanguage = "en"
        }
        changeBackPressCallBack {
            if (intent.getBooleanExtra(Constant.KEY_FROM_MAIN, false)) {
                finish()
            } else {

            }
        }
        initRecyclerView()
        initButton()
        insertAds()
    }


    override fun initButton() {
        binding.btnCheck.show()
//        binding.loading.hide()
        binding.btnCheck.clickWithDebounce {
            if(currentIndex == -1){
                Toast.makeText(this@LanguageActivity,getText(R.string.selected_lang),Toast.LENGTH_SHORT).show()
            }else{
                clickApplyBtn()
            }
        }

        binding.containerCurrentLanguage.clickWithDebounce {
            currentIndex = 100
            binding.tick.setImageResource(R.drawable.ic_tick_active)
            binding.tvCountry.setTextColor(getColor(R.color.black))
            binding.containerCurrentLanguage.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            adapter?.removeSelectedId()
            currentLanguage = config.savedLanguage
        }
    }

    private fun clickApplyBtn() {
        val isFromMain = intent.getBooleanExtra(Constant.KEY_FROM_MAIN, false)
//        showInterAds(action = {
        if (!isFromMain) {
//            pushEvent( "click_language_save")
            config.savedLanguage = currentLanguage
//            startActivity(Intent(this, OnboardActivity::class.java))
            finish()
        } else {
            config.savedLanguage = currentLanguage
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
//        }, actionFailed = {
//            if (!isFromMain) {
//                pushEvent(this, "click_language_save")
//                config.isPassLang = true
//                config.savedLanguage = currentLanguage
//                startActivity(Intent(this, GetStartedActivity::class.java))
//                finish()
//            } else {
//                config.savedLanguage = currentLanguage
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }
//        }, InterAdsEnum.LANG)


    }

    private fun initRecyclerView() {
        val mDisplayLangList: MutableList<Triple<Int, Int, Locale>> =
            supportDisplayLang().toMutableList()
        adapter = AdapterLanguage(this, onCLickItem = {
            currentIndex = 100
            currentLanguage = it.third.language
            binding.tick.setImageResource(R.drawable.ic_tick_inactive)
            binding.tvCountry.setTextColor(getColor(R.color.black))
            binding.containerCurrentLanguage.setBackgroundResource(R.drawable.bg_8)

        })

        adapter?.setData(mDisplayLangList)
        handleUnSupportLang(mDisplayLangList)

        binding.rcvLanguage.adapter = adapter
        binding.rcvLanguage.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }


    private fun handleUnSupportLang(mLanguageList: MutableList<Triple<Int, Int, Locale>>) {
        var support = false
        mLanguageList.forEachIndexed { index, item ->
            if (item.third.language == currentLanguage) {
                support = true
                binding.tick.setImageResource(R.drawable.ic_tick_inactive)
                binding.imvFlag.setImageResource(item.second)
                binding.tvCountry.text = getText(item.first)
                val newDisplayLangList =
                    mLanguageList.filter { l -> l.third.language != item.third.language }
                        .toMutableList()
                adapter?.setData(newDisplayLangList)
            }
        }
        if (!support) {
            currentLanguage = mLanguageList[0].third.language
//            binding.tick.setImageResource(R.drawable.ic_tick_lang_active)
//            binding.imvFlag.setImageResource(mLanguageList[0].second)
//            binding.tvCountry.text = getText(mLanguageList[0].first)
            val newDisplayLangList =
                mLanguageList.filter { l -> l.third.language != mLanguageList[0].third.language }
                    .toMutableList()
            adapter?.setData(newDisplayLangList)
        }
    }


    private fun insertAds() {
//        AdsCore.showNativeAds(this, binding.nativeAdmob, binding.nativeMax, {
//            binding.btnCheck.show()
//            binding.loading.hide()
//        }, {
//            binding.btnCheck.show()
//            binding.loading.hide()
//        }, NativeTypeEnum.LANGUAGE
//        )
    }

}