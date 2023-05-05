package dev.yong.wheel.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author coderyong
 */
public class DLManager {

    private static final String CACHE_KEY = "CACHE_DOWNLOAD";

    /**
     * 文件类型与文件后缀名的匹配表
     */
    private static final String[][] MATCH_ARRAY = {
            //{后缀名，    文件类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    private Context mContext;
    private final android.app.DownloadManager mManager;
    private final BroadcastReceiver mDownloadReceiver;

    private Vector<OnDownloadCompleteListener> mDownloadCompleteListeners;

    public static void init(Context context) {
        if (context != null) {
            getInstance().mContext = context.getApplicationContext();
        }
    }

    /**
     * 获取下载构建对象
     *
     * @param url 下载地址
     * @return DownloadBuilder
     */
    public static DownloadBuilder with(String url) {
        return new DownloadBuilder(getInstance().mManager, url);
    }

    /**
     * 获取下载构建对象
     *
     * @param request 下载请求
     * @return DownloadBuilder
     */
    public static DownloadBuilder with(DownloadManager.Request request) {
        return new DownloadBuilder(getInstance().mManager, request);
    }

    /**
     * 添加下载完成监听
     *
     * @param downloadCompleteListener OnDownloadCompleteListener
     */
    public void addOnDownloadCompleteListener(OnDownloadCompleteListener downloadCompleteListener) {
        if (downloadCompleteListener != null) {
            if (mDownloadCompleteListeners == null) {
                mDownloadCompleteListeners = new Vector<>();
            }
            mDownloadCompleteListeners.add(downloadCompleteListener);
        }
    }

    /**
     * 查询已下载长度
     *
     * @param downloadId 下载ID
     * @return DownloadSize
     */
    @SuppressLint("Range")
    public long queryDownloadSize(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                return cur.getLong(cur.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 查询总大小
     *
     * @param downloadId 下载ID
     * @return TotalSize
     */
    @SuppressLint("Range")
    public long queryTotalSize(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                return cur.getLong(cur.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 查询下载状态
     *
     * @param downloadId 下载ID
     * @return 下载状态
     * @see android.app.DownloadManager#STATUS_PENDING
     * @see android.app.DownloadManager#STATUS_RUNNING
     * @see android.app.DownloadManager#STATUS_PAUSED
     * @see android.app.DownloadManager#STATUS_SUCCESSFUL
     * @see android.app.DownloadManager#STATUS_FAILED
     */
    @SuppressLint("Range")
    public int queryDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                return cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 查询下载错误原因
     *
     * @param downloadId 下载ID
     * @return 错误原因
     * @see android.app.DownloadManager#ERROR_FILE_ERROR
     * @see android.app.DownloadManager#ERROR_UNHANDLED_HTTP_CODE
     * @see android.app.DownloadManager#ERROR_HTTP_DATA_ERROR
     * @see android.app.DownloadManager#ERROR_TOO_MANY_REDIRECTS
     * @see android.app.DownloadManager#ERROR_INSUFFICIENT_SPACE
     * @see android.app.DownloadManager#ERROR_DEVICE_NOT_FOUND
     * @see android.app.DownloadManager#ERROR_CANNOT_RESUME
     * @see android.app.DownloadManager#PAUSED_WAITING_TO_RETRY
     * @see android.app.DownloadManager#PAUSED_WAITING_FOR_NETWORK
     * @see android.app.DownloadManager#PAUSED_QUEUED_FOR_WIFI
     * @see android.app.DownloadManager#PAUSED_UNKNOWN
     */
    @SuppressLint("Range")
    public int queryErrorReason(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                return cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_REASON));
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 查询下载文件URI
     *
     * @param downloadId 下载ID
     * @return Uri
     */
    @SuppressLint("Range")
    public Uri queryLocalUri(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                String localUri = cur.getString(
                        cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(localUri)) {
                    return Uri.parse(localUri);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 查询下载地址
     *
     * @param downloadId 下载ID
     * @return Url
     */
    @SuppressLint("Range")
    public String queryUrl(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cur = null;
        try {
            cur = mManager.query(query);
            if (cur != null && cur.moveToFirst()) {
                return cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_URI));
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 通过反射暂停下载
     *
     * @param downloadIds 下载ID
     * @return true暂停成功，false暂停失败
     */
    @SuppressWarnings({"JavaReflectionMemberAccess"})
    public boolean invokePause(long... downloadIds) {
        try {
            Method resumeDownload = DownloadManager.class
                    .getMethod("pauseDownload", long[].class);
            resumeDownload.invoke(mManager, (Object) downloadIds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过反射恢复下载
     *
     * @param downloadIds 下载ID
     * @return true恢复成功，false恢复失败
     */
    @SuppressWarnings({"JavaReflectionMemberAccess"})
    public boolean invokeResume(long... downloadIds) {
        try {
            Method resumeDownload = DownloadManager.class
                    .getMethod("resumeDownload", long[].class);
            resumeDownload.invoke(mManager, (Object) downloadIds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过ContentResolver暂停下载
     *
     * @param localUri   下载Url
     * @param downloadId 下载ID
     * @return true暂停成功，false暂停失败
     */
    public boolean pause(Uri localUri, long downloadId) {
        int updatedRows = 0;
        ContentValues pauseDownload = new ContentValues();
        pauseDownload.put("control", 1); // Pause Control Value
        try {
            updatedRows = mContext.getContentResolver().update(localUri,
                    pauseDownload, DownloadManager.COLUMN_ID + "=?",
                    new String[]{downloadId + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    /**
     * 通过ContentResolver恢复下载
     *
     * @param downloadId 当前下载ID
     * @return true恢复成功，false恢复失败
     */
    public boolean resume(Uri localUri, long downloadId) {
        int updatedRows = 0;
        ContentValues resumeDownload = new ContentValues();
        resumeDownload.put("control", 0); // Resume Control Value
        try {
            updatedRows = mContext.getContentResolver().update(localUri,
                    resumeDownload, DownloadManager.COLUMN_ID + "=?",
                    new String[]{downloadId + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    /**
     * 移除下载任务
     *
     * @param downloadIds 下载ID
     * @return 移除数量
     */
    public long remove(long... downloadIds) {
        return mManager.remove();
    }

    public void destroy() {
        if (mDownloadCompleteListeners != null) {
            mDownloadCompleteListeners.clear();
            mDownloadCompleteListeners = null;
        }
        if (mContext != null && mDownloadReceiver != null) {
            mContext.unregisterReceiver(mDownloadReceiver);
        }
    }

    /**
     * 检查下载状态
     *
     * @param downloadId 下载ID
     * @return true：正在下载，false：下载完成/下载失败
     */
    public boolean checkResumeStatus(long downloadId) {
        int status = queryDownloadStatus(downloadId);
        switch (status) {
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                return true;
            case DownloadManager.STATUS_PAUSED:
                return invokeResume(downloadId);
            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_SUCCESSFUL:
            default:
                return false;
        }
    }

    /**
     * 打开文件
     *
     * @param downloadId 下载ID
     */
    public boolean open(long downloadId) {
        if (mContext == null || downloadId < 0) {
            return false;
        }
        try {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "请允许手机读写存储权限后尝试", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!mContext.getPackageManager().canRequestPackageInstalls()) {
                    //没有权限让调到设置页面进行开启权限；
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + mContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    return true;
                }
            }
            Uri uri = queryLocalUri(downloadId);
            String absolutePath = uri.getPath();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //文件的类型
            String type = "*/*";
            for (String[] strings : MATCH_ARRAY) {
                String suffix = getSuffix(absolutePath);
                //判断文件的格式
                if (!TextUtils.isEmpty(suffix)) {
                    if (suffix.toLowerCase().contains(strings[0])) {
                        type = strings[1];
                        break;
                    }
                } else {
                    if (absolutePath.contains(strings[0])) {
                        type = strings[1];
                        break;
                    }
                }
            }
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(mContext,
                        mContext.getPackageName() + ".FileProvider", new File(absolutePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            //设置intent的data和Type属性
            intent.setDataAndType(uri, type);
            //跳转
            mContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件后缀
     *
     * @param path 文件绝对路径
     */
    private String getSuffix(String path) {
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf(".")).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * 获取下载记录
     *
     * @param url 下载地址
     */
    long getDownloadId(String url) {
        return mContext.getSharedPreferences(CACHE_KEY, Activity.MODE_PRIVATE)
                .getLong(url, -1);
    }

    /**
     * 保存下载记录
     *
     * @param url        下载地址
     * @param downloadId 下载ID
     */
    void saveDownloadId(String url, long downloadId) {
        mContext.getSharedPreferences(CACHE_KEY, Activity.MODE_PRIVATE)
                .edit().putLong(url, downloadId).apply();
    }

    /**
     * 移除下载记录
     *
     * @param url 下载地址
     */
    void removeDownloadId(String url) {
        mContext.getSharedPreferences(CACHE_KEY, Activity.MODE_PRIVATE)
                .edit().remove(url).apply();
    }

    private DLManager() {
        if (mContext == null) {
            mContext = getContext();
        }
        mManager = (android.app.DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
                        .equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(
                            android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                    String url = queryUrl(downloadId);
                    if (!TextUtils.isEmpty(url)) {
                        SharedPreferences preferences = context.getSharedPreferences(
                                CACHE_KEY, Activity.MODE_PRIVATE);
                        //判断是否时自己的下载
                        if (downloadId > 0 && downloadId == preferences.getLong(url, -1)) {
                            if (mDownloadCompleteListeners != null && !mDownloadCompleteListeners.isEmpty()) {
                                Iterator<OnDownloadCompleteListener> iterator = mDownloadCompleteListeners.iterator();
                                while (iterator.hasNext()) {
                                    iterator.next().onDownloadComplete(downloadId);
                                    iterator.remove();
                                }
                            } else {
                                open(downloadId);
                            }
                        }
                    }
                }
            }
        };
        mContext.registerReceiver(mDownloadReceiver,
                new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @SuppressLint("PrivateApi")
    private static Context getContext() {
        Context context = null;
        try {
            context = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context == null) {
            try {
                context = (Application) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return context;
    }

    public static DLManager getInstance() {
        return DownloadManagerHolder.INSTANCE;
    }

    private static class DownloadManagerHolder {
        @SuppressLint("StaticFieldLeak")
        private static final DLManager INSTANCE = new DLManager();
    }

    public interface OnDownloadCompleteListener {
        /**
         * 下载完成回调
         *
         * @param downloadId 下载ID
         */
        void onDownloadComplete(long downloadId);
    }
}
