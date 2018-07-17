package dev.yong.wheel.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 下载文件广播接收器
 *
 * @author coderyong
 */
public class DownloadReceiver extends BroadcastReceiver {

    private OnDownloadCompleteListener mListener;
    private long mDownloadId;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            if (mDownloadId == downloadId && mListener != null) {
                Query query = new Query();
                query.setFilterById(downloadId);
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                if (manager != null) {
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        int fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String fileUri = cursor.getString(fileUriIdx);
                        if (!TextUtils.isEmpty(fileUri)) {
                            mListener.onComplete(Uri.parse(fileUri));
                        }
                    }
                    cursor.close();
                }
            }
        }
    }

    public void setCurrentDownloadId(long downloadId) {
        this.mDownloadId = downloadId;
    }

    /**
     * 设置下载完成监听器
     *
     * @param listener 监听实现类
     */
    public void setOnDownloadCompleteListener(OnDownloadCompleteListener listener) {
        this.mListener = listener;
    }

    public interface OnDownloadCompleteListener {
        /**
         * 下载完成回调
         *
         * @param uri uri地址
         */
        void onComplete(Uri uri);
    }
}
