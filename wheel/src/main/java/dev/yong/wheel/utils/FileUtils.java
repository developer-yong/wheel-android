package dev.yong.wheel.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import dev.yong.wheel.AppManager;
import dev.yong.wheel.BuildConfig;

/**
 * @author CoderYong
 */
public class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    /**
     * 获取SD卡DaBai路径
     *
     * @return 默认SD卡路径
     */
    public static String getSDPath() {
        return getSDPath("");
    }

    /**
     * 获取SD卡指定文件夹路径
     *
     * @param parent 指定文件夹
     * @return 指定SD卡路径
     */
    public static String getSDPath(String parent) {
        String path;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 已挂载
            File pic = Environment.getExternalStorageDirectory();
            path = pic + File.separator + parent;
        } else {
            File cacheDir = AppManager.getInstance().getApplication().getCacheDir();
            path = cacheDir + File.separator + parent;
        }
        return create(path) == null ? "" : path;
    }

    /**
     * 创建文件
     *
     * @param parent 文件存放路径
     * @param name   文件名
     * @return 文件，文件路径无效时返回null
     */
    public static File create(String parent, String name) {
        if (!parent.endsWith("/")) {
            parent += "/";
        }
        return create(parent + name);
    }

    /**
     * 创建文件
     *
     * @param absolutePath 文件绝对路径
     * @return 文件，文件路径无效时返回null
     */
    public static File create(String absolutePath) {
        File file = new File(absolutePath);
        if (!file.exists()) {
            return file.mkdirs() ? file : null;
        }
        return file;
    }

    /**
     * 检测文件是否存在
     *
     * @param parent 文件存放路径
     * @param name   文件名
     * @return {@link File#exists()}
     */
    public static boolean exists(String parent, String name) {
        return new File(parent, name).exists();
    }

    /**
     * 检测文件是否存在
     *
     * @param absolutePath 文件绝对路径
     * @return {@link File#exists()}
     */
    public static boolean exists(String absolutePath) {
        return new File(absolutePath).exists();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 是否删除
     */
    public static boolean delete(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                //声明目录下所有的文件 files[];
                File[] files = file.listFiles();
                if (files != null) {
                    //遍历目录下所有的文件
                    for (File f : files) {
                        //把每个文件用这个方法进行迭代
                        delete(f);
                    }
                }
                //删除文件夹
                return file.delete();
            }
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param absolutePath 文件绝对路径
     * @return 是否删除
     */
    public static boolean delete(String absolutePath) {
        return delete(new File(absolutePath));
    }

    /**
     * 文件大小格式化
     *
     * @param length
     * @return
     */
    public static String formatFileSize(long length) {
        DecimalFormat df = new DecimalFormat("#.00");
        String size;
        if (length < 1024) {
            size = df.format((double) length) + "B";
        } else if (length < 1048576) {
            size = df.format((double) length / 1024) + "K";
        } else if (length < 1073741824) {
            size = df.format((double) length / 1048576) + "M";
        } else {
            size = df.format((double) length / 1073741824) + "G";
        }
        return size;
    }

    /**
     * 文件md5
     */
    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 把byte[]数组转换成十六进制字符串表示形式
     *
     * @param tmp 要转换的byte[]
     * @return 十六进制字符串表示形式
     */
    private static String byteToHexString(byte[] tmp) {

        // 用字节表示就是 16 个字节
        // 每个字节用 16 进制表示的话，使用两个字符，
        char[] c = new char[16 * 2];

        // 所以表示成 16 进制需要 32 个字符
        // 表示转换结果中对应的字符位置
        int k = 0;
        // 从第一个字节开始，对 MD5 的每一个字节
        for (int i = 0; i < 16; i++) {

            // 转换成 16 进制字符的转换
            // 取第 i 个字节
            byte byte0 = tmp[i];
            // 取字节中高 4 位的数字转换,
            c[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];

            // >>> 为逻辑右移，将符号位一起右移
            // 取字节中低 4 位的数字转换
            c[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        // 换后的结果转换为字符串
        return new String(c);
    }

    public static String getMD5(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = in.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] b = md.digest();
            in.close();
            return byteToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件重命名
     *
     * @param file 原文件
     * @param name 新文件名
     *             <P>不带后缀</P>
     * @return 新文件名
     */
    public static String rename(File file, String name) {
        return rename(file, name);
    }


    /**
     * 文件重命名
     *
     * @param file 原文件
     * @param path 新文件路径
     * @param name 新文件名
     *             <P>不带后缀</P>
     * @return 新文件名
     */
    public static String rename(File file, String path, String name) {
        if (file != null && file.exists()) {
            //创建新的文件名
            name = name + getSuffix(file.getAbsolutePath());
            //如果新的文件名与原文件名不相同则不做操作
            if (!name.equals(file.getName())) {
                String parent = TextUtils.isEmpty(path) ? file.getParent() : path;
                File newFile = new File(parent, name);
                if (newFile.exists()) {
                    newFile.delete();
                }
                return file.renameTo(newFile) ? name : file.getName();
            }
        }
        return "";
    }

    /**
     * 复制文件
     *
     * @param fromFile 原文件
     * @param toFile   新文件
     */
    public static void copy(File fromFile, File toFile) {
        try {
            FileInputStream ins = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            byte[] b = new byte[1024];
            int n;
            while ((n = ins.read(b)) != -1) {
                out.write(b, 0, n);
            }
            ins.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件移动
     *
     * @param file 原文件
     * @param path 新文件路径
     */

    public static File move(File file, String path) {
        if (file != null && file.exists() && !path.equals(file.getParent())) {
            File newFile = new File(path, file.getName());
            newFile.deleteOnExit();
            return file.renameTo(newFile) ? newFile : file;
        }
        return file;
    }

    /**
     * 获取文件后缀
     *
     * @param path 文件绝对路径
     */
    public static String getSuffix(String path) {
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf(".")).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * 建立一个文件类型与文件后缀名的匹配表
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


    /**
     * 根据路径打开文件
     *
     * @param context 上下文
     * @param path    文件路径
     */
    public static void openFileByPath(Context context, String path) {
        if (context == null || path == null) {
            return;
        }
        Intent intent = new Intent();
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //文件的类型
        String type = "*/*";
        for (String[] strings : MATCH_ARRAY) {
            String suffix = getSuffix(path);
            //判断文件的格式
            if (!TextUtils.isEmpty(suffix)) {
                if (suffix.toLowerCase().contains(strings[0])) {
                    type = strings[1];
                    break;
                }
            } else {
                if (path.contains(strings[0])) {
                    type = strings[1];
                    break;
                }
            }
        }
        try {
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(
                        context, context.getApplicationInfo().packageName + ".FileProvider", new File(path));
                intent.setDataAndType(contentUri, type);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //设置intent的data和Type属性
                intent.setDataAndType(Uri.fromFile(new File(path)), type);
            }
            //跳转
            context.startActivity(intent);
        } catch (Exception e) {
            //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
