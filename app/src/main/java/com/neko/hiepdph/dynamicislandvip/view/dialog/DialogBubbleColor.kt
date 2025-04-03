package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import android.graphics.Color
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.DialogBubbleColorBinding
import com.neko.hiepdph.dynamicislandvip.databinding.DialogColorAnimationBinding
import com.neko.hiepdph.mypiano.common.base_component.BaseDialog

class DialogBubbleColor(
    context: Context,
    var initColor: String,
    var initAlpha:Float ,
    val onClickGrant: (String) -> Unit,
    val onClickCancel: () -> Unit
) : BaseDialog<DialogBubbleColorBinding>(context, canHide = false) {
    private var currentColor: String = "#000000"
    private var currentAlpha: Float = 1f
    private var adapterColors: AdapterColors? = null
    override fun getViewBinding(): DialogBubbleColorBinding {
        return DialogBubbleColorBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        currentColor = initColor
        currentAlpha = initAlpha
        setColorForView()
        adapterColors = AdapterColors(onClickColor = {
            currentColor = it
        }, onClickAdd = {

        })
        binding.rcvColor.adapter = adapterColors
        adapterColors?.setData(mutableListOf("#EF4444", "#FACC15", "#4ADE80", "#4ADE80", "#4B44BF"))
        binding.colorPickerView.alphaSliderView = binding.colorAlphaSlider
        binding.colorPickerView.hueSliderView = binding.hueSlider

        binding.colorPickerView.color

        binding.btnOk.clickWithDebounce {
            onClickGrant.invoke(currentColor)
            dismiss()
        }

        binding.btnCancel.clickWithDebounce {
            onClickCancel.invoke()
            dismiss()
        }
    }

    private fun setColorForView() {
        binding.colorPickerView.color = Color.parseColor(currentColor)
        binding.colorAlphaSlider.alphaValue = currentAlpha
    }
}