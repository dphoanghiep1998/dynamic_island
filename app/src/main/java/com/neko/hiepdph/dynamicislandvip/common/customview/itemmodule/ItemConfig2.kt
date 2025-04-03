package com.neko.hiepdph.dynamicislandvip.common.customview.itemmodule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemConfig2Binding

class ItemConfig2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding: LayoutItemConfig2Binding
    init {
        binding = LayoutItemConfig2Binding.inflate(LayoutInflater.from(context), null, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemConfig2)
        val title = typedArray.getText(R.styleable.ItemConfig2_titleItemConfig2)
        val content = typedArray.getText(R.styleable.ItemConfig2_content)
        val isColor = typedArray.getBoolean(R.styleable.ItemConfig2_isColor, false)
        val isShowContent = typedArray.getBoolean(R.styleable.ItemConfig2_isShowContent, false)

        if (isShowContent) {
            binding.content.show()
        } else {
            binding.content.hide()
        }
        binding.content.text = content

        if (isColor) {
            binding.viewColor.show()
            binding.arrow.hide()
            binding.content.hide()
        } else {
            binding.viewColor.hide()
            binding.arrow.show()
            binding.content.show()
        }
        binding.title.text = title
        binding.content.isSelected = true


        addView(binding.root)
        typedArray.recycle()
    }

    fun setListener(action:()->Unit){
        binding.root.clickWithDebounce {
            action.invoke()
        }
    }

    fun setContent(content:String){
        binding.content.text = content
    }
}