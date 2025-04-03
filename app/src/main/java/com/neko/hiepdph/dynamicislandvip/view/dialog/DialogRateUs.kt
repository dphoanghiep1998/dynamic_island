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
import android.widget.Toast
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.common.config
import com.neko.hiepdph.dynamicislandvip.common.isInternetAvailable
import com.neko.hiepdph.dynamicislandvip.databinding.DialogRateUsBinding


class DialogRateUs() {
    private lateinit var binding: DialogRateUsBinding
    fun onCreateDialog(actvity: Activity): Dialog {
        binding = DialogRateUsBinding.inflate(LayoutInflater.from(actvity))
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
        dialog.window!!.setBackgroundDrawable(ColorDrawable(actvity.getColor(android.R.color.transparent)))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(actvity, dialog)
        return dialog
    }


    private fun initView(actvity: Activity, dialog: Dialog) {
        actvity.config.timeShowRate = System.currentTimeMillis()

        initFirst(dialog)
        initButton(actvity, dialog)

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
        binding.btnRate.clickWithDebounce {
            if (isInternetAvailable(actvity)) {
                actvity.config.isUserRated = true
                val manager = ReviewManagerFactory.create(actvity)
//                    val manager = FakeReviewManager(this@ActivityVault)
                manager.requestReviewFlow().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        manager.launchReviewFlow(actvity, reviewInfo)
                    } else {
                        @ReviewErrorCode val reviewErrorCode =
                            (task.exception as ReviewException).errorCode
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(
                    actvity, actvity.getString(R.string.internet_not_available), Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.btnLater.clickWithDebounce {
            actvity.config.timeShowRate = System.currentTimeMillis()
            dialog.dismiss()
        }

        binding.btnExit.clickWithDebounce {
            actvity.config.timeShowRate = System.currentTimeMillis()
            dialog.dismiss()
        }

    }


    private fun initFirst(dialog: Dialog) {

        binding.containerMain.clickWithDebounce {}

    }

}