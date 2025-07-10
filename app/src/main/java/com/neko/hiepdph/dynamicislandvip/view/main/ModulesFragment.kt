package com.neko.hiepdph.dynamicislandvip.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentModulesBinding

class ModulesFragment : BaseFragment<FragmentModulesBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentModulesBinding {
        return FragmentModulesBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        setupView()
        initButton()
    }

    private fun setupView() {
        lifecycleScope.launchWhenResumed {
            binding.containerMusic.setStatusSwitch(requireActivity().config.musicEnable)
            binding.containerEarphone.setStatusSwitch(requireActivity().config.earphonesEnable)
            binding.containerBattery.setStatusSwitch(requireActivity().config.batteryChargeEnable)
            binding.containerRinging.setStatusSwitch(requireActivity().config.ringingStateEnable)
            binding.containerDirect.setStatusSwitch(requireActivity().config.directEnable)
            binding.containerCall.setStatusSwitch(requireActivity().config.incomingCall)
        }

    }

    private fun initButton() {
        binding.containerMusic.setChangeSwitchListener {
            requireActivity().config.musicEnable = it
        }
        binding.containerEarphone.setChangeSwitchListener {
            requireActivity().config.earphonesEnable = it
        }
        binding.containerBattery.setChangeSwitchListener {
            requireActivity().config.batteryChargeEnable = it
        }
        binding.containerRinging.setChangeSwitchListener {
            requireActivity().config.ringingStateEnable = it
        }
        binding.containerDirect.setChangeSwitchListener {
            requireActivity().config.directEnable = it
        }


        binding.containerMusic.setListener {
            binding.containerMusic.setStatusSwitch(!requireActivity().config.musicEnable)
            requireActivity().config.musicEnable = !requireActivity().config.musicEnable
        }
        binding.containerEarphone.setListener {
            binding.containerEarphone.setStatusSwitch(!requireActivity().config.earphonesEnable)
            requireActivity().config.earphonesEnable = !requireActivity().config.earphonesEnable
        }
        binding.containerBattery.setListener {
            binding.containerBattery.setStatusSwitch(!requireActivity().config.batteryChargeEnable)
            requireActivity().config.batteryChargeEnable =
                !requireActivity().config.batteryChargeEnable
        }
        binding.containerRinging.setListener {
            binding.containerRinging.setStatusSwitch(!requireActivity().config.ringingStateEnable)
            requireActivity().config.ringingStateEnable =
                !requireActivity().config.ringingStateEnable
        }
        binding.containerDirect.setListener {
            binding.containerDirect.setStatusSwitch(!requireActivity().config.directEnable)
            requireActivity().config.directEnable = !requireActivity().config.directEnable
        }

        binding.containerNotification.setListener {
            (requireActivity() as MainActivity).pushTo(R.id.actionNotification)
        }

        binding.containerCall.setListener {
//            ( requireActivity() as MainActivity).pushTo(R.id.actionCall)
            requireActivity().config.incomingCall = !requireActivity().config.incomingCall
            binding.containerCall.setStatusSwitch(requireActivity().config.incomingCall)
        }

        binding.containerCall.setChangeSwitchListener {
            requireActivity().config.incomingCall = it
        }

    }

}