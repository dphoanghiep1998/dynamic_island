package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.DialogAnimationBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogAnimation(
    context: Context, val index: Int, private val onGrant: (index: Int) -> Unit
) : BaseDialog<DialogAnimationBinding>(context) {
    private var currentIndex = 0
    override fun getViewBinding(): DialogAnimationBinding {
        return DialogAnimationBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        currentIndex = index
        if (currentIndex == 0) {
            binding.imageNeon.setImageResource(R.drawable.ic_neon)
            binding.imageShake.setImageResource(R.drawable.ic_shake)
            binding.containerNeon.setBackgroundResource(R.drawable.bg_button_cancel)
            binding.containerShake.setBackgroundResource(R.drawable.bg_s400_20)
            binding.tickNeon.show()
            binding.tickShake.hide()
        } else {
            binding.imageNeon.setImageResource(R.drawable.ic_neon_off)
            binding.imageShake.setImageResource(R.drawable.ic_shake_on)
            binding.containerShake.setBackgroundResource(R.drawable.bg_button_cancel)
            binding.containerNeon.setBackgroundResource(R.drawable.bg_s400_20)
            binding.tickNeon.hide()
            binding.tickShake.show()
        }


        binding.containerNeon.clickWithDebounce {
            binding.imageNeon.setImageResource(R.drawable.ic_neon)
            binding.imageShake.setImageResource(R.drawable.ic_shake)
            binding.containerNeon.setBackgroundResource(R.drawable.bg_button_cancel)
            binding.containerShake.setBackgroundResource(R.drawable.bg_s400_20)
            binding.tickNeon.show()
            binding.tickShake.hide()
            currentIndex = 0
        }

        binding.containerShake.clickWithDebounce {
            binding.imageNeon.setImageResource(R.drawable.ic_neon_off)
            binding.imageShake.setImageResource(R.drawable.ic_shake_on)
            binding.containerShake.setBackgroundResource(R.drawable.bg_button_cancel)
            binding.containerNeon.setBackgroundResource(R.drawable.bg_s400_20)
            binding.tickNeon.hide()
            binding.tickShake.show()
            currentIndex = 1

        }

        binding.btnClose.clickWithDebounce {
            dismiss()
        }

        binding.btnOk.clickWithDebounce {
            onGrant.invoke(currentIndex)
            dismiss()
        }
    }

}