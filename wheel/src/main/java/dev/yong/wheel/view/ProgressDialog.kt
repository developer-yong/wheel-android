@file:Suppress("unused")

package dev.yong.wheel.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import dev.yong.wheel.R

/**
 * @author CoderYong
 */
class ProgressDialog @JvmOverloads constructor(
    context: Context,
    theme: Int = R.style.DialogTransparent
) : Dialog(
    context, theme
) {
    private val mTvMessage: TextView
    private val mView: View = View.inflate(context, R.layout.dialog_progress, null)
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(mView)
        setCancelable(false)
    }

    fun show(resId: Int) {
        show(context.getString(resId))
    }

    fun show(message: CharSequence) {
        this.show()
        mTvMessage.text = message
    }

    init {
        val progressBar = mView.findViewById<View>(R.id.progress) as ProgressBar
        progressBar.isIndeterminate = true
        mTvMessage = mView.findViewById<View>(R.id.tv_progress_content) as TextView
        val params = window!!.attributes
        params!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = params
    }
}