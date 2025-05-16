package com.neko.hiepdph.dynamicislandvip.view.showbubble

import android.content.Intent
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.OnSeekChangeListener
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.SeekParams
import com.neko.hiepdph.dynamicislandvip.common.customview.tickseekbar.TickSeekBar
import com.neko.hiepdph.dynamicislandvip.common.launchWhenResumed
import com.neko.hiepdph.dynamicislandvip.databinding.ActivityShowBubblesBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
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
        binding.containerChooseBubbleColorBorder.setColor(config.bubbleBackgroundBorder.toColorInt())
        binding.containerBubbleColorBG.setColor(config.bubbleBackgroundColor.toColorInt())

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
        binding.tvSizeBorderValue.text = config.sizeBorder.toInt().toString()
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
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }
        binding.switchBubbleColor.clickWithDebounce {
            config.showBubbleColor = binding.switchBubbleColor.isChecked
            if (binding.switchBubbleColor.isChecked) {
                binding.containerBubbleBackgroundExpand.expand()
            } else {
                binding.containerBubbleBackgroundExpand.collapse()
            }
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerShowBubble.clickWithDebounce {
            binding.switchEnable.isChecked = !config.showBubble
            config.showBubble = !config.showBubble
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.switchEnable.clickWithDebounce {
            config.showBubble = binding.switchEnable.isChecked
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerBubbleColorBorder.clickWithDebounce {
            config.showBubbleBorder = !config.showBubbleBorder
            binding.switchBubbleColorBorder.isChecked = config.showBubbleBorder
            if (binding.switchBubbleColorBorder.isChecked) {
                binding.containerExpandBorder.expand()
            } else {
                binding.containerExpandBorder.collapse()
            }
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }
        binding.switchBubbleColorBorder.clickWithDebounce {
            config.showBubbleBorder = binding.switchBubbleColorBorder.isChecked
            if (binding.switchBubbleColorBorder.isChecked) {
                binding.containerExpandBorder.expand()
            } else {
                binding.containerExpandBorder.collapse()
            }
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerRight.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_active)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleLocation = 1
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )

        }
        binding.tickOnTheRight.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_active)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleLocation = 1
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerLeft.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_active)
            config.bubbleLocation = 0
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.tickOnTheLeft.clickWithDebounce {
            binding.tickOnTheRight.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickOnTheLeft.setImageResource(R.drawable.ic_tick_active)
            config.bubbleLocation = 0
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerShowWhenHasNotification.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_active)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleFrequency = 0
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.tickShowWhenHasNotification.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_active)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_inactive)
            config.bubbleFrequency = 0
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerAlwaysShow.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_active)
            config.bubbleFrequency = 1
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.tickAlwaysShow.clickWithDebounce {
            binding.tickShowWhenHasNotification.setImageResource(R.drawable.ic_tick_inactive)
            binding.tickAlwaysShow.setImageResource(R.drawable.ic_tick_active)
            config.bubbleFrequency = 1
            startService(
                Intent(
                    this, MyAccessService::class.java
                ).setAction(Constant.UPDATE_BUBBLE)
            )
        }

        binding.containerChooseBubbleColorBorder.clickWithDebounce {
            val dialogBubbleColorBorder = DialogBubbleColorBorder(this,
                config.bubbleBackgroundBorder,
                config.alphaValueBubbleBorder,
                onClickGrant = {
                    config.bubbleBackgroundBorder = it
                    binding.containerChooseBubbleColorBorder.setColor(it.toColorInt())

                    startService(
                        Intent(
                            this, MyAccessService::class.java
                        ).setAction(Constant.UPDATE_BUBBLE)
                    )
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
                   binding.containerBubbleColorBG.setColor(it.toColorInt())
                    startService(
                        Intent(
                            this, MyAccessService::class.java
                        ).setAction(Constant.UPDATE_BUBBLE)
                    )
                },
                onClickCancel = {

                })
            dialogBubbleColor.show()
        }

        binding.seekbarChangeSizeBorder.max = 10f
        binding.seekbarChangeSizeBorder.min = 0f
        binding.seekbarChangeSizeBorder.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {

            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
                config.sizeBorder = binding.seekbarChangeSizeBorder.progress
                binding.tvSizeBorderValue.text = binding.seekbarChangeSizeBorder.progress.toFloat().toString()
                startService(
                    Intent(
                        this@ShowBubblesActivity , MyAccessService::class.java
                    ).setAction(Constant.UPDATE_BUBBLE)
                )
            }


        }


    }

}