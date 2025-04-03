package com.neko.hiepdph.dynamicislandvip.common.customview.itemmodule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemModuleBinding

class ItemModule @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding: LayoutItemModuleBinding


    init {
        binding = LayoutItemModuleBinding.inflate(LayoutInflater.from(context), null, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemModule)
        val isShowArrow = typedArray.getBoolean(R.styleable.ItemModule_isShowArrow, false)
        val isShowSwitch = typedArray.getBoolean(R.styleable.ItemModule_isShowSwitch, false)
        val imageRes = typedArray.getResourceId(R.styleable.ItemModule_imageRes, 0)
        val title = typedArray.getText(R.styleable.ItemModule_titleItem)
        binding.icon.setImageResource(imageRes)
        binding.title.text = title


        if (isShowArrow) {
            binding.arrow.show()
        } else {
            binding.arrow.hide()
        }

        if (isShowSwitch) {
            binding.switchChange.show()
        } else {
            binding.switchChange.hide()
        }
        addView(binding.root)
        typedArray.recycle()
    }

    fun setListener(action:()->Unit){
        binding.root.setOnClickListener {
            action.invoke()
        }
    }

    fun setStatusSwitch(isChecked:Boolean){
        binding.switchChange.isChecked = isChecked
    }

    fun setChangeSwitchListener(action:(Boolean)->Unit){
        binding.switchChange.clickWithDebounce {
            action.invoke( binding.switchChange.isChecked)
        }
    }
}