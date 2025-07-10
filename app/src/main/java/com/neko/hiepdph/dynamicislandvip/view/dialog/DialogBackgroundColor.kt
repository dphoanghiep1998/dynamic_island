package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.databinding.DialogBackgroundColorBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogBackgroundColor(
    context: Context,
    var initColor: String,
    var initAlpha: Float,
    val onClickGrant: (String) -> Unit,
    val onClickCancel: () -> Unit
) : BaseDialog<DialogBackgroundColorBinding>(context, canHide = false) {
    private var currentColor: String = "#000000"
    private var currentAlpha: Float = 1f
    private var adapterColors: AdapterColors? = null
    private var listColors = mutableListOf<String>()
    override fun getViewBinding(): DialogBackgroundColorBinding {
        return DialogBackgroundColorBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12)
    }

    override fun initView() {
        listColors = context.config.listBackgroundColor.toMutableList()
        currentColor = initColor
        currentAlpha = initAlpha

        setColorForView()
        adapterColors = AdapterColors(onClickColor = {
            currentColor = it
            binding.dynamicIsland.backgroundTintList = ColorStateList.valueOf(currentColor.toColorInt())

        }, onClickAdd = {

        })
        binding.rcvColor.adapter = adapterColors
        adapterColors?.setData(listColors)

        val index = listColors.indexOfFirst { it == currentColor }
        adapterColors?.setSelectedIndex(index)
        binding.colorPickerView.alphaSliderView = binding.colorAlphaSlider
        binding.colorPickerView.hueSliderView = binding.hueSlider

        binding.colorPickerView.setOnColorChangeEndListener {
            currentColor = String.format("#%06X", 0xFFFFFF and it)
            currentAlpha = binding.colorAlphaSlider.alphaValue
            binding.dynamicIsland.backgroundTintList = ColorStateList.valueOf(currentColor.toColorInt())
        }

        binding.btnAdd.clickWithDebounce {
            listColors.add(listColors.size, currentColor)
            adapterColors?.setData(listColors)
            binding.rcvColor.post {
                adapterColors?.setSelectedIndex(listColors.size-1)
                binding.rcvColor.scrollToPosition(listColors.size-1)
            }
        }

        binding.btnOk.clickWithDebounce {
            onClickGrant.invoke(currentColor)
            context.config.listBackgroundColor = listColors
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