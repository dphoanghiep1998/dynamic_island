package com.neko.hiepdph.dynamicislandvip.view.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import com.neko.hiepdph.dynamicislandvip.R
import com.neko.hiepdph.dynamicislandvip.common.Constant
import com.neko.hiepdph.dynamicislandvip.common.clickWithDebounce
import com.neko.hiepdph.dynamicislandvip.databinding.DialogFeedbackBinding
import com.neko.hiepdph.dynamicislandvip.common.base_component.BaseDialog

class DialogFeedBack(val mContext: Context) : BaseDialog<DialogFeedbackBinding>(mContext,canPan = true) {
    override fun getViewBinding(): DialogFeedbackBinding {
        return DialogFeedbackBinding.inflate(layoutInflater)
    }

    override fun setBackGroundDrawable() {
        window?.setBackgroundDrawableResource(R.drawable.bg_12_primary)
    }

    override fun initView() {
        binding.btnSend.clickWithDebounce {
            if (binding.editText.text.trim().isEmpty()) {
                Toast.makeText(context, R.string.empty_feedback, Toast.LENGTH_SHORT).show()
            } else {
                sendEmail(binding.editText.text.toString())
                binding.editText.text.clear()
                dismiss()
            }
        }

        binding.btnCancel.clickWithDebounce {
            dismiss()
        }

    }

    private fun sendEmail(message: String) {
        val deviceName = Build.MODEL // returns model name
        val deviceManufacturer = Build.MANUFACTURER

        val testIntent = Intent(Intent.ACTION_VIEW)
        val data: Uri =
            """mailto:?subject=Feedback ${context.getString(R.string.app_name)}&body=Device: $deviceManufacturer - $deviceName 
                |Android SDK ${Build.VERSION.SDK_INT}
                |${message}&to=${Constant.MAIL_TO}
               
            """.trimMargin().toUri()
        testIntent.data = data
        try {
            context.startActivity(testIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.no_sender_found), Toast.LENGTH_SHORT)
                .show()
        }
    }
}