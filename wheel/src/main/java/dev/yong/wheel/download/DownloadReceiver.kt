@file:Suppress("unused")

package dev.yong.wheel.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils

/**
 * 下载文件广播接收器
 *
 * @author coderyong
 */
class DownloadReceiver : BroadcastReceiver() {

    private var mListener: OnDownloadCompleteListener? = null
    private var mDownloadId: Long = 0

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (mDownloadId == downloadId && mListener != null) {
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val fileUri = cursor.getString(fileUriIdx)
                    if (!TextUtils.isEmpty(fileUri)) {
                        mListener!!.onComplete(Uri.parse(fileUri))
                    }
                }
                cursor.close()
            }
        }
    }

    fun setCurrentDownloadId(downloadId: Long) {
        mDownloadId = downloadId
    }

    /**
     * 设置下载完成监听器
     *
     * @param listener 监听实现类
     */
    fun setOnDownloadCompleteListener(listener: OnDownloadCompleteListener) {
        mListener = listener
    }

    interface OnDownloadCompleteListener {
        /**
         * 下载完成回调
         *
         * @param uri uri地址
         */
        fun onComplete(uri: Uri)
    }
}