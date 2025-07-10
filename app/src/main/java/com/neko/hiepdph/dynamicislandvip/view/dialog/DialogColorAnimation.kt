package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import androidx.core.graphics.toColorInt
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.DialogColorAnimationBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogColorAnimation(
    context: Context,
    var initColor: String,
    var initAlpha:Float ,
    val onClickGrant: (String) -> Unit,
    val onClickCancel: () -> Unit
) : BaseDialog<DialogColorAnimationBinding>(context, canHide = false) {
    private var currentColor: String = "#000000"
    private var currentAlpha: Float = 1f
    private var adapterColors: AdapterColors? = null
    private var listColors = mutableListOf<String>()
    override fun getViewBinding(): DialogColorAnimationBinding {
        return DialogColorAnimationBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        listColors = context.config.listAnimColor.toMutableList()
        currentColor = initColor
        currentAlpha = initAlpha
        setColorForView()
        adapterColors = AdapterColors(onClickColor = {
            currentColor = it
        }, onClickAdd = {

        })
        binding.rcvColor.adapter = adapterColors
        adapterColors?.setData(listColors)
        binding.colorPickerView.alphaSliderView = binding.colorAlphaSlider
        binding.colorPickerView.hueSliderView = binding.hueSlider
        binding.colorPickerView.setOnColorChangeEndListener {
            currentColor = String.format("#%06X", 0xFFFFFF and it)
            currentAlpha = binding.colorAlphaSlider.alphaValue

        }
        val index = listColors.indexOfFirst { it == currentColor }
        adapterColors?.setSelectedIndex(index)
        binding.colorPickerView.color = currentColor.toColorInt()

        binding.btnOk.clickWithDebounce {
            onClickGrant.invoke(currentColor)
            context.config.listAnimColor = listColors
            dismiss()
        }

        binding.btnCancel.clickWithDebounce {
            onClickCancel.invoke()
            dismiss()
        }
        binding.btnAdd.clickWithDebounce {
            listColors.add(listColors.size, currentColor)
            adapterColors?.setData(listColors)
            binding.rcvColor.post {
                adapterColors?.setSelectedIndex(listColors.size-1)
                binding.rcvColor.scrollToPosition(listColors.size-1)
            }
        }
    }

    private fun setColorForView() {
        binding.colorPickerView.color = currentColor.toColorInt()
        binding.colorAlphaSlider.alphaValue = currentAlpha
    }
}