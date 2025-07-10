package com.neko.hiepdph.dynamicislandvip.view.main

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.isAccessibilityServiceRunning
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentConfigBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogAnimation
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogClickMode
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogColorAnimation
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogDisplayMode
import com.neko.hiepdph.dynamicislandvip.view.showbubble.ShowBubblesActivity


class ConfigFragment : BaseFragment<FragmentConfigBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentConfigBinding {
        return FragmentConfigBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        setupView()
        initButton()
    }

    private fun setupView() {
        binding.containerAdjustVolume.setStatusSwitch(requireActivity().config.adjustVolume)
        binding.containerAdjustVolume.setListener {
            requireActivity().config.adjustVolume = !requireActivity().config.adjustVolume
            binding.containerAdjustVolume.setStatusSwitch(requireActivity().config.adjustVolume)
            requireActivity().startService(
                Intent(requireActivity(), MyAccessService::class.java).setAction(
                    Constant.UPDATE_ADJUST_VOLUME
                )
            )
        }
        binding.containerAdjustVolume.setChangeSwitchListener {
            requireActivity().config.adjustVolume = it
            requireActivity().startService(
                Intent(requireActivity(), MyAccessService::class.java).setAction(
                    Constant.UPDATE_ADJUST_VOLUME
                )
            )
        }

        binding.containerVibration.setStatusSwitch(requireActivity().config.vibration)
        binding.containerVibration.setListener {
            requireActivity().config.vibration = !requireActivity().config.vibration
            binding.containerVibration.setStatusSwitch(requireActivity().config.vibration)
            requireActivity().startService(
                Intent(requireActivity(), MyAccessService::class.java).setAction(
                    Constant.UPDATE_ADJUST_VIBRATION
                )
            )
        }
        binding.containerVibration.setChangeSwitchListener {
            requireActivity().config.vibration = it
            requireActivity().startService(
                Intent(requireActivity(), MyAccessService::class.java).setAction(
                    Constant.UPDATE_ADJUST_VIBRATION
                )
            )

        }


        binding.containerColor.setColor(requireActivity().config.animColor.toColorInt())

        binding.containerNotificationAnimation.setContent(
            when (requireActivity().config.notificationAnimation) {
                0 -> requireActivity().getString(R.string.neon)
                1 -> requireActivity().getString(R.string.shake)
                else -> ""
            }
        )
        binding.containerDisplay.setContent(

            when (requireActivity().config.displayMode) {
                0 -> requireActivity().getString(R.string.always)
                1 -> requireActivity().getString(R.string.show_when_having_notification)
                else -> ""
            }
        )
        binding.containerClickMode.setContent(
            when (requireActivity().config.clickMode) {
                0 -> requireActivity().getString(R.string.normal_click)
                1 -> requireActivity().getString(R.string.long_click)
                else -> ""
            }
        )
    }

    private fun initButton() {
        binding.containerNotificationAnimation.setListener {
            val dialogAnimation = DialogAnimation(
                requireContext(), requireActivity().config.notificationAnimation, onGrant = {
                    requireActivity().config.notificationAnimation = it
                    binding.containerNotificationAnimation.setContent(
                        when (it) {
                            0 -> requireActivity().getString(R.string.neon)
                            1 -> requireActivity().getString(R.string.shake)
                            else -> ""
                        }
                    )
                    if(requireActivity().isAccessibilityServiceRunning(AccessibilityService::class.java)){
                        requireActivity().startService(
                            Intent(requireActivity(), MyAccessService::class.java).setAction(
                                Constant.UPDATE_ANIMATION
                            )
                        )
                    }
                })
            dialogAnimation.show()
        }

        binding.containerDisplay.setListener {
            val dialogDisplayMode = DialogDisplayMode(
                requireContext(), requireActivity().config.displayMode, onClickGrant = {
                    requireActivity().config.displayMode = it
                    binding.containerDisplay.setContent(
                        when (it) {
                            0 -> requireActivity().getString(R.string.always)
                            1 -> requireActivity().getString(R.string.show_when_having_notification)
                            else -> ""
                        }
                    )
                    if(requireActivity().isAccessibilityServiceRunning(AccessibilityService::class.java)){
                        requireActivity().startService(
                            Intent(requireActivity(), MyAccessService::class.java).setAction(
                                Constant.UPDATE_DISPLAY_MODE
                            )
                        )
                    }
                })
            dialogDisplayMode.show()
        }

        binding.containerColor.setListener {
            val dialogColorAnimation = DialogColorAnimation(
                requireContext(),
                requireActivity().config.animColor,
                requireActivity().config.alphaValueAnim,
                onClickGrant = { color ->
                    requireActivity().config.animColor = color
                    binding.containerColor.setColor(color.toColorInt())
                },
                onClickCancel = {})
            dialogColorAnimation.show()
        }

        binding.containerClickMode.setListener {
            val dialogClickMode = DialogClickMode(
                requireContext(), requireActivity().config.clickMode, onClickGrant = {
                    requireActivity().config.clickMode = it
                    binding.containerClickMode.setContent(
                        when (it) {
                            0 -> requireActivity().getString(R.string.normal_click)
                            1 -> requireActivity().getString(R.string.long_click)
                            else -> ""
                        }
                    )
                })
            dialogClickMode.show()
        }

        binding.containerShowBubble.clickWithDebounce {
            startActivity(Intent(requireActivity(), ShowBubblesActivity::class.java))
        }
    }


}