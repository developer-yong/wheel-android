package dev.yong.wheel.cache;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class DiskLruCache {

    private static final String TAG = "LruCache";

    private static String sCacheDir;
    private final LruCache<String, File> mLruCache;

    private DiskLruCache() {
        mLruCache = new LruCache<String, File>((int) (loadDiskUsableSpace() / 1048576)) {
            @Override
            protected int sizeOf(String key, File value) {
                int size = super.sizeOf(key, value);
                try {
                    //获取文件大小（单位：MB）
                    size = (int) (value.length() / 1048576);
                } catch (Throwable ignored) {
                }
                return size;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, File oldValue, File newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                try {
                    if (evicted || !oldValue.equals(newValue)) {
                        if (oldValue.delete()) {
                            resetCacheRecord();
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        };
        initCacheRecord();
    }

    public String getCacheDir() {
        return sCacheDir;
    }

    /**
     * 获取缓存文件
     *
     * @param url 文件来源地址
     * @return 缓存文件
     */
    public File get(String url) {
        try {
            return mLruCache.get(generateKey(url));
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 添加缓存记录
     *
     * @param url   文件来源地址
     * @param value 缓存文件
     */
    public void put(String url, File value) {
        try {
            String key = generateKey(url);
            //检查文件是否已经存在
            Map<String, File> snapshot = mLruCache.snapshot();
            if (snapshot != null && !snapshot.isEmpty()) {
                File file = snapshot.get(key);
                if (file != null && file.equals(value)) {
                    return;
                }
            }
            mLruCache.put(key, value);
            File cacheDir = new File(sCacheDir, TAG);
            if (cacheDir.exists() || cacheDir.createNewFile()) {
                RandomAccessFile raf = new RandomAccessFile(cacheDir, "rw");
                raf.seek(raf.length());
                raf.writeBytes(key + "," + value.getAbsolutePath() + "\n");
                raf.close();
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * 生成文件标识
     */
    private static String generateKey(String url) {
        StringBuilder cacheKey = new StringBuilder();
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            for (byte aByte : digest.digest()) {
                String hex = Integer.toHexString(0xFF & aByte);
                if (hex.length() == 1) {
                    cacheKey.append('0');
                }
                cacheKey.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            cacheKey.append(url.hashCode());
        }
        return cacheKey.toString();
    }

    /**
     * 初始化缓存记录
     */
    private void initCacheRecord() {
        try {
            RandomAccessFile raf = new RandomAccessFile(new File(sCacheDir, TAG), "r");
            String line;
            while (!TextUtils.isEmpty(line = raf.readLine())) {
                try {
                    String[] record = line.split(",");
                    File file = new File(record[1]);
                    if (file.exists()) {
                        //添加记录到LruCache
                        mLruCache.put(record[0], file);
                    }
                } catch (Exception ignored) {
                }
            }
            raf.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * 重置缓存记录
     */
    private void resetCacheRecord() {
        try {
            Map<String, File> snapshot = mLruCache.snapshot();
            if (snapshot != null) {
                RandomAccessFile raf = new RandomAccessFile(new File(sCacheDir, TAG), "rw");
                raf.setLength(0);
                for (String key : snapshot.keySet()) {
                    File file = snapshot.get(key);
                    if (file != null && file.exists()) {
                        raf.writeBytes(key + "," + file.getAbsolutePath() + "\n");
                    }
                }
                raf.close();
            }
        } catch (Exception ignored) {
        }
    }

    public static DiskLruCache getInstance() {
        return DiskLruCacheHolder.INSTANCE;
    }

    private static class DiskLruCacheHolder {
        @SuppressLint("StaticFieldLeak")
        private static final DiskLruCache INSTANCE = new DiskLruCache();
    }

    /**
     * 获取磁盘可用空间大小
     */
    private static long loadDiskUsableSpace() {
        long maxSize;
        try {
            sCacheDir = getAppContext().getExternalCacheDir().getAbsolutePath() + "/" + TAG;
            StatFs stats = new StatFs(sCacheDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                maxSize = stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
            } else {
                maxSize = (long) stats.getBlockSize() * stats.getAvailableBlocks();
            }
        } catch (Exception ignored) {
            maxSize = 200 * 1048576;
        }
        return maxSize;
    }

    @SuppressLint("PrivateApi")
    public static Context getAppContext() {
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
}
