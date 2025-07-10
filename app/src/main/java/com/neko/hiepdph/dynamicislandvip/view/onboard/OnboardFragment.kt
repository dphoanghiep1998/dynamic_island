package com.neko.hiepdph.dynamicislandvip.view.onboard

import android.view.LayoutInflater
import android.view.ViewGroup
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseFragment
import com.neko.hiepdph.dynamicislandvip.databinding.FragmentOnboardBinding


class OnboardFragment : BaseFragment<FragmentOnboardBinding>() {
    var position = 0
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOnboardBinding {
        return FragmentOnboardBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        val listBg =
            mutableListOf(R.drawable.bg_intro_1, R.drawable.bg_intro_2, R.drawable.bg_intro_3)

        val listImg =
            mutableListOf(R.drawable.ic_intro_1, R.drawable.ic_intro_2, R.drawable.ic_intro_3)

        val listTitle =
            mutableListOf(R.string.intro_title_1, R.string.intro_title_2, R.string.intro_title_3)
        val listContent = mutableListOf(
            R.string.intro_content_1, R.string.intro_content_2, R.string.intro_content_3
        )

        binding.title.text = requireActivity().getString(listTitle[position])
        binding.content.text = requireActivity().getString(listContent[position])
        binding.root.setBackgroundResource(listBg[position])
        binding.image.setImageResource(listImg[position])
    }


    companion object {
        @JvmStatic
        fun newInstance(pos: Int) = OnboardFragment().apply {
            position = pos
        }
    }
}