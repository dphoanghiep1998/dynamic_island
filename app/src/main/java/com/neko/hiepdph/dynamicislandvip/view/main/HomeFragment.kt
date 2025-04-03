package com.neko.hiepdph.dynamicislandvip.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentHomeBinding
import com.neko.hiepdph.dynamicislandvip.view.setting.SettingsActivity
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.btnSetting.clickWithDebounce {
            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
        }
    }

}