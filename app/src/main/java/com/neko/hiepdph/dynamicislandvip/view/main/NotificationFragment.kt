package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentNotificationBinding
import com.neko.hiepdph.dynamicislandvip.service.MyAccessService
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogBackgroundColor
import com.neko.hiepdph.dynamicislandvip.view.dialog.DialogIcon
import com.neko.hiepdph.dynamicislandvip.view.selectapp.SelectAppsActivity


class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setupView()
        initButton()
    }

    private fun setupView() {
        changeBackPressCallBack {
            popTo()
        }
        binding.containerBackgroundColor.setColor(requireActivity().config.backgroundDynamicColor.toColorInt())
        binding.containerIcon.setContent(
            when (requireActivity().config.notificationIcon) {
                0 -> getString(R.string.app_icon)
                1 -> getString(R.string.small_notification_icon)
                else -> ""
            }
        )
        lifecycleScope.launchWhenResumed {
            binding.switchEnable.isChecked = requireActivity().config.notificationEnable
            binding.switchDisplay.isChecked = requireActivity().config.notificationDisplay
            binding.switchEnableShowAction.isChecked =
                requireActivity().config.notificationShowAction
            binding.switchEnableHideOnLock.isChecked =
                requireActivity().config.notificationHideOnLockScreen

        }
    }

    private fun initButton() {
        binding.containerIcon.setListener {
            val dialogIcon =
                DialogIcon(requireContext(), requireActivity().config.notificationIcon) {
                    requireActivity().config.notificationIcon = it
                    binding.containerIcon.setContent(
                        when (requireActivity().config.notificationIcon) {
                            0 -> getString(R.string.app_icon)
                            1 -> getString(R.string.small_notification_icon)
                            else -> ""
                        }
                    )
                }
            dialogIcon.show()
        }

        binding.switchEnable.clickWithDebounce {
            requireActivity().config.notificationEnable =  binding.switchEnable.isChecked
        }
        binding.containerEnableDynamicIsland.clickWithDebounce {
            requireActivity().config.notificationEnable = !requireActivity().config.notificationEnable
            binding.switchEnable.isChecked = requireActivity().config.notificationEnable
        }

        binding.containerQuickPlay.clickWithDebounce {
            requireActivity().config.notificationDisplay =
                !requireActivity().config.notificationDisplay
            binding.switchDisplay.isChecked = requireActivity().config.notificationDisplay
        }
        binding.switchDisplay.clickWithDebounce {
            requireActivity().config.notificationDisplay = binding.switchDisplay.isChecked
        }

        binding.containerHideOnLockScreen.clickWithDebounce {
            requireActivity().config.notificationHideOnLockScreen =
                !requireActivity().config.notificationHideOnLockScreen
            binding.switchEnableHideOnLock.isChecked =
                requireActivity().config.notificationHideOnLockScreen


        }

        binding.switchEnableHideOnLock.clickWithDebounce {
            requireActivity().config.notificationHideOnLockScreen =
                binding.switchEnableHideOnLock.isChecked

        }

        binding.containerShowAction.clickWithDebounce {
            requireActivity().config.notificationShowAction =
                !requireActivity().config.notificationShowAction
            binding.switchEnableShowAction.isChecked =
                requireActivity().config.notificationShowAction

        }

        binding.switchEnableShowAction.clickWithDebounce {
            requireActivity().config.notificationShowAction =
                !requireActivity().config.notificationShowAction
        }

        binding.containerApps.clickWithDebounce {
            startActivity(Intent(requireActivity(), SelectAppsActivity::class.java))
        }

        binding.containerBackgroundColor.clickWithDebounce {
            val dialogBackColor = DialogBackgroundColor(
                requireContext(),
                requireActivity().config.backgroundDynamicColor,
                requireActivity().config.backgroundDynamicAlpha,
                onClickGrant = {
                    requireActivity().config.backgroundDynamicColor = it
                    binding.containerBackgroundColor.setColor(it.toColorInt())
                    requireActivity().startService(
                        Intent(requireActivity(), MyAccessService::class.java).setAction(
                            Constant.UPDATE_BACKGROUND_COLOR
                        )
                    )
                },
                onClickCancel = {

                })

            dialogBackColor.show()
        }

        binding.btnBack.clickWithDebounce {
            popTo()
        }


    }

}