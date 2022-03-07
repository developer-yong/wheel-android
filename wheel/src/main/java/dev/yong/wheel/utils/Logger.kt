@file:Suppress("unused")

package dev.yong.wheel.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import dev.yong.wheel.BuildConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * @author coderyong
 */
object Logger {
    private val TAG = Logger::class.java.simpleName

    /**
     * Drawing toolbox
     */
    private const val TOP_LEFT_CORNER = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER = '╟'
    private const val HORIZONTAL_LINE = '║'
    private const val DOUBLE_DIVIDER = "════════════════════════════════════════════════════════"
    private const val SINGLE_DIVIDER = "────────────────────────────────────────────────────────"
    private const val TOP_BORDER =
        TOP_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val BOTTOM_BORDER =
        BOTTOM_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val MIDDLE_BORDER =
        MIDDLE_CORNER.toString() + SINGLE_DIVIDER + SINGLE_DIVIDER

    @JvmStatic
    var DEBUG = BuildConfig.DEBUG

    @JvmStatic
    fun v(vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.VERBOSE, *msg)
        }
    }

    @JvmStatic
    fun d(vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.DEBUG, *msg)
        }
    }

    @JvmStatic
    fun i(vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.INFO, *msg)
        }
    }

    @JvmStatic
    fun w(vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.WARN, *msg)
        }
    }

    @JvmStatic
    fun w(tr: Throwable, vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.WARN, *msg, Log.getStackTraceString(tr))
        }
    }

    @JvmStatic
    fun e(vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.ERROR, *msg)
        }
    }

    @JvmStatic
    fun e(tr: Throwable, vararg msg: String) {
        if (DEBUG) {
            singleLog(Log.ERROR, *msg, Log.getStackTraceString(tr))
        }
    }

    @JvmStatic
    fun json(jsonMsg: String) {
        var json = jsonMsg
        if (DEBUG) {
            if (json.startsWith("{") || json.startsWith("[")) {
                try {
                    json = json.trim { it <= ' ' }
                    var message = ""
                    if (json.startsWith("{")) {
                        message = JSONObject(json).toString(4)
                    }
                    if (json.startsWith("[")) {
                        message = JSONArray(json).toString(4)
                    }
                    singleLog(Log.INFO, message)
                } catch (e: JSONException) {
                    e(e)
                }
            }
        }
    }

    @JvmStatic
    fun xml(xml: String) {
        if (DEBUG) {
            try {
                val xmlInput: Source = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                singleLog(Log.INFO, xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
            } catch (e: TransformerException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun file(context: Context, msg: String, tr: Throwable) {
        //如果外部储存可用
        var path = context.filesDir.path + "/Logs"
        if (!Environment.isExternalStorageRemovable()) {
            //获得外部存储路径,默认路径为
            val dir = context.getExternalFilesDir(null)
            if (dir == null) {
                e(IllegalStateException("ExternalFilesDir is null"))
            }
            path = dir!!.path + "/Logs"
        }
        file(path, msg, tr)
    }

    @JvmStatic
    fun file(path: String, fileMsg: String, tr: Throwable) {
        var msg = fileMsg
        val parent = File(path)
        if (!parent.exists()) {
            val mkdirs = parent.mkdirs()
            check(!(!mkdirs && !parent.exists())) { "LogFile not create" }
        }
        var bw: BufferedWriter? = null
        try {
            val fileName = "$TAG.log"
            val logFile = File(path, fileName)
            //这里的第二个参数代表追加还是覆盖，true为追加，false为覆盖
            val fos = FileOutputStream(logFile, true)
            bw = BufferedWriter(OutputStreamWriter(fos))
            //日期格式;
            val format = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            )
            msg = format.format(Date()) + " — " + msg
            bw.write(
                """
    $msg
    ${Log.getStackTraceString(tr)}
    
    """.trimIndent()
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val useMethodName: String
        get() {
            val trace = Thread.currentThread().stackTrace
            var usePosition = -1
            for (i in trace.indices) {
                val className = trace[i].className
                if (className.contains(Logger::class.java.name)) {
                    usePosition = i
                } else if (usePosition != -1) {
                    usePosition = i
                    break
                }
            }
            return trace[usePosition].className +
                    "." +
                    trace[usePosition].methodName +
                    "  (" +
                    trace[usePosition].fileName +
                    ":" +
                    trace[usePosition].lineNumber +
                    ")"
        }

    @Synchronized
    private fun log(priority: Int, vararg msg: String) {
        TEMP_TAG = '@'
        if (DEBUG) {
            Log.println(priority, resolveTag(), TOP_BORDER)
            Log.println(
                priority,
                resolveTag(),
                HORIZONTAL_LINE.toString() + "\t" + useMethodName
            )
            Log.println(priority, resolveTag(), MIDDLE_BORDER)
            for (m in msg) {
                var property: String? = System.getProperty("line.separator")
                if (property == null) property = "\n"
                val lines: Array<String> = m.split(property.toRegex()).toTypedArray()
                for (line in lines) {
                    longLog(priority, HORIZONTAL_LINE.toString() + "\t" + line)
                }
            }
            Log.println(priority, resolveTag(), BOTTOM_BORDER)
        }
    }

    /**
     * 单条日志打印
     *
     * @param priority 打印标签[Log.println]
     * @param msg     需要打印的信息
     */
    @JvmStatic
    fun singleLog(priority: Int, vararg msg: String) {
        if (DEBUG) {
            val message = StringBuilder()
            for (m in msg) {
                var property: String? = System.getProperty("line.separator")
                if (property == null) property = "\n"
                val lines: Array<String> =
                    m.split(property.toRegex()).toTypedArray()
                for (line in lines) {
                    message.append(HORIZONTAL_LINE.toString() + "\t").append(line)
                        .append("\n")
                }
            }
            val m = """ -> 
$TOP_BORDER
$HORIZONTAL_LINE	$useMethodName
$MIDDLE_BORDER
$message$BOTTOM_BORDER"""
            longLog(priority, m)
        }
    }

    /**
     * 对系统日志打印做了扩展，将长度超出系统限制的日志分段打印
     *
     * @param priority [Log.println]
     * @param msg      日志内容
     */
    @Synchronized
    private fun longLog(priority: Int, msg: String) {
        if (!TextUtils.isEmpty(msg)) {
            val maxLength = 3072
            if (msg.length > maxLength) {
                var i = 0
                var m: String
                while (i < msg.length) {
                    //当前截取的长度<总长度则继续截取最大的长度来打印
                    m = if (i + maxLength < msg.length) {
                        (if (i == 0) "" else HORIZONTAL_LINE.toString() + "\t") + msg.substring(
                            i,
                            i + maxLength
                        )
                    } else {
                        //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                        HORIZONTAL_LINE.toString() + "\t" + msg.substring(i)
                    }
                    i += maxLength
                    Log.println(priority, resolveTag(), m)
                }
            } else {
                Log.println(priority, resolveTag(), msg)
            }
        }
    }

    private var TEMP_TAG = '@'

    /**
     * 解决在 AndroidStudio v3.1 以上， Logcat 输出的日志无法对齐的问题
     *
     * @return 因为多次使用同一个TAG 日志会被合并为同一条打印输出，所以对原有TAG进行了包装区分。例：-A-TAG
     */
    private fun resolveTag(): String {
        if (TEMP_TAG >= 'Z') {
            TEMP_TAG = '@'
        }
        return "-" + ++TEMP_TAG + "-" + TAG
    }
}