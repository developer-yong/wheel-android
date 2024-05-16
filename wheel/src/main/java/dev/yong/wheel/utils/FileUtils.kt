@file:Suppress("unused")

package dev.yong.wheel.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.*
import java.text.DecimalFormat
import java.util.*

/**
 * @author coderyong
 */
object FileUtils {

    /**
     * 创建文件
     *
     * @param parent 文件存放路径
     * @param name   文件名
     * @return 文件，文件路径无效时返回null
     */
    @JvmStatic
    fun create(parent: String, name: String): File? {
        if (!parent.endsWith("/")) {
            parent.plus("/")
        }
        return create(parent + name)
    }

    /**
     * 创建文件
     *
     * @param absolutePath 文件绝对路径
     * @return 文件，文件路径无效时返回null
     */
    @JvmStatic
    fun create(absolutePath: String): File? {
        val file = File(absolutePath)
        return if (!file.exists()) {
            if (file.isDirectory) {
                if (file.mkdirs()) file else null
            } else {
                try {
                    if (file.createNewFile()) file else null
                } catch (e: IOException) {
                    null
                }
            }
        } else file
    }

    /**
     * 检测文件是否存在
     *
     * @param parent 文件存放路径
     * @param name   文件名
     * @return [File.exists]
     */
    @JvmStatic
    fun exists(parent: String?, name: String): Boolean {
        return File(parent, name).exists()
    }

