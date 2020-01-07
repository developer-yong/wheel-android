package dev.yong.photo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.yong.photo.bean.MediaFile;

import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.DATE_MODIFIED;
import static android.provider.MediaStore.MediaColumns.DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns._ID;
import static android.provider.MediaStore.Video.VideoColumns.DURATION;

/**
 * @author coderyong
 */
class Utils {

    /**
     * 获取本地所有的图片
     *
     * @return 图片集合
     */
    static List<MediaFile> getAllLocalImages(Context context) {
        List<MediaFile> mediaFiles = new ArrayList<>();
        String[] projection = {_ID, DATA, DISPLAY_NAME, DATE_MODIFIED, SIZE};
        //创建Sql查询条件
        String where = MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=?";
        //指定格式
        String[] whereArgs = {"image/jpeg", "image/png", "image/jpg", "image/gif"};
        //查询图片
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs, DATE_MODIFIED + " DESC ");
        if (cursor == null) {
            return mediaFiles;
        }
        //遍历游标
        while (cursor.moveToNext()) {
            //创建图片文件
            MediaFile mediaFile = new MediaFile();
            mediaFile.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
            mediaFile.setName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
            mediaFile.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(SIZE)));
            mediaFile.setPath(cursor.getString(cursor.getColumnIndex(DATA)));
            mediaFile.setLastModified(cursor.getLong(cursor.getColumnIndex(DATE_MODIFIED)));
            mediaFile.setType(MediaFile.Type.IMAGE);
            mediaFiles.add(mediaFile);
        }
        cursor.close();
        return mediaFiles;
    }

    /**
     * 获取本地所有的视频
     *
     * @return 视频集合
     */
    static List<MediaFile> getAllLocalVideos(Context context) {
        List<MediaFile> mediaFiles = new ArrayList<>();
        String[] projection = {_ID, DATA, DISPLAY_NAME, DATE_MODIFIED, DURATION, SIZE};
        //创建Sql查询条件
        String where = MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=? or "
                + MIME_TYPE + "=?";

        String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                "video/mkv", "video/mov", "video/mpg"};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs, DATE_ADDED + " DESC ");
        if (cursor == null) {
            return mediaFiles;
        }
        while (cursor.moveToNext()) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
            mediaFile.setName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
            mediaFile.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(SIZE)));
            mediaFile.setPath(cursor.getString(cursor.getColumnIndexOrThrow(DATA)));
            mediaFile.setDuration(cursor.getLong(cursor.getColumnIndex(DURATION)));
            mediaFile.setLastModified(cursor.getLong(cursor.getColumnIndex(DATE_MODIFIED)));
            mediaFile.setType(MediaFile.Type.VIDEO);
            mediaFiles.add(mediaFile);
        }
        cursor.close();
        return mediaFiles;
    }

    /**
     * 获取本地所有的视频
     *
     * @return 视频集合
     */
    static List<MediaFile> getAllLocalMediaFiles(Context context) {
        List<MediaFile> mediaFiles = new ArrayList<>();
        mediaFiles.addAll(getAllLocalImages(context));
        mediaFiles.addAll(getAllLocalVideos(context));
        Collections.sort(mediaFiles, (o1, o2) -> (int) (o2.getLastModified() - o1.getLastModified()));
        return mediaFiles;
    }

    /**
     * 全屏实现
     *
     * @param activity 作用Activity
     */
    static void fullScreen(Activity activity) {
        Window window = activity.getWindow();
        //4.4 全透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //5.0 全透明实现
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.color_action_bar));
        }
    }

    /**
     * 获取状态栏高度
     */
    static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
