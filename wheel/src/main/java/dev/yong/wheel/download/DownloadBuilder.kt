package dev.yong.wheel.download

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import java.io.File

/**
 * @author coderyong
 */
class DownloadBuilder {

    private var mUrl: String? = null
    private val mManager: DownloadManager
    private val mRequest: DownloadManager.Request
    private val mDefaultFileName: String
    private var mSaveFile: File? = null
    private var mNotificationTitle: String? = null
    private var mNotificationDescription: String? = null
    private var mVisibility = DownloadManager.Request.VISIBILITY_VISIBLE
    private var mFlags = 0

    constructor(manager: DownloadManager, url: String) {
        mUrl = url
        mManager = manager
        mRequest = DownloadManager.Request(Uri.parse(url))
        mDefaultFileName = url.substring(url.lastIndexOf("/") + 1)
    }

    constructor(manager: DownloadManager, request: DownloadManager.Request) {
        mManager = manager
        mRequest = request
        mDefaultFileName = request.toString()
    }

    /**
     * 设置保存文件
     *
     * @param saveFile 保存到文件
     * @return DownloadBuilder
     */
    fun setSaveFile(saveFile: File?): DownloadBuilder {
        mSaveFile = saveFile
        return this
    }

    /**
     * 设置通知标题
     *
     * @param notificationTitle 通知标题
     * @return DownloadBuilder
     */
    fun setNotificationTitle(notificationTitle: String?): DownloadBuilder {
        mNotificationTitle = notificationTitle
        return this
    }

    /**
     * 设置通知描述
     *
     * @param notificationDescription 通知描述
     * @return DownloadBuilder
     */
    fun setNotificationDescription(notificationDescription: String?): DownloadBuilder {
        mNotificationDescription = notificationDescription
        return this
    }

    /**
     * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
     * 默认：VISIBILITY_VISIBLE_NOTIFY_COMPLETED
     *
     * @param visibility VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
     * VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
     * VISIBILITY_HIDDEN:                    始终不显示通知
     * @return DownloadBuilder
     */
    fun setNotificationVisibility(visibility: Int): DownloadBuilder {
        mVisibility = visibility
        return this
    }

    /**
     * 设置允许使用的网络类型, 可选值:
     * 默认：NETWORK_WIFI
     *
     * @param flags NETWORK_MOBILE:      移动网络
     * NETWORK_WIFI:        WIFI网络
     * NETWORK_BLUETOOTH:   蓝牙网络
     * @return DownloadBuilder
     */
    fun setAllowedNetworkTypes(flags: Int): DownloadBuilder {
        mFlags = flags
        return this
    }

    /**
     * 执行下载
     */
    fun download(): Long {
        return try {
            val manager = dev.yong.wheel.download.DLManager.getInstance()
            var downloadId = manager.getDownloadId(mUrl)
            if (downloadId > 0 && (manager.checkResumeStatus(downloadId) || manager.open(downloadId))) {
                return downloadId
            } else {
                manager.removeDownloadId(mUrl)
            }
            // 创建下载请求
            mRequest.setNotificationVisibility(mVisibility)
            if (!TextUtils.isEmpty(mNotificationTitle)) {
                mRequest.setTitle(mNotificationTitle)
            }
            if (!TextUtils.isEmpty(mNotificationDescription)) {
                mRequest.setDescription(mNotificationDescription)
            }
            if (mFlags > 0) {
                mRequest.setAllowedNetworkTypes(mFlags)
            }
            // 设置下载文件的保存位置
            if (mSaveFile != null) {
                mRequest.setDestinationUri(Uri.fromFile(mSaveFile))
            } else {
                mRequest.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, mDefaultFileName
                )
            }
            // 将下载请求加入下载队列, 返回一个下载ID
            downloadId = mManager.enqueue(mRequest)
            manager.saveDownloadId(mUrl, downloadId)
            downloadId
        } catch (e: Exception) {
            -1
        }
    }
}