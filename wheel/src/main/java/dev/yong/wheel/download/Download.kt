@file:Suppress("unused")

package dev.yong.wheel.download

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import java.io.File

/**
 * @author coderyong
 */
object Download {

    @JvmStatic
    fun create(context: Context, url: String): Builder {
        return Builder(context, url)
    }

    class Builder(private val context: Context, private val url: String) {

        private var saveFile: File? = null
        private var notificationTitle: String? = null
        private var notificationDescription: String? = null
        private var visibility = DownloadManager.Request.VISIBILITY_VISIBLE
        private var flags = DownloadManager.Request.NETWORK_WIFI
        private var receiver: DownloadReceiver? = null

        /**
         * 设置保存文件
         *
         * @param saveFile 保存到文件
         * @return Builder
         */
        fun setSaveFile(saveFile: File): Builder {
            this.saveFile = saveFile
            return this
        }

        /**
         * 设置通知标题
         *
         * @param notificationTitle 通知标题
         * @return Builder
         */
        fun setNotificationTitle(notificationTitle: String): Builder {
            this.notificationTitle = notificationTitle
            return this
        }

        /**
         * 设置通知描述
         *
         * @param notificationDescription 通知描述
         * @return Builder
         */
        fun setNotificationDescription(notificationDescription: String): Builder {
            this.notificationDescription = notificationDescription
            return this
        }

        /**
         * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
         * 默认：VISIBILITY_VISIBLE_NOTIFY_COMPLETED
         *
         * @param visibility
         *
         *
         * VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
         * VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
         * VISIBILITY_HIDDEN:                    始终不显示通知
         *
         * @return Builder
         */
        fun setNotificationVisibility(visibility: Int): Builder {
            this.visibility = visibility
            return this
        }

        /**
         * 设置允许使用的网络类型, 可选值:
         * 默认：NETWORK_WIFI
         *
         * @param flags
         *
         *
         * NETWORK_MOBILE:      移动网络
         * NETWORK_WIFI:        WIFI网络
         * NETWORK_BLUETOOTH:   蓝牙网络
         *
         * @return Builder
         */
        fun setAllowedNetworkTypes(flags: Int): Builder {
            this.flags = flags
            return this
        }

        /**
         * 注册下载广播
         *
         * @param receiver [DownloadReceiver]
         * @return Builder
         */
        fun registerDownloadReceiver(receiver: DownloadReceiver): Builder {
            this.receiver = receiver
            return this
        }

        /**
         * 执行下载
         */
        fun download() {
            // 创建下载请求
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(visibility)
            if (!TextUtils.isEmpty(notificationTitle)) {
                request.setTitle(notificationTitle)
            }
            if (!TextUtils.isEmpty(notificationDescription)) {
                request.setDescription(notificationDescription)
            }
            request.setAllowedNetworkTypes(flags)

            // 添加请求头
            // request.addRequestHeader("User-Agent", "Chrome Mozilla/5.0");

            // 设置下载文件的保存位置
            if (saveFile != null) {
                request.setDestinationUri(Uri.fromFile(saveFile))
            } else {
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/") + 1)
                )
            }
            //获取下载管理器服务的实例, 添加下载任务
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            // 将下载请求加入下载队列, 返回一个下载ID
            val downloadId = manager.enqueue(request)
            // 如果中途想取消下载, 可以调用remove方法, 根据返回的下载ID取消下载, 取消下载后下载保存的文件将被删除
            // manager.remove(downloadId);
            if (receiver != null) {
                receiver!!.setCurrentDownloadId(downloadId)
                val filter = IntentFilter()
                filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                context.registerReceiver(receiver, filter)
            }
        }
    }
}