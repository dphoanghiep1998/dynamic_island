package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.DialogClickModeBinding
import com.neko.hiepdph.mypiano.common.base_component.BaseDialog

class DialogClickMode(
    context: Context, private val index: Int, private val onClickGrant: (Int) -> Unit
) : BaseDialog<DialogClickModeBinding>(context) {
    private var currentIndex = 0

    override fun getViewBinding(): DialogClickModeBinding {
        return DialogClickModeBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        currentIndex = index
        if (currentIndex == 0) {
            binding.containerNormalClick.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.containerLongClick.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickNormalClick.show()
            binding.tickLongClick.hide()
        } else {
            binding.containerNormalClick.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.containerLongClick.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickNormalClick.hide()
            binding.tickLongClick.show()
        }
        binding.containerNormalClick.clickWithDebounce {
            binding.containerNormalClick.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.containerLongClick.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickNormalClick.show()
            binding.tickLongClick.hide()
            currentIndex = 0
        }

        binding.containerLongClick.clickWithDebounce {
            binding.containerLongClick.setBackgroundResource(R.drawable.bg_stroke_gradient_8)
            binding.containerNormalClick.setBackgroundResource(R.drawable.bg_s400_8)
            binding.tickNormalClick.hide()
            binding.tickLongClick.show()
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