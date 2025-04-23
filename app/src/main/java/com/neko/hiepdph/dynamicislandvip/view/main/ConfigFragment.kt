package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentConfigBinding
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogAnimation
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogClickMode
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogColorAnimation
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogDisplayMode
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
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
                requireContext(),
                requireActivity().config.notificationAnimation,
                onGrant = {
                    requireActivity().config.notificationAnimation = it
                    binding.containerNotificationAnimation.setContent(
                        when (it) {
                            0 -> requireActivity().getString(R.string.neon)
                            1 -> requireActivity().getString(R.string.shake)
                            else -> ""
                        }
                    )
                })
            dialogAnimation.show()
        }

        binding.containerDisplay.setListener {
            val dialogDisplayMode = DialogDisplayMode(
                requireContext(),
                requireActivity().config.displayMode,
                onClickGrant = {
                    requireActivity().config.displayMode = it
                    binding.containerDisplay.setContent(
                        when (it) {
                            0 -> requireActivity().getString(R.string.always)
                            1 -> requireActivity().getString(R.string.show_when_having_notification)
                            else -> ""
                        }
                    )
                })
            dialogDisplayMode.show()
        }

        binding.containerColor.setListener {
            val dialogColorAnimation = DialogColorAnimation(requireContext(),requireActivity().config.animColor,requireActivity().config.alphaValueAnim, onClickGrant = {}, onClickCancel = {})
            dialogColorAnimation.show()
        }

        binding.containerClickMode.setListener {
            val dialogClickMode = DialogClickMode(
                requireContext(),
                requireActivity().config.clickMode,
                onClickGrant = {
                    requireActivity().config.clickMode = it
                    binding.containerDisplay.setContent(
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