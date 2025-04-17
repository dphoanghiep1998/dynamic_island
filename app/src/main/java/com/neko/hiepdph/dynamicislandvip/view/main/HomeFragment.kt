package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.OnSeekChangeListener
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.SeekParams
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.TickSeekBar
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentHomeBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
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

    private fun setupView() {
        changeNotchStyle()
        binding.seekbarVertical.apply {
            max =
                requireActivity().resources.displayMetrics.heightPixels / context.resources.displayMetrics.scaledDensity - context.config.dynamicHeight * context.resources.displayMetrics.scaledDensity
            setProgress(requireActivity().config.dynamicMarginVertical.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                    binding.tvVerticalValue.text = seekBar?.progress.toString()
                    requireActivity().config.dynamicMarginVertical =
                        binding.seekbarVertical.progress
                    updateDynamicView()

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

                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                    binding.tvHorizontalValue.text = seekBar?.progress.toString()
                    requireActivity().config.dynamicMarginHorizontal =
                        binding.seekbarHorizontal.progress
                    updateDynamicView()

                }


            }
        }
        binding.tvHorizontalValue.text = requireActivity().config.dynamicMarginHorizontal.toString()

        binding.seekbarWidth.apply {
            max =
                requireActivity().resources.displayMetrics.widthPixels.toFloat() / requireActivity().resources.displayMetrics.scaledDensity
            setProgress(requireActivity().config.dynamicWidth.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                    binding.tvWidthValue.text = seekBar?.progress.toString()
                    requireActivity().config.dynamicWidth = binding.seekbarWidth.progress
                    updateDynamicView()

                }


            }
        }
        binding.tvWidthValue.text = requireActivity().config.dynamicWidth.toString()

        binding.seekbarHeight.apply {
            max = 40f
            min = 0f
            setProgress(requireActivity().config.dynamicHeight.toFloat())
            onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                    binding.tvHeightValue.text = seekBar?.progress.toString()
                    requireActivity().config.dynamicHeight = binding.seekbarHeight.progress
                    updateDynamicView()

                }


            }
        }
        binding.tvHeightValue.text = requireActivity().config.dynamicHeight.toString()
    }

    private fun initButton() {
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

    }

    private fun changeHeight(amount: Int) {
        binding.seekbarHeight.setProgress((binding.seekbarHeight.progress + amount).toFloat())
        binding.tvHeightValue.text = binding.seekbarHeight.progress.toString()
        requireActivity().config.dynamicHeight = binding.seekbarHeight.progress
        updateDynamicView()

    }

    private fun changeNotchStyle() {
        if (requireActivity().config.notchStyle == 0) {
            binding.btnTickDynamicIsland.setImageResource(R.drawable.ic_tick_active)
            binding.btnTickNotch.setImageResource(R.drawable.ic_tick_inactive)
            binding.btnDynamicIsland.setImageResource(R.drawable.ic_dynamic_active)
            binding.btnNotch.setImageResource(R.drawable.ic_dynamic_inactive)
        } else {
            binding.btnDynamicIsland.setImageResource(R.drawable.ic_dynamic_inactive)
            binding.btnNotch.setImageResource(R.drawable.ic_dynamic_active)
            binding.btnTickDynamicIsland.setImageResource(R.drawable.ic_tick_inactive)
            binding.btnTickNotch.setImageResource(R.drawable.ic_tick_active)
        }
    }

    private fun updateDynamicView() {
        requireActivity().startService(
            Intent(
                requireActivity(), MyAccessService::class.java
            ).setAction(Constant.UPDATE_LAYOUT_SIZE)
        )
    }

    private fun reCalculate() {

    }

}