package com.neko.hiepdph.dynamicislandvip.view.language

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.common.supportDisplayLang
import com.neko.hiepdph.dynamicislandvip.common.supportedLanguages
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityLanguageBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.main.MainActivity
import com.neko.hiepdph.dynamicislandvip.view.onboard.OnboardActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity
import java.util.Locale

class LanguageActivity() : BaseActivity<ActivityLanguageBinding>() {
    private var isReloadedAds = false
    override fun getViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    private var currentLanguage = Locale.getDefault().language
    private var adapter: AdapterLanguage? = null
    private var currentIndex = -1
    private var adLoaded = false


    override fun initView() {
//        AdsCore.showNativeAds(
//            this, binding.nativeAdmob, {
//                if (currentIndex != -1) {
//                    binding.containerButton.show()
//                }
//
//            }, {
//                if (currentIndex != -1) {
//                    binding.containerButton.show()
//                }
//            }, NativeTypeEnum.LANGUAGE
//        )
        binding.btnCheck.hide()
        currentLanguage = config.savedLanguage
        if (currentLanguage !in supportedLanguages().map { it.language }) {
            currentLanguage = "en"
            config.savedLanguage = "en"
        }
        if (intent.getBooleanExtra(Constant.KEY_FROM_MAIN, false)) {
            binding.btnBack.show()
            binding.btnBack.clickWithDebounce {
                finish()
            }
        } else {
            binding.btnBack.hide()

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

    fun getDialogWaiting(): Dialog {
        val dialogLoad = Dialog(this)
        dialogLoad.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialogLoad.setContentView(R.layout.dialog_post_loading)
        dialogLoad.setCanceledOnTouchOutside(false)
        dialogLoad.setCancelable(false)
        dialogLoad.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        return dialogLoad
    }

    override fun initButton() {
        binding.btnCheck.clickWithDebounce {
            if (currentIndex == -1) {
                toast(R.string.selected_lang)

            } else {
                clickApplyBtn()
            }
        }

        binding.containerCurrentLanguage.clickWithDebounce {
            currentIndex = 100
            if (!isReloadedAds) {
                isReloadedAds = true
                binding.btnCheck.show()
//                if (AdsConfigUtils(this).getPremium()) {
//                    binding.containerButton.show()
//                    adLoaded = true
//                    isReloadedAds = true
//                } else {
//                    val dialogLoad = getDialogWaiting()
//                    lifecycleScope.launch(Dispatchers.Main) { dialogLoad.show() }
//                    AdsCore.showNativeAds(
//                        this, binding.nativeAdmob, {
//                            if (currentIndex != -1) {
//                                binding.containerButton.show()
//                            }
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                delay(1000)
//                                dialogLoad.dismiss()
//                            }
//                        }, {
//                            if (currentIndex != -1) {
//                                binding.containerButton.show()
//                            }
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                delay(1000)
//                                dialogLoad.dismiss()
//                            }
//                        }, NativeTypeEnum.LANGUAGE
//                    )
//                }

            }
            if (adLoaded) {
                binding.btnCheck.show()
            }
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
            startActivity(Intent(this, OnboardActivity::class.java))
            finish()
        } else {
            config.savedLanguage = currentLanguage
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        startService(
            Intent(
                this, MyAccessService::class.java
            ).apply {
                action = Constant.UPDATE_LANG
            })


    }

    private fun initRecyclerView() {
        val mDisplayLangList: MutableList<Triple<Int, Int, Locale>> =
            supportDisplayLang().toMutableList()
        adapter = AdapterLanguage(this, onCLickItem = {
            currentIndex = 100
            binding.btnCheck.show()
//            if (AdsConfigUtils(this).getPremium()) {
//                adLoaded = true
//                isReloadedAds = true
//                binding.containerButton.show()
//            } else {
//                if (!isReloadedAds) {
//                    isReloadedAds = true
//                    val dialogLoad = getDialogWaiting()
//                    lifecycleScope.launch(Dispatchers.Main) { dialogLoad.show() }
//                    AdsCore.showNativeAds(
//                        this, binding.nativeAdmob, {
//                            if (currentIndex != -1) {
//                                binding.containerButton.show()
//                            }
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                delay(1000)
//                                dialogLoad.dismiss()
//                            }
//                        }, {
//                            if (currentIndex != -1) {
//                                binding.containerButton.show()
//                            }
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                delay(1000)
//                                dialogLoad.dismiss()
//                            }
//                        }, NativeTypeEnum.LANGUAGE
//                    )
//                }
//            }

            if (adLoaded) {
                binding.btnCheck.show()
            }
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

    override fun onStart() {
        super.onStart()

    }

    private fun insertAds() {

    }

}