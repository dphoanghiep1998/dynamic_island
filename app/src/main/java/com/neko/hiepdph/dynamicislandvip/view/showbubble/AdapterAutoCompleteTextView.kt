package com.neko.hiepdph.dynamicislandvip.view.showbubble

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.config
import java.util.Locale


class AdapterAutoCompleteTextView(
    private val mContext: Context,
    private val layoutId: Int,
    private val textViewId: Int,
    private val dataList: List<String>
) : ArrayAdapter<String?>(mContext, layoutId, textViewId,dataList) {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if(dataList.isNotEmpty()){
            val text: String? = getItem(position)
            if (convertView == null) {
                convertView = inflater.inflate(layoutId, null)
                val holder = ResultHolder()
                holder.textView = convertView.findViewById<View>(textViewId) as TextView
                convertView.tag = holder
            }
            val holder = convertView!!.tag as ResultHolder
            if(position == mContext.config.bubbleDisplayTime){
                holder.textView?.setTextColor(mContext.getColor(R.color.su100))
            }else{
                holder.textView?.setTextColor(mContext.getColor(R.color.b100))
            }
            holder.textView?.text = text
            return convertView
        }
        return parent.rootView

    }

    override fun getCount(): Int {
        return dataList.size
    }
    override fun getFilter(): Filter {
        return CustomFilter
    }

    private val CustomFilter: Filter = object : Filter() {

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return super.convertResultToString(resultValue.toString())
        }

        override fun performFiltering(chars: CharSequence?): FilterResults {
            val resultFilter = FilterResults()
            val originalValues: ArrayList<String> = ArrayList(dataList)
            if (chars.isNullOrEmpty()) {
                resultFilter.values = originalValues
                resultFilter.count = originalValues.size
            } else {
                val prefixString = chars.toString().lowercase(Locale.getDefault())
                val newValues: ArrayList<String> = ArrayList()
                for (item in originalValues) {
                    val valueText: String = item.toString()
                    if (valueText.startsWith(prefixString)) newValues.add(item)
                }
                resultFilter.values = newValues
                resultFilter.count = newValues.size
            }
            return resultFilter
        }

        override fun publishResults(chars: CharSequence?, results: FilterResults) {
            clear()
            if (results.count > 0) addAll(results.values as ArrayList<String>) else notifyDataSetInvalidated()
        }
    }

    private class ResultHolder {
        var textView: TextView? = null
    }
}