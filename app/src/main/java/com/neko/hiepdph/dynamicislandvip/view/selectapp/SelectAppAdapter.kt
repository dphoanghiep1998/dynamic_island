package com.neko.hiepdph.dynamicislandvip.view.selectapp

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.AppModel
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemSelectAppBinding

class SelectAppAdapter(
    val onClickAll: (List<AppModel>) -> Unit, val onClickItem: (List<AppModel>) -> Unit
) : RecyclerView.Adapter<SelectAppAdapter.SelectAppViewHolder>() {
    private val listApp = mutableListOf<AppModel>()
    private val listAppSelected = mutableListOf<AppModel>()

    fun setData(appList: List<AppModel>?, selectedList: List<AppModel>) {
        if(appList != null){
            listApp.clear()
            listApp.addAll(appList)
        }


        listAppSelected.clear()
        listAppSelected.addAll(selectedList)
        notifyDataSetChanged()
    }

    fun setItemSelected(item: AppModel) {
        if (listAppSelected.contains(item)) {
            listAppSelected.remove(item)
        } else {
            listAppSelected.add(item)
        }
        notifyDataSetChanged()
    }

    inner class SelectAppViewHolder(val binding: LayoutItemSelectAppBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAppViewHolder {
        val binding =
            LayoutItemSelectAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectAppViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listApp.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SelectAppViewHolder, position: Int) {
        val item = listApp[position]
        with(holder) {
            if (position == 0) {
                binding.tvSelectAll.show()
                binding.tvApp.hide()
                binding.iconApp.hide()
                binding.icTick.show()
                if(listApp.size == listAppSelected.size){
                    binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                }else{
                    binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                }
                binding.root.clickWithDebounce {
                    if(listApp.size == listAppSelected.size){
                        listAppSelected.clear()
                        onClickAll.invoke(listAppSelected)
                    }else{
                        listAppSelected.clear()
                        listAppSelected.addAll(listApp)
                        onClickAll.invoke(listAppSelected)
                    }
                    notifyDataSetChanged()
                }
            } else {
                binding.icTick.show()
                binding.tvSelectAll.hide()
                binding.tvApp.show()
                binding.iconApp.show()
                binding.tvApp.text = item.className

                val newItem = getAppByPkg(
                    itemView.context, item.pkg.substringBefore("/"), item.className, item
                )
                Glide.with(itemView.context).load(newItem?.icon)
                    .placeholder(R.drawable.ic_config_inactive).error(R.drawable.ic_config_inactive)
                    .into(binding.iconApp)
                binding.root.clickWithDebounce {
                    if (item.id in listAppSelected.map { it.id }) {
                        listAppSelected.removeIf { it.id == item.id }

                    } else {
                        listAppSelected.add(item)
                    }
                    notifyItemChanged(position)
                    onClickItem.invoke(listAppSelected)
                }

                if (item.id in listAppSelected.map { it.id }) {
                    binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                } else {
                    binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                }

            }
        }

    }

    private fun getAppByPkg(context: Context, pkg: String, cln: String, item: AppModel): AppModel? {
        if (Build.VERSION.SDK_INT >= 26) {
            val launcherApps =
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            var id = 0L
            for (userHandle in launcherApps.profiles) {
                for (launcherActivityInfo in launcherApps.getActivityList(null, userHandle)) {
                    val packageName = launcherActivityInfo.componentName.packageName
                    val name = launcherActivityInfo.name
                    if (packageName == pkg) {
                        val newAppModel = AppModel(
                            item.id, pkg, name
                        )
                        newAppModel.icon = launcherActivityInfo.getIcon(0)
                        return newAppModel
                    }
                }
                id++
            }
        } else {
            val intent = Intent("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.LAUNCHER")
            for (resolveInfo in context.packageManager.queryIntentActivities(intent, 0)) {
                val str3 = resolveInfo.activityInfo.packageName
                val str4 = resolveInfo.activityInfo.name
                if (str3 == pkg && cln == str4) {
                    val newItem = AppModel(
                        item.id, cln, pkg
                    )
                    newItem.icon = resolveInfo.loadIcon(context.packageManager)
                    return newItem

                }
            }
        }
        return null
    }
}