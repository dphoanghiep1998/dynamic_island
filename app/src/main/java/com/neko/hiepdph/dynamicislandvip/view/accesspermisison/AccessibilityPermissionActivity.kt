package com.neko.hiepdph.dynamicislandvip.view.accesspermisison

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.buildMinVersion26
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.notification.NotificationListener
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.SliderData
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityAccessibilityPermissionBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.main.MainActivity
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AccessibilityPermissionActivity : BaseActivity<ActivityAccessibilityPermissionBinding>() {
    private var isGranted = false
    override fun getViewBinding(): ActivityAccessibilityPermissionBinding {
        return ActivityAccessibilityPermissionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.root.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
        lifecycleScope.launchWhenResumed {
            checkPermission()
        }
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
                ds.color = getColor(R.color.p200)
                ds.bgColor = Color.TRANSPARENT
            }

        }.apply { }
        val ss3 = SpannableString("$ss1 $ss2")

        ss3.setSpan(clickableTerm, ss1.length + 1, ss3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerm.text = ss3
        binding.tvTerm.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerm.highlightColor = Color.TRANSPARENT


        val sliderDataArrayList = ArrayList<SliderData>()

        sliderDataArrayList.add(SliderData(R.drawable.ic_access_1))
        sliderDataArrayList.add(SliderData(R.drawable.ic_access_2))
        sliderDataArrayList.add(SliderData(R.drawable.ic_access_3))

        // passing this array list inside our adapter class.
        val adapter = SliderAdapter(this, sliderDataArrayList)

        binding.slider.isUserInputEnabled = false
        binding.slider.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                lifecycleScope.launch(Dispatchers.Main) {
                    when (position) {
                        0 -> {
                            binding.dot.setImageResource(R.drawable.ic_per_dot_1)
                            binding.container1.show()
                            binding.container2.hide()
                            binding.container3.hide()
                        }

                        1 -> {
                            binding.dot.setImageResource(R.drawable.ic_per_dot_2)
                            binding.container1.hide()
                            binding.container2.show()
                            binding.container3.hide()
                        }

                        2 -> {
                            binding.dot.setImageResource(R.drawable.ic_per_dot_3)
                            binding.container1.hide()
                            binding.container2.hide()
                            binding.container3.show()
                        }
                    }
                }
            }
        })

        binding.slider.adapter = adapter


        binding.btnCheck.clickWithDebounce {
            isGranted = !isGranted
            if (isGranted) {
                binding.btnCheck.setImageResource(R.drawable.ic_check_active_app)
            } else {
                binding.btnCheck.setImageResource(R.drawable.ic_check_inactive_app)
            }
        }
//
//        binding.btnCancel.clickWithDebounce {
//            finish()
//        }

        binding.btnContinue.clickWithDebounce {
            if (binding.slider.currentItem == 0) {
                if (!MyAccessService.isRunning) {
                    if (isGranted) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        activityAccessibilityLauncher.launch(intent)
                    } else {
                        Toast.makeText(this, getString(R.string.agreement), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding.slider.currentItem = 1

                }
                return@clickWithDebounce
            }

            if (binding.slider.currentItem == 1) {
                if (!isNotificationServiceEnabled()) {
                    requestNotificationPermission()
                } else {
                    binding.slider.currentItem = 2
                }
                return@clickWithDebounce
            }

            if (binding.slider.currentItem == 2) {
                val powerManager = getSystemService(POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    Log.d("TAG", "initView: ")
                    requestIgnoreBatteryOptimization()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                return@clickWithDebounce
            }

        }
    }

    private val notificationListenerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermission()
        }

    private val batteryOptimizationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermission()
        }

    private val activityAccessibilityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermission()
        }

    private fun checkPermission() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (!MyAccessService.isRunning) {
            binding.slider.currentItem = 0
        } else {
            if (!isNotificationServiceEnabled()) {
                binding.slider.currentItem = 1
            } else {
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    binding.slider.currentItem = 2
                } else {
//                    startActivity(Intent(this, GetStartedActivity::class.java))
//                    finish()
                    binding.slider.currentItem = 2
                }
            }
        }
    }

    private fun requestIgnoreBatteryOptimization() {
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = "package:${packageName}".toUri()
        batteryOptimizationLauncher.launch(intent)
    }

    private fun requestNotificationPermission() {
        val intent = Intent()
        intent.action = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
        if (buildMinVersion26()) {
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        notificationListenerLauncher.launch(intent)
    }

    fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, NotificationListener::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat?.contains(cn.flattenToString()) == true
    }

}