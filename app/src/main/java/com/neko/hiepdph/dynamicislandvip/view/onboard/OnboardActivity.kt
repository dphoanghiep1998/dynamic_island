package com.neko.hiepdph.dynamicislandvip.view.onboard

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityOnboardBinding
import com.neko.hiepdph.dynamicislandvip.view.accesspermisison.AccessibilityPermissionActivity
import com.neko.hiepdph.dynamicislandvip.view.getstarted.GetStartedActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardActivity : BaseActivity<ActivityOnboardBinding>() {
    private lateinit var viewpagerAdapter: ViewPagerAdapter
    private var isAgreed = true

    //    private var isAdsLoaded = false
    private var isAdsLoaded = true

    //    private var isAdsLoadSuccess = false
    private var isAdsLoadSuccess = true

    override fun getViewBinding(): ActivityOnboardBinding {
        return ActivityOnboardBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnNext.hide()
        initViewPager()
        changeBackPressCallBack {

        }
        loadAds()
        val ss1 = getString(R.string.term1)
        val ss2 = getString(R.string.term2)
        val clickableTerm = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_POLICY))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = getColor(R.color.g3)
                ds.bgColor = Color.TRANSPARENT
            }

        }.apply { }
//        val colorTerm = object : ClickableSpan() {
//            override fun onClick(p0: View) {
//
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false
//                ds.color = getColor(R.color.black)
//                ds.bgColor = Color.TRANSPARENT
//            }
//
//        }.apply { }
        val ss3 = SpannableString("$ss1 $ss2")
//        ss3.setSpan(colorTerm, 0, ss1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss3.setSpan(clickableTerm, ss1.length + 1, ss3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerm.text = ss3
        binding.tvTerm.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerm.highlightColor = Color.TRANSPARENT
    }

    override fun initButton() {
        binding.btnNext.show()
//        binding.btnNext.setOnClickListener {
//            val currentItem = binding.vpOnboard.currentItem
//            if (binding.vpOnboard.currentItem == 2) {
//                binding.btnNext.text = getString(R.string.let_start)
//                if (isAdsLoaded) {
//                    binding.btnNext.show()
//                } else {
//                    binding.btnNext.hide()
//                }
//            } else {
//                binding.vpOnboard.currentItem = currentItem + 1
//            }
//        }

        binding.btnNext.clickWithDebounce {
            if (binding.vpOnboard.currentItem == 2) {
                if (isAgreed) {
                    startActivity(Intent(this, GetStartedActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this,R.string.agreement, Toast.LENGTH_SHORT).show()
                }

            } else {
                binding.vpOnboard.currentItem = binding.vpOnboard.currentItem + 1
            }

        }

        binding.btnCheck.clickWithDebounce {
            isAgreed = !isAgreed
            if (isAgreed) {
                binding.btnCheck.setImageResource(R.drawable.ic_check_active_app)
            } else {
                binding.btnCheck.setImageResource(R.drawable.ic_check_inactive_app)
            }
        }


    }

    private fun initViewPager() {

        viewpagerAdapter = ViewPagerAdapter(
            this, supportFragmentManager, lifecycle
        )
        binding.vpOnboard.adapter = viewpagerAdapter

        binding.vpOnboard.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.dot.setImageResource(R.drawable.ic_dot_1)
                        binding.btnNext.text = getString(R.string.next)
                    }

                    1 -> {
                        binding.dot.setImageResource(R.drawable.ic_dot_2)
                        binding.btnNext.text = getString(R.string.next)

                    }

                    2 -> {
                        binding.dot.setImageResource(R.drawable.ic_dot_3)
                        binding.btnNext.text = getString(R.string.let_start)
                    }
                }
//                when (binding.vpOnboard.currentItem) {
//                    0, 1 -> {
//                        binding.containerAds.hide()
//
//                        if (!isAdsLoaded) {
//                            binding.btnNext.show()
//                            binding.btnStart.hide()
//                            binding.loadingAds.hide()
//                        } else {
//                            binding.btnNext.show()
//                            binding.btnStart.hide()
//                            binding.loadingAds.hide()
//                        }
//                    }
//
//                    2 -> {
//                        if (!isAdsLoaded) {
//                            binding.btnNext.hide()
//                            binding.btnStart.hide()
//                            binding.loadingAds.show()
//                        } else {
//                            binding.btnStart.show()
//                            binding.btnNext.hide()
//                            binding.loadingAds.hide()
//                            if (isAdsLoadSuccess) {
//                                binding.containerAds.show()
//                            } else {
//                                binding.containerAds.hide()
//                            }
//                        }
//                    }
//                }
            }
        })
    }

    private fun loadAds() {
//        AdsCore.showNativeAds(this, binding.nativeAdmob, binding.nativeMax, {
//            isAdsLoadSuccess = true
//            handleWhenLoadAdsDone()
//        }, { handleWhenLoadAdsDone() }, NativeTypeEnum.INTRO)


    }

    private fun handleWhenLoadAdsDone() {
        isAdsLoaded = true
//        when (binding.vpOnboard.currentItem) {
//            0, 1 -> {
//                binding.containerAds.hide()
//
//                if (!isAdsLoaded) {
//                    binding.btnNext.show()
//                    binding.btnStart.hide()
//                    binding.loadingAds.hide()
//                } else {
//                    binding.btnNext.show()
//                    binding.btnStart.hide()
//                    binding.loadingAds.hide()
//                }
//            }
//
//            2 -> {
//                if (!isAdsLoaded) {
//                    binding.btnNext.hide()
//                    binding.btnStart.hide()
//                    binding.loadingAds.show()
//                } else {
//                    binding.btnStart.show()
//                    binding.btnNext.hide()
//                    binding.loadingAds.hide()
//                    if (isAdsLoadSuccess) {
//                        binding.containerAds.show()
//                    } else {
//                        binding.containerAds.hide()
//                    }
//                }
//            }
//        }
    }


}