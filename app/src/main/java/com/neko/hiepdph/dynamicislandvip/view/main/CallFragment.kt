package com.neko.hiepdph.dynamicislandvip.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentCallBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment


class CallFragment : BaseFragment<FragmentCallBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCallBinding {
        return FragmentCallBinding.inflate(inflater,container,false)
    }

    override fun initView() {
        changeBackPressCallBack {
            popTo()
        }
        initButton()
        lifecycleScope.launchWhenResumed {
            binding.switchIncomingCall.isChecked = requireActivity().config.incomingCall
            binding.switchCallTimer.isChecked = requireActivity().config.callTimer
            binding.switchShowDefault.isChecked = requireActivity().config.showDefault
        }


    }

    private fun initButton() {
        binding.containerIncomingCall.clickWithDebounce {
            binding.switchIncomingCall.isChecked = !requireActivity().config.incomingCall
            requireActivity().config.incomingCall = !requireActivity().config.incomingCall

        }

        binding.containerShowDefault.clickWithDebounce {
            binding.switchShowDefault.isChecked = !requireActivity().config.showDefault
            requireActivity().config.showDefault = !requireActivity().config.showDefault
        }


        binding.containerCallTimer.clickWithDebounce {
            binding.switchCallTimer.isChecked = !requireActivity().config.callTimer
            requireActivity().config.callTimer = !requireActivity().config.callTimer
        }

        binding.btnBack.clickWithDebounce {
            popTo()
        }
    }

}