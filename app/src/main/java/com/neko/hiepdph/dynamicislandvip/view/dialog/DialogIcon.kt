package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.DialogIconBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogIcon(
    context: Context, private val index: Int, private val onClickGrant: (Int) -> Unit
) : BaseDialog<DialogIconBinding>(context) {
    private var currentIndex = 0

    override fun getViewBinding(): DialogIconBinding {
        return DialogIconBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        currentIndex = index
        if (currentIndex == 0) {
            binding.tvAppIcon.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvSmallNotificationIcon.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAppIcon.show()
            binding.tickSmallIcon.hide()
        } else {
            binding.tvSmallNotificationIcon.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvAppIcon.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAppIcon.hide()
            binding.tickSmallIcon.show()
        }
        binding.tvAppIcon.clickWithDebounce {
            binding.tvAppIcon.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvSmallNotificationIcon.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAppIcon.show()
            binding.tickSmallIcon.hide()
            currentIndex = 0
        }

        binding.tvSmallNotificationIcon.clickWithDebounce {
            binding.tvSmallNotificationIcon.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.tvAppIcon.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickAppIcon.hide()
            binding.tickSmallIcon.show()
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