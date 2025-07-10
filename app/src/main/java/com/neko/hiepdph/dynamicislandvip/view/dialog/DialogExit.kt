package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.DialogExitBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogExit(
    context: Context, private val onCancel: () -> Unit, private val onAccept: () -> Unit
) : BaseDialog<DialogExitBinding>(context) {
    override fun getViewBinding(): DialogExitBinding {
        return DialogExitBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        binding.btnBack.clickWithDebounce {
            onAccept.invoke()
            dismiss()
        }

        binding.btnCancel.clickWithDebounce {
            onCancel.invoke()
            dismiss()
        }
    }
}