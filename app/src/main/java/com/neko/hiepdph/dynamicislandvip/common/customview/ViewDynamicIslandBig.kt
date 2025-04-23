package com.neko.hiepdph.dynamicislandvip.common.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutCallItemBinding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutListItemsBinding

class ViewDynamicIslandBig @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val  callBinding: LayoutCallItemBinding
    private val  itemBinding: LayoutListItemsBinding

    init {
        itemBinding = LayoutListItemsBinding.inflate(LayoutInflater.from(context),this,false)
        callBinding = LayoutCallItemBinding.inflate(LayoutInflater.from(context),this,false)
    }
}