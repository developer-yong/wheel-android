package dev.yong.wheel.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File

/**
 * 注册文件选择返回
 * <P>
 *     注：该方法需要在onStart之前调用
 * </P>
 */
fun ComponentActivity.registerFileChooserResult(iChooserResult: IFileChooserResult)
        : ActivityResultLauncher<IFileChooserResult.FileChooserResult> {
    return this.registerForActivityResult(
        object : ActivityResultContract<IFileChooserResult.FileChooserResult, Array<Uri>>() {
            var chooserResult: IFileChooserResult.FileChooserResult? = null
            override fun createIntent(
                context: Context,
                input: IFileChooserResult.FileChooserResult
            ): Intent {
                chooserResult = input
                return input.createIntent()
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Array<Uri>? {
                return chooserResult?.parse(resultCode, intent)
            }
        }) { iChooserResult.onChooserResult(it) }
}

/**
 * 文件选择结果接口类
 * @author coderyong
 */
interface IFileChooserResult {

    /**
     * 文件选择回调
     * @param uriResult 文件选择结果
     */
    fun onChooserResult(uriResult: Array<Uri>?)

    class FileChooserResult(
        private var mContext: Context,
        chooserParams: WebChromeClient.FileChooserParams? = null
    ) {

        var mode = WebChromeClient.FileChooserParams.MODE_OPEN
        var acceptTypes = arrayOf("*/*")
        var isCaptureEnabled = false
        var title: CharSequence? = null
        var filenameHint: String? = null
        var captureUri: Uri? = null

        init {
            if (chooserParams != null) {
                mode = chooserParams.mode
                acceptTypes = chooserParams.acceptTypes
                isCaptureEnabled = chooserParams.isCaptureEnabled
                title = chooserParams.title
                filenameHint = chooserParams.filenameHint
            }
        }

        fun createIntent(): Intent {
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)

            if (isCaptureEnabled) {
                val captureIntent = Intent(
                    if (acceptTypes.contains("video/*"))
                        MediaStore.ACTION_IMAGE_CAPTURE_SECURE
                    else MediaStore.ACTION_IMAGE_CAPTURE
                )
                captureIntent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                val outFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    if (acceptTypes.contains("video/*"))
                        "${System.currentTimeMillis()}.jpg"
                    else
                        "${System.currentTimeMillis()}.mp4"
                )
                captureUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(
                        mContext,
                        mContext.applicationInfo.packageName + ".FileProvider",
                        outFile
                    ) else Uri.fromFile(outFile)
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri)
                if (acceptTypes.size > 1) {
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, captureIntent)
                } else {
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
                }
            }

            val mediaIntents = arrayOfNulls<Intent>(acceptTypes.size)
            for (i in acceptTypes.indices) {
                mediaIntents[i] = Intent(Intent.ACTION_GET_CONTENT)
                mediaIntents[i]!!.type = acceptTypes[i]
                mediaIntents[i]!!.putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE
                )
            }
            if (acceptTypes.size > 1) {
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, mediaIntents)
            } else {
                chooserIntent.putExtra(Intent.EXTRA_INTENT, mediaIntents[0])
            }
            return chooserIntent
        }

        fun parse(resultCode: Int, intent: Intent?): Array<Uri>? {
            if (resultCode == Activity.RESULT_OK) {
                when {
                    intent?.data != null -> {
                        return arrayOf(intent.data!!)
                    }
                    intent?.clipData != null -> {
                        val data = intent.clipData
                        val uriArray = arrayListOf<Uri>()
                        for (i in 0 until data!!.itemCount) {
                            uriArray.add(data.getItemAt(i).uri)
                        }
                        return uriArray.toArray(emptyArray())
                    }
                    captureUri != null -> {
                        return arrayOf(captureUri!!)
                    }
                }
            }
            return WebChromeClient.FileChooserParams.parseResult(resultCode, intent)
        }
    }
}