    /**
     * 检测文件是否存在
     *
     * @param absolutePath 文件绝对路径
     * @return [File.exists]
     */
    @JvmStatic
    fun exists(absolutePath: String): Boolean {
        return File(absolutePath).exists()
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 是否删除
     */
    @JvmStatic
    fun delete(file: File?): Boolean {
        if (file != null && file.exists()) {
            if (file.isFile) {
                return file.delete()
            } else if (file.isDirectory) {
                //声明目录下所有的文件 files[];
                val files = file.listFiles()
                if (files != null) {
                    //遍历目录下所有的文件
                    for (f in files) {
                        //把每个文件用这个方法进行迭代
                        delete(f)
                    }
                }
                //删除文件夹
                return file.delete()
            }
        }
        return true
    }

    /**
     * 删除文件
     *
     * @param absolutePath 文件绝对路径
     * @return 是否删除
     */
    @JvmStatic
    fun delete(absolutePath: String): Boolean {
        return delete(File(absolutePath))
    }

    /**
     * 文件大小格式化
     *
     * @param length 文件字节长度
     * @return 格式字符串
     */
    @JvmStatic
    fun formatFileSize(length: Long): String {
        val df = DecimalFormat("#.00")
        return when {
            length < 1024 -> {
                df.format(length.toDouble()) + "B"
            }
            length < 1048576 -> {
                df.format(length.toDouble() / 1024) + "K"
            }
            length < 1073741824 -> {
                df.format(length.toDouble() / 1048576) + "M"
            }
            else -> {
                df.format(length.toDouble() / 1073741824) + "G"
            }
        }
    }

    /**
     * 文件重命名
     *
     * @param file 原文件
     * @param name 新文件名
     * @return 新文件名
     */
    @JvmStatic
    fun rename(file: File, name: String): String {
        val destFile = File(file.parent, name)
        return if (file.renameTo(destFile)) destFile.name else file.name
    }

    /**
     * 复制文件
     *
     * @param fromFile 原文件
     * @param toFile   新文件
     */
    @JvmStatic
    fun copy(fromFile: File, toFile: File) {
        try {
            val ins = FileInputStream(fromFile)
            val out = FileOutputStream(toFile)
            val b = ByteArray(1024)
            var n: Int
            while (ins.read(b).also { n = it } != -1) {
                out.write(b, 0, n)
            }
            ins.close()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 文件移动
     *
     * @param file 原文件
     * @param path 新文件路径
     */
    @JvmStatic
    fun move(file: File, path: String): File {
        if (file.exists() && path != file.parent) {
            val destFile = File(path, file.name)
            if (!destFile.exists() || destFile.delete()) {
                return if (create(path) != null && file.renameTo(destFile)) destFile else file
            }
        }
        return file
    }

    /**
     * 获取文件后缀
     *
     * @param path 文件绝对路径
     */
    @JvmStatic
    fun getSuffix(path: String): String {
        return if (path.contains(".")) {
            path.substring(path.lastIndexOf(".")).lowercase(Locale.getDefault())
        } else {
            ""
        }
    }

    /**
     * 建立一个文件类型与文件后缀名的匹配表
     */
    private val MATCH_ARRAY = arrayOf(
        arrayOf(".3gp", "video/3gpp"),
        arrayOf(".apk", "application/vnd.android.package-archive"),
        arrayOf(".asf", "video/x-ms-asf"),
        arrayOf(".avi", "video/x-msvideo"),
        arrayOf(".bin", "application/octet-stream"),
        arrayOf(".bmp", "image/bmp"),
        arrayOf(".c", "text/plain"),
        arrayOf(".class", "application/octet-stream"),
        arrayOf(".conf", "text/plain"),
        arrayOf(".cpp", "text/plain"),
        arrayOf(".doc", "application/msword"),
        arrayOf(".exe", "application/octet-stream"),
        arrayOf(".gif", "image/gif"),
        arrayOf(".gtar", "application/x-gtar"),
        arrayOf(".gz", "application/x-gzip"),
        arrayOf(".h", "text/plain"),
        arrayOf(".htm", "text/html"),
        arrayOf(".html", "text/html"),
        arrayOf(".jar", "application/java-archive"),
        arrayOf(".java", "text/plain"),
        arrayOf(".jpeg", "image/jpeg"),
        arrayOf(".jpg", "image/jpeg"),
        arrayOf(".js", "application/x-javascript"),
        arrayOf(".log", "text/plain"),
        arrayOf(".m3u", "audio/x-mpegurl"),
        arrayOf(".m4a", "audio/mp4a-latm"),
        arrayOf(".m4b", "audio/mp4a-latm"),
        arrayOf(".m4p", "audio/mp4a-latm"),
        arrayOf(".m4u", "video/vnd.mpegurl"),
        arrayOf(".m4v", "video/x-m4v"),
        arrayOf(".mov", "video/quicktime"),
        arrayOf(".mp2", "audio/x-mpeg"),
        arrayOf(".mp3", "audio/x-mpeg"),
        arrayOf(".mp4", "video/mp4"),
        arrayOf(".mpc", "application/vnd.mpohun.certificate"),
        arrayOf(".mpe", "video/mpeg"),
        arrayOf(".mpeg", "video/mpeg"),
        arrayOf(".mpg", "video/mpeg"),
        arrayOf(".mpg4", "video/mp4"),
        arrayOf(".mpga", "audio/mpeg"),
        arrayOf(".msg", "application/vnd.ms-outlook"),
        arrayOf(".ogg", "audio/ogg"),
        arrayOf(".pdf", "application/pdf"),
        arrayOf(".png", "image/png"),
        arrayOf(".pps", "application/vnd.ms-powerpoint"),
        arrayOf(".ppt", "application/vnd.ms-powerpoint"),
        arrayOf(".prop", "text/plain"),
        arrayOf(".rar", "application/x-rar-compressed"),
        arrayOf(".rc", "text/plain"),
        arrayOf(".rmvb", "audio/x-pn-realaudio"),
        arrayOf(".rtf", "application/rtf"),
        arrayOf(".sh", "text/plain"),
        arrayOf(".tar", "application/x-tar"),
        arrayOf(".tgz", "application/x-compressed"),
        arrayOf(".txt", "text/plain"),
        arrayOf(".wav", "audio/x-wav"),
        arrayOf(".wma", "audio/x-ms-wma"),
        arrayOf(".wmv", "audio/x-ms-wmv"),
        arrayOf(".wps", "application/vnd.ms-works"),
        arrayOf(".xml", "text/plain"),
        arrayOf(".z", "application/x-compress"),
        arrayOf(".zip", "application/zip"),
        arrayOf("", "*/*")
    )

    /**
     * 根据路径打开文件
     *
     * @param context      上下文
     * @param absolutePath 文件绝对路径
     */
    @JvmStatic
    fun open(context: Context?, absolutePath: String?) {
        if (context == null || absolutePath == null) {
            return
        }
        val intent = Intent()
        //设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        //文件的类型
        var type = "*/*"
        for (strings in MATCH_ARRAY) {
            val suffix = getSuffix(absolutePath)
            //判断文件的格式
            if (!TextUtils.isEmpty(suffix)) {
                if (suffix.lowercase(Locale.getDefault()).contains(strings[0])) {
                    type = strings[1]
                    break
                }
            } else {
                if (absolutePath.contains(strings[0])) {
                    type = strings[1]
                    break
                }
            }
        }
        try {
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                    context,
                    context.applicationInfo.packageName + ".FileProvider",
                    File(absolutePath)
                )
                intent.setDataAndType(contentUri, type)
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //设置intent的data和Type属性
                intent.setDataAndType(Uri.fromFile(File(absolutePath)), type)
            }
            //跳转
            context.startActivity(intent)
        } catch (e: Exception) {
            //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * 根据指定文件获取Uri
     *
     * @param context  Context
     * @param file     指定文件
     * @param mimeType 文件类型
     */
    fun getUriForFile(context: Context, file: File, mimeType: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                context.applicationInfo.packageName + ".FileProvider",
                file
            )
        } else Uri.fromFile(file)
    }

    /**
     * 解析Uri到文件
     *
     * @param context   Context
     * @param inUri     输入Uri
     * @param outFile   输出文件
     */
    fun parseForUri(context: Context, inUri: Uri, outFile: File) {
        try {
            if (!outFile.exists() && !outFile.createNewFile()) {
                return
            }
            val input = context.contentResolver.openInputStream(inUri) ?: return
            var out: FileOutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                out = FileOutputStream(outFile)
                android.os.FileUtils.copy(input, out)
            } else {
                val arrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024 * 10)
                while (true) {
                    val len = input.read(buffer)
                    if (len == -1) {
                        break
                    }
                    arrayOutputStream.write(buffer, 0, len)
                }
                arrayOutputStream.close()
                val dataByte = arrayOutputStream.toByteArray()
                if (dataByte.isNotEmpty()) {
                    out = FileOutputStream(outFile)
                    out.write(dataByte)
                }
            }
            out?.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 获取SD卡路径
 *
 * @return 默认SD卡路径
 */
val Context.sDPath: String
    get() = getSDPath("")

/**
 * 获取SD卡指定文件夹路径
 *
 * @param parent 指定文件夹
 * @return 指定SD卡路径
 */
fun Context.getSDPath(parent: String): String {
    val path: String
    val state = Environment.getExternalStorageState()
    path = if (state == Environment.MEDIA_MOUNTED) {
        // 已挂载
        this.getExternalFilesDir(null).toString() + File.separator + parent
    } else {
        this.applicationContext.cacheDir.toString() + File.separator + parent
    }
    return if (FileUtils.create(path) == null) "" else path
}