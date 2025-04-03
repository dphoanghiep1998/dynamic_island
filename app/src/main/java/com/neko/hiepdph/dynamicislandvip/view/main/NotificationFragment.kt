package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentNotificationBinding
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

        binding.containerQuickPlay.clickWithDebounce {
            binding.switchDisplay.isChecked = !requireActivity().config.notificationDisplay
        }
        binding.switchDisplay.clickWithDebounce {
            requireActivity().config.notificationDisplay = binding.switchDisplay.isChecked
        }

        binding.containerHideOnLockScreen.clickWithDebounce {
            binding.switchEnableHideOnLock.isChecked =
                !requireActivity().config.notificationHideOnLockScreen

        }

        binding.switchEnableHideOnLock.clickWithDebounce {
            requireActivity().config.notificationHideOnLockScreen =
                binding.switchEnableHideOnLock.isChecked
        }

        binding.containerShowAction.clickWithDebounce {
            binding.switchEnableShowAction.isChecked =
                !requireActivity().config.notificationShowAction

        }

        binding.switchEnableShowAction.clickWithDebounce {
            requireActivity().config.notificationShowAction =
                binding.switchEnableShowAction.isChecked
        }

        binding.containerApps.clickWithDebounce {
            startActivity(Intent(requireActivity(), SelectAppsActivity::class.java))
        }

        binding.containerBackgroundColor.clickWithDebounce {
            val dialogBackColor = DialogBackgroundColor(requireContext(),
                requireActivity().config.backgroundDynamicColor,
                requireActivity().config.backgroundDynamicAlpha,
                onClickGrant = {
                    requireActivity().config.backgroundDynamicColor = it
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