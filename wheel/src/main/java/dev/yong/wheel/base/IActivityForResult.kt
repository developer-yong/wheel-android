package dev.yong.wheel.base

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import dev.yong.wheel.utils.FileUtils
import dev.yong.wheel.utils.Logger
import java.io.File

/**
 * 注册图片选择
 * <P>
 *     注：该方法需要在onStart之前调用
 * </P>
 */
fun ComponentActivity.registerPickImageResult(imageResult: IPickImageResult)
        : ActivityResultLauncher<Intent?> {
    val cropImageLauncher = this.registerForActivityResult(
        CropImageResult()
    ) {
        imageResult.onImageResult(it)
    }
    return this.registerForActivityResult(
        PickImageResult()
    ) {
        if (it != null) {
            cropImageLauncher.launch(it)
        } else {
            imageResult.onCancel()
        }
    }
}

/**
 * 图片选择结果
 * @author coderyong
 */
interface IPickImageResult {

    /**
     * 图片返回结果
     * @param imageUri 图片Uri
     */
    fun onImageResult(imageUri: Uri?)

    fun onCancel() {}
}

class CropImageResult : ActivityResultContract<Intent, Uri?>() {

    private lateinit var mCropIntent: Intent
    override fun createIntent(context: Context, input: Intent): Intent {
        mCropIntent = input
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        var imageUri: Uri? = null
        if (resultCode == Activity.RESULT_OK) {
            imageUri = intent?.data
        }
        if (imageUri == null) {
            imageUri = mCropIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
            return imageUri ?: mCropIntent.data
        }
        return imageUri
    }
}

class PickImageResult : ActivityResultContract<Intent?, Intent?>() {

    private lateinit var mContext: Context
    private var mCropIntent: Intent? = null

    override fun createIntent(context: Context, input: Intent?): Intent {
        mContext = context
        mCropIntent = input

        val mediaIntents = arrayOfNulls<Intent>(2)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        //创建拍照意图
        mediaIntents[0] = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mediaIntents[0]?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        mediaIntents[0]?.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        if (input != null) {
            //拍照图片保存到指定的路径
            var outUri: Uri? = input.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
            if (outUri == null) {
                val outFile = File(
                    mContext.externalCacheDir,
                    "Cache_${System.currentTimeMillis()}.jpg"
                )
                outUri = FileUtils.getUriForFile(mContext, outFile, "image/*")
            }
            mediaIntents[0]?.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        }
        //创建相册选择意图
        mediaIntents[1] = Intent(Intent.ACTION_PICK).setType("image/*")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, mediaIntents)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, mediaIntents[0])
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "使用以下应用获取图片")
        return chooserIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        Logger.e(resultCode.toString(), intent.toString())
        val result = intent.takeIf { resultCode == Activity.RESULT_OK }
        if (resultCode == Activity.RESULT_OK && mCropIntent != null) {
            val uri =
                if (result != null) result.data else mCropIntent!!.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
            mCropIntent!!.action = "com.android.camera.action.CROP"
            //添加读写权限
            mCropIntent!!.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            mCropIntent!!.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            //设置裁剪数据来源和类型
            mCropIntent!!.setDataAndType(uri, "image/*")
            //设置裁剪为true
            mCropIntent!!.putExtra("crop", "true")
            val outFile = File(
                mContext.externalCacheDir,
                "Cache_${System.currentTimeMillis()}.jpg"
            )
            val outUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Images.Media.DATA, outFile.absolutePath)
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, outFile.name)
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                mContext.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else try {
                Uri.fromFile(outFile)
            } catch (_: Throwable) {
                FileProvider.getUriForFile(
                    mContext,
                    mContext.applicationContext.packageName + ".FileProvider", outFile
                )
            }
            //设置输出Uri
            mCropIntent!!.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
            //设置输出格式
            mCropIntent!!.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            return mCropIntent
        }
        return result
    }
}