package com.neko.hiepdph.dynamicislandvip.view.showbubble

import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.OnSeekChangeListener
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.SeekParams
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.TickSeekBar
import com.neko.hiepdph.dynamicislandvip.common.launchWhenResumed
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityShowBubblesBinding
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogBubbleColor
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogBubbleColorBorder
import com.neko.hiepdph.mypiano.common.base_component.BaseActivity

class ShowBubblesActivity : BaseActivity<ActivityShowBubblesBinding>() {
    override fun getViewBinding(): ActivityShowBubblesBinding {
        return ActivityShowBubblesBinding.inflate(layoutInflater)
    }

    override fun initView() {
        lifecycleScope.launchWhenResumed {
            binding.switchBubbleColor.isChecked = config.showBubbleColor
            binding.switchBubbleColorBorder.isChecked = config.showBubbleBorder
            binding.switchEnable.isChecked = config.showBubble
        }

        if (config.bubbleFrequency == 0) {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_active)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_inactive)
        } else {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_active)
        }


        if (config.bubbleLocation == 0) {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_active)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_inactive)
        } else {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_active)
        }
        val arrayFilter = arrayOf(
            getString(R.string.b_3_sec),
            getString(R.string.b_10_sec),
            getString(R.string.b_30_sec),
            getString(R.string.b_1_min),
            getString(R.string.b_5_min),
            getString(R.string.b_10_min),
        )
        launchWhenResumed {
            binding.filterCondition.setText(arrayFilter[config.bubbleDisplayTime])
            binding.filterCondition.setAdapter(
                AdapterAutoCompleteTextView(
                    this@ShowBubblesActivity,
                    R.layout.layout_autocomplete_custom,
                    R.id.tvFilter,
                    arrayFilter.toList()
                )
            )
            binding.filterCondition.setOnItemClickListener { parent, view, position, id ->
                config.bubbleDisplayTime = position
//                filterHeartRate(position)
            }
//            filterHeartRate(filterIndex)
        }
        binding.tvSizeBorderValue.text = config.sizeBorder.toString()
        binding.seekbarChangeSizeBorder.setProgress(config.sizeBorder.toFloat())

        if (config.showBubbleBorder) {
            binding.containerExpandBorder.expand()
        } else {
            binding.containerExpandBorder.collapse()
        }


        if (config.showBubbleColor) {
            binding.containerBubbleBackgroundExpand.expand()
        } else {
            binding.containerBubbleBackgroundExpand.collapse()
        }


    }

    override fun initButton() {
        binding.btnBack.clickWithDebounce {
            finish()
        }
        binding.containerBubbleColor.clickWithDebounce {
            config.showBubbleColor = !config.showBubbleColor
            binding.switchBubbleColor.isChecked = config.showBubbleColor
            if (binding.switchBubbleColor.isChecked) {
                binding.containerBubbleBackgroundExpand.expand()
            } else {
                binding.containerBubbleBackgroundExpand.collapse()
            }
        }
        binding.switchBubbleColor.clickWithDebounce {
            config.showBubbleColor = binding.switchBubbleColor.isChecked
            if (binding.switchBubbleColor.isChecked) {
                binding.containerBubbleBackgroundExpand.expand()
            } else {
                binding.containerBubbleBackgroundExpand.collapse()
            }
        }

        binding.containerShowBubble.clickWithDebounce {
            binding.switchEnable.isChecked = !config.showBubble
            config.showBubble = !config.showBubble
        }

        binding.switchEnable.clickWithDebounce {
            config.showBubble = binding.switchEnable.isChecked
        }

        binding.containerBubbleColorBorder.clickWithDebounce {
            config.showBubbleBorder = !config.showBubbleBorder
            binding.switchBubbleColorBorder.isChecked = config.showBubbleBorder
            if (binding.switchBubbleColorBorder.isChecked) {
                binding.containerExpandBorder.expand()
            } else {
                binding.containerExpandBorder.collapse()
            }
        }
        binding.switchBubbleColorBorder.clickWithDebounce {
            config.showBubbleBorder = binding.switchBubbleColorBorder.isChecked
            if (binding.switchBubbleColorBorder.isChecked) {
                binding.containerExpandBorder.expand()
            } else {
                binding.containerExpandBorder.collapse()
            }
        }

        binding.containerRight.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_active)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleLocation = 0

        }
        binding.tickOnTheRight.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_active)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleLocation = 0
        }

        binding.containerLeft.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_active)
            config.bubbleLocation = 1
        }

        binding.tickOnTheLeft.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_active)
            config.bubbleLocation = 1
        }

        binding.containerShowWhenHasNotification.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_active)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleFrequency = 0
        }

        binding.tickShowWhenHasNotification.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_active)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleFrequency = 0
        }

        binding.containerAlwaysShow.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_active)
            config.bubbleFrequency = 1
        }

        binding.tickAlwaysShow.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_active)
            config.bubbleFrequency = 1
        }

        binding.containerChooseBubbleColorBorder.clickWithDebounce {
            val dialogBubbleColorBorder = DialogBubbleColorBorder(this,
                config.bubbleBackgroundBorder,
                config.alphaValueBubbleBorder,
                onClickGrant = {
                    config.bubbleBackgroundBorder = it
                },
                onClickCancel = {

                })
            dialogBubbleColorBorder.show()
        }

        binding.containerBubbleColorBG.clickWithDebounce {
            val dialogBubbleColor = DialogBubbleColor(this,
                config.bubbleBackgroundColor,
                config.alphaValueBubbleBackground,
                onClickGrant = {
                    config.bubbleBackgroundColor = it
                },
                onClickCancel = {

                })
            dialogBubbleColor.show()
        }


        binding.seekbarChangeSizeBorder.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {

            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                config.sizeBorder = binding.seekbarChangeSizeBorder.progress
                binding.tvSizeBorderValue.text = binding.seekbarChangeSizeBorder.progress.toFloat().toString()
            }


        }


    }

}