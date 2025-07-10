package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.OnSeekChangeListener
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.SeekParams
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.TickSeekBar
import com.neko.hiepdph.dynamicislandvip.common.isAccessibilityServiceRunning
import com.neko.hiepdph.dynamicislandvip.common.toPx
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentHomeBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.accesspermisison.AccessibilityPermissionActivity
import com.neko.hiepdph.dynamicislandvip.view.setting.SettingsActivity


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        setupView()
        initButton()
    }

    override fun onResume() {
        super.onResume()
        if (!requireActivity().config.controlEnable) {
            binding.switchEnable.isChecked = false
        } else {
            binding.switchEnable.isChecked = true
        }
    }


    private fun setupView() {
        changeNotchStyle()
        val point = Point()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getRealSize(point)
        binding.seekbarVertical.apply {
            max = (point.y - context.config.dynamicHeight).toFloat()
            setProgress(requireActivity().config.dynamicMarginVertical.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    binding.tvVerticalValue.text = seekParams?.progress.toString()
                    requireActivity().config.dynamicMarginVertical =
                        binding.seekbarVertical.progress
                    updateDynamicView()
                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {


                }


            }
        }
        binding.tvVerticalValue.text = requireActivity().config.dynamicMarginVertical.toString()


        binding.seekbarHorizontal.apply {
            min = 0f
            max =
                (requireActivity().resources.displayMetrics.widthPixels - context.config.dynamicWidth).toFloat()
            setProgress(requireActivity().config.dynamicMarginHorizontal.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    binding.tvHorizontalValue.text = seekParams?.progress.toString()
                    requireActivity().config.dynamicMarginHorizontal =
                        binding.seekbarHorizontal.progress
                    updateDynamicView()
                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {


                }


            }
        }
        binding.tvHorizontalValue.text = requireActivity().config.dynamicMarginHorizontal.toString()

        binding.seekbarWidth.apply {
            max = requireActivity().toPx(200)
            min = requireActivity().toPx(120)
            setProgress(requireActivity().config.dynamicWidth.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    binding.tvWidthValue.text = seekParams?.progress.toString()
                    requireActivity().config.dynamicWidth = binding.seekbarWidth.progress
                    reCalculate()
                    updateDynamicView()

                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {


                }


            }
        }
        binding.tvWidthValue.text = requireActivity().config.dynamicWidth.toString()

        binding.seekbarHeight.apply {
            max = requireActivity().toPx(40)
            min = requireActivity().toPx(20)
            setProgress(requireActivity().config.dynamicHeight.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    binding.tvHeightValue.text = seekParams?.progress.toString()
                    requireActivity().config.dynamicHeight = binding.seekbarHeight.progress
                    updateDynamicView()
                    reCalculate()
                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {


                }


            }
        }
        binding.tvHeightValue.text = requireActivity().config.dynamicHeight.toString()
    }

    private fun initButton() {
        binding.switchEnable.clickWithDebounce {
            if (MyAccessService.isRunning) {
                requireActivity().config.controlEnable = !requireActivity().config.controlEnable
                binding.switchEnable.isChecked = requireActivity().config.controlEnable
                requireActivity().startService(
                    Intent(
                        requireActivity(), MyAccessService::class.java
                    ).setAction(Constant.TOGGLE_CONTROL)
                )
            } else {
                binding.switchEnable.isChecked = false
                startActivity(
                    Intent(
                        requireActivity(), AccessibilityPermissionActivity::class.java
                    )
                )
            }
        }
        binding.containerEnableDynamicIsland.clickWithDebounce {
            if (MyAccessService.isRunning) {
                requireActivity().config.controlEnable = !requireActivity().config.controlEnable
                binding.switchEnable.isChecked = requireActivity().config.controlEnable

                requireActivity().startService(
                    Intent(
                        requireActivity(), MyAccessService::class.java
                    ).setAction(Constant.TOGGLE_CONTROL)
                )
            } else {
                startActivity(
                    Intent(
                        requireActivity(), AccessibilityPermissionActivity::class.java
                    )
                )
            }
        }

        binding.btnSetting.clickWithDebounce {
            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
        }
        binding.btnDynamicIsland.clickWithDebounce {
            requireActivity().config.notchStyle = 0
            changeNotchStyle()
        }
        binding.btnNotch.clickWithDebounce {
            requireActivity().config.notchStyle = 1
            changeNotchStyle()

        }
        binding.btnTickDynamicIsland.clickWithDebounce {
            requireActivity().config.notchStyle = 0
            changeNotchStyle()
        }
        binding.btnTickNotch.clickWithDebounce {
            requireActivity().config.notchStyle = 1
            changeNotchStyle()

        }

        binding.btnReset.clickWithDebounce {
            reset()
        }


        binding.btnWidthUp.clickWithDebounce {
            changeWidth(1)
        }

        binding.btnWidthDown.clickWithDebounce {
            changeWidth(-1)
        }

        binding.btnHorizontalUp.clickWithDebounce {
            changeHorizontal(1)
        }
        binding.btnHorizontalDown.clickWithDebounce {
            changeHorizontal(-1)
        }

        binding.btnVerticalUp.clickWithDebounce {
            changeVertical(1)
        }
        binding.btnVerticalDown.clickWithDebounce {
            changeVertical(-1)
        }

        binding.btnHeightUp.clickWithDebounce {
            changeHeight(1)
        }

        binding.btnHeightDown.clickWithDebounce {
            changeHeight(-1)

        }
    }

    private fun reset() {
        requireActivity().config.dynamicWidth = requireActivity().toPx(120).toInt()
        requireActivity().config.dynamicHeight = requireActivity().toPx(20).toInt()
        requireActivity().config.notchStyle = 0
        requireActivity().config.dynamicMarginVertical = 0
        requireActivity().config.dynamicMarginHorizontal =
            requireActivity().resources.displayMetrics.widthPixels / 2 - requireActivity().config.dynamicWidth / 2

        initView()

    }

    private fun changeVertical(amount: Int) {
        binding.seekbarVertical.setProgress((binding.seekbarVertical.progress + amount).toFloat())
        binding.tvVerticalValue.text = binding.seekbarVertical.progress.toString()
        requireActivity().config.dynamicMarginVertical = binding.seekbarVertical.progress
        updateDynamicView()
    }

    private fun changeHorizontal(amount: Int) {
        binding.seekbarHorizontal.setProgress((binding.seekbarHorizontal.progress + amount).toFloat())
        binding.tvHorizontalValue.text = binding.seekbarHorizontal.progress.toString()
        requireActivity().config.dynamicMarginHorizontal = binding.seekbarHorizontal.progress
        updateDynamicView()
    }

    private fun changeWidth(amount: Int) {
        binding.seekbarWidth.setProgress((binding.seekbarWidth.progress + amount).toFloat())
        binding.tvWidthValue.text = binding.seekbarWidth.progress.toString()
        requireActivity().config.dynamicWidth = binding.seekbarWidth.progress
        updateDynamicView()
        reCalculate()

    }

    private fun changeHeight(amount: Int) {
        binding.seekbarHeight.setProgress((binding.seekbarHeight.progress + amount).toFloat())
        binding.tvHeightValue.text = binding.seekbarHeight.progress.toString()
        requireActivity().config.dynamicHeight = binding.seekbarHeight.progress
        updateDynamicView()
        reCalculate()

    }

    private fun changeNotchStyle() {
        if (requireActivity().config.notchStyle == 0) {
            binding.btnTickDynamicIsland.setImageResource(R.drawable.ic_tick_active)
            binding.btnTickNotch.setImageResource(R.drawable.ic_tick_inactive)
            binding.btnDynamicIsland.setImageResource(R.drawable.ic_dynamic_active)
            binding.btnNotch.setImageResource(R.drawable.ic_notch_inactive)
        } else {
            binding.btnDynamicIsland.setImageResource(R.drawable.ic_dynamic_inactive)
            binding.btnNotch.setImageResource(R.drawable.ic_notch_active)
            binding.btnTickDynamicIsland.setImageResource(R.drawable.ic_tick_inactive)
            binding.btnTickNotch.setImageResource(R.drawable.ic_tick_active)
        }
        requireActivity().startService(
            Intent(
                requireActivity(), MyAccessService::class.java
            ).setAction(Constant.UPDATE_NOTCH_STYLE)
        )
    }

    private fun updateDynamicView() {
        if (requireActivity().isAccessibilityServiceRunning(MyAccessService::class.java))
            requireActivity().startService(
                Intent(
                    requireActivity(), MyAccessService::class.java
                ).setAction(Constant.UPDATE_LAYOUT_SIZE)
            )
    }

    private fun reCalculate() {
        val point = Point()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getRealSize(point)
        binding.seekbarVertical.apply {
            max = (point.y - context.config.dynamicHeight).toFloat()
        }
        binding.seekbarHorizontal.apply {
            min = 0f
            max =
                (requireActivity().resources.displayMetrics.widthPixels - context.config.dynamicWidth).toFloat()
        }
    }

}