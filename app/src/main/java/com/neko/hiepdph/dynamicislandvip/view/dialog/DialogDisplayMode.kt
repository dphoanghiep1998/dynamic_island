package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.DialogDisplayModeBinding
import com.neko.hiepdph.mypiano.common.base_component.BaseDialog

class DialogDisplayMode(
    context: Context, private val index: Int, private val onClickGrant: (Int) -> Unit
) : BaseDialog<DialogDisplayModeBinding>(context) {
    private var currentIndex = 0

    override fun getViewBinding(): DialogDisplayModeBinding {
        return DialogDisplayModeBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        currentIndex = index
        if (currentIndex == 0) {
            binding.tvAlways.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvShowWhenHavingNotification.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAlways.show()
            binding.tickShowWhenHavingNotification.hide()
        } else {
            binding.tvShowWhenHavingNotification.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvAlways.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAlways.hide()
            binding.tickShowWhenHavingNotification.show()
        }
        binding.tvAlways.clickWithDebounce {
            binding.tvAlways.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvShowWhenHavingNotification.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAlways.show()
            binding.tickShowWhenHavingNotification.hide()
            currentIndex = 0
        }

        binding.tvShowWhenHavingNotification.clickWithDebounce {
            binding.tvShowWhenHavingNotification.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvAlways.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAlways.hide()
            binding.tickShowWhenHavingNotification.show()
            currentIndex = 1
        }

        binding.btnOk.clickWithDebounce {
            onClickGrant.invoke(currentIndex)
            dismiss()
        }

        binding.btnClose.clickWithDebounce {
            dismiss()
        }
    }
}