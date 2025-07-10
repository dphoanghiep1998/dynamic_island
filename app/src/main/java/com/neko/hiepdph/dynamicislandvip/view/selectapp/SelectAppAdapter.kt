package com.neko.hiepdph.dynamicislandvip.view.selectapp

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.hide
import com.neko.hiepdph.dynamicislandvip.common.show
import com.neko.hiepdph.dynamicislandvip.data.model.AppDetail
import com.neko.hiepdph.dynamicislandvip.databinding.LayoutItemSelectAppBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectAppAdapter(
    val onClickAll: (List<AppDetail>) -> Unit, val onClickItem: (List<AppDetail>) -> Unit
) : RecyclerView.Adapter<SelectAppAdapter.SelectAppViewHolder>() {
    private val listApp = mutableListOf<AppDetail>()
    private val listAppSelected = mutableListOf<AppDetail>()

    fun setData(appList: List<AppDetail>, selectedList: List<AppDetail>) {
        listApp.clear()
        listApp.addAll(appList)
        listAppSelected.clear()
        listAppSelected.addAll(selectedList)
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

    override fun onBindViewHolder(
        holder: SelectAppViewHolder, position: Int, payloads: List<Any?>
    ) {
        val item = listApp[position]

        if (payloads.isNotEmpty()) {
            payloads.forEach {
                if (it == "reload") {
                    with(holder) {
                        if (position == 0) {
                            if (listAppSelected.isNotEmpty()) {
                                binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                            } else {
                                binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                            }
                        } else {
                            binding.tvApp.isSelected = true

                            if (item.pkg !in listAppSelected.map { it.pkg }) {
                                binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                            } else {
                                binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                            }

                        }
                    }
                }
            }

        } else {
            super.onBindViewHolder(holder, position, payloads)
            with(holder) {
                if (position == 0) {
                    if (listAppSelected.isNotEmpty()) {
                        binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                    } else {
                        binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                    }

                } else {
                    binding.tvApp.isSelected = true
                    Log.d("TAG", "onBindViewHolder: " + listAppSelected.map { it.pkg })
                    if (item.pkg !in listAppSelected.map { it.pkg }) {
                        binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                    } else {
                        binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                    }

                }
            }
        }
    }

    override fun onBindViewHolder(holder: SelectAppViewHolder, position: Int) {
        val item = listApp[position]
        with(holder) {
            if (position == 0) {
                binding.tvSelectAll.show()
                binding.tvApp.hide()
                binding.iconApp.hide()
                binding.icTick.show()

                if (listAppSelected.isNotEmpty()) {
                    binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                } else {
                    binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                }

                binding.root.clickWithDebounce {
                    if (listAppSelected.isNotEmpty()) {
                        listAppSelected.clear()
                        onClickAll.invoke(listAppSelected)
                    } else {
                        listAppSelected.clear()
                        listAppSelected.addAll(listApp)
                        onClickAll.invoke(listAppSelected)
                    }
                    notifyItemRangeChanged(0, listApp.size, "reload")
                }
            } else {
                binding.icTick.show()
                binding.tvSelectAll.hide()
                binding.tvApp.show()
                binding.iconApp.show()
                binding.tvApp.text = item.label
                CoroutineScope(Dispatchers.IO).launch {
                    val newItem = getAppByPkg(
                        itemView.context,
                        item.pkg?.substringBefore("/").toString(),
                        item.activityInfoName.toString(),
                        item
                    )
                    launch(Dispatchers.Main) {
                        Glide.with(itemView.context).load(newItem)
                            .placeholder(R.drawable.ic_config_inactive)
                            .error(R.drawable.ic_config_inactive).into(binding.iconApp)
                    }

                }

                binding.root.clickWithDebounce {
                    if (item.pkg in listAppSelected.map { it.pkg }) {
                        listAppSelected.removeIf { it.pkg == item.pkg }
                    } else {
                        listAppSelected.add(item)
                    }
                    notifyItemChanged(position, "reload")
                    notifyItemChanged(0, "reload")
                    onClickItem.invoke(listAppSelected)
                }
                binding.tvApp.isSelected = true

                if (item.pkg !in listAppSelected.map { it.pkg }) {
                    binding.icTick.setImageResource(R.drawable.ic_check_active_app)
                } else {
                    binding.icTick.setImageResource(R.drawable.ic_check_inactive_app)
                }

            }
        }

    }

    private fun getAppByPkg(
        context: Context, pkg: String, cln: String, item: AppDetail
    ): Drawable? {
        return try {
            if (Build.VERSION.SDK_INT >= 26) {
                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                for (userHandle in launcherApps.profiles) {
                    try {
                        val activities = launcherApps.getActivityList(pkg, userHandle) // Optimized
                        for (info in activities) {
                            if (info.componentName.packageName == pkg) {
                                return info.getIcon(0)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val pm = context.packageManager
                val activities = pm.queryIntentActivities(intent, 0)
                for (resolveInfo in activities) {
                    if (resolveInfo.activityInfo.packageName == pkg && resolveInfo.activityInfo.name == cln) {
                        return resolveInfo.loadIcon(pm)
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}