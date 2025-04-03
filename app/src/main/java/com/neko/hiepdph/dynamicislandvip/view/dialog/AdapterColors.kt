package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemColorAddBinding
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemColorBinding

class AdapterColors(val onClickColor: (String) -> Unit, val onClickAdd: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listColors = mutableListOf<String>()
    private var selectedIndex = -1

    inner class ItemColorViewHolder(val binding: LayoutItemColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemColorViewHolderAdd(val binding: LayoutItemColorAddBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setData(list: List<String>) {
        listColors.clear()
        listColors.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType != 999) {
            val binding =
                LayoutItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemColorViewHolder(binding)
        } else {
            val binding = LayoutItemColorAddBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ItemColorViewHolderAdd(binding)
        }

    }

    override fun getItemCount(): Int {
        return listColors.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == listColors.size - 1) {
            return 999
        } else {
            return position
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) != 999) {
            with(holder as ItemColorViewHolder) {
                val color = listColors[adapterPosition]
                binding.imageColor.setBackgroundColor(Color.parseColor(color))
                if (adapterPosition == selectedIndex) {
                    binding.cardView.setCardBackgroundColor(Color.parseColor(color))
                }

                binding.root.clickWithDebounce {
                    notifyItemChanged(selectedIndex)
                    selectedIndex = adapterPosition
                    onClickColor(color)
                    notifyItemChanged(position)
                }
            }
        } else {
            with(holder as ItemColorViewHolderAdd) {
                binding.root.clickWithDebounce {
                    onClickAdd()
                }
            }
        }

    }
}