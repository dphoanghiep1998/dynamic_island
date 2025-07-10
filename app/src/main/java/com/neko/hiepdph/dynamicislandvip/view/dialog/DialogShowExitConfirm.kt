package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.DialogShowExitBinding


class DialogShowExitConfirm(
    private val onPositive: () -> Unit,
    private val onNegative: () -> Unit
) {

    private lateinit var binding: DialogShowExitBinding
    fun onCreateDialog(actvity: Activity): Dialog {
        binding = DialogShowExitBinding.inflate(LayoutInflater.from(actvity))
        val dialog = Dialog(actvity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawable(actvity.getColor(android.R.color.transparent).toDrawable())
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(actvity, dialog)
        return dialog
    }


    private fun initView(actvity: Activity, dialog: Dialog) {
        initFirst(dialog)
        initButton(actvity, dialog)
//        AdsCore.showNativeAds(actvity,binding.nativeAdmob,binding.nativeMax,null,null,
//            NativeTypeEnum.TYPE)
    }

    private fun openLink(context: Context, strUri: String?) {
        try {
            val uri = Uri.parse(strUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initButton(actvity: Activity, dialog: Dialog) {
        binding.btnLater.clickWithDebounce {
            dialog.dismiss()
            onPositive.invoke()
        }

        binding.btnRate.clickWithDebounce {
            dialog.dismiss()
        }

    }


    private fun initFirst(dialog: Dialog) {

        binding.containerMain.clickWithDebounce {}

    }

}