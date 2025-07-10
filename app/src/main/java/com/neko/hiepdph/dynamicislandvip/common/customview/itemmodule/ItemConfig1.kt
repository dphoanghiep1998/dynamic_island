package com.neko.hiepdph.dynamicislandvip.common.customview.itemmodule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemConfig1Binding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemModuleBinding

class ItemConfig1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding: LayoutItemConfig1Binding


    init {
        binding = LayoutItemConfig1Binding.inflate(LayoutInflater.from(context), null, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemConfig1)
        val imageRes = typedArray.getResourceId(R.styleable.ItemConfig1_imageResConfig, 0)
        val title = typedArray.getText(R.styleable.ItemConfig1_titleItemConfig)
        binding.icon.setImageResource(imageRes)
        binding.title.text = title


        addView(binding.root)
        typedArray.recycle()
    }
    fun setStatusSwitch(isChecked:Boolean){
        binding.switchChange.isChecked = isChecked
    }

    fun setChangeSwitchListener(action:(Boolean)->Unit){
        binding.switchChange.clickWithDebounce {
            action.invoke( binding.switchChange.isChecked)
        }
    }
    fun setListener(action:()->Unit){
        binding.root.setOnClickListener {
            action.invoke()
        }
    }
}