package dev.yong.wheel.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.yong.wheel.BuildConfig;

/**
 * @author coderyong
 */
public class Logger {

    private static String TAG = Logger.class.getSimpleName();

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    public static boolean DEBUG = BuildConfig.DEBUG;

    private Logger() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    public static void v(String... msg) {
        if (DEBUG) {
            log(Log.VERBOSE, msg);
        }
    }

    public static void d(String... msg) {
        if (DEBUG) {
            log(Log.DEBUG, msg);
        }
    }

    public static void i(String... msg) {
        if (DEBUG) {
            log(Log.INFO, msg);
        }
    }

    public static void w(String... msg) {
        if (DEBUG) {
            log(Log.WARN, msg);
        }
    }

    public static void w(Throwable tr, String msg) {
        if (DEBUG) {
            log(Log.WARN, msg + "\n" + Log.getStackTraceString(tr));
        }
    }

    public static void e(String... msg) {
        if (DEBUG) {
            log(Log.ERROR, msg);
        }
    }

    public static void e(Throwable tr, String... msg) {
        if (DEBUG) {
            log(Log.ERROR, msg + "\n" + Log.getStackTraceString(tr));
        }
    }

    public static void json(String json) {
        if (DEBUG) {
            if (json.startsWith("{") || json.startsWith("[")) {
                try {
                    json = json.trim();
                    String message = "";
                    if (json.startsWith("{")) {
                        message = new JSONObject(json).toString(4);
                    }
                    if (json.startsWith("[")) {
                        message = new JSONArray(json).toString(4);
                    }
                    log(Log.INFO, message);
                } catch (JSONException e) {
                    e(e, e.getMessage());
                }
            }
        }
    }

    public static void file(Context context, String msg, Throwable tr) {
        //如果外部储存可用
        String path = context.getFilesDir().getPath() + "/Logs";
        if (!Environment.isExternalStorageRemovable()) {
            //获得外部存储路径,默认路径为
            File dir = context.getExternalFilesDir(null);
            if (dir == null) {
                e(tr, msg);
                throw new IllegalStateException("ExternalFilesDir is null");
            }
            path = dir.getPath() + "/Logs";
        }
        file(path, msg, tr);
    }

    public static void file(String path, String msg, Throwable tr) {
        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        BufferedWriter bw = null;
        try {
            String fileName = TAG + ".log";
            File logFile = new File(path, fileName);
            //这里的第二个参数代表追加还是覆盖，true为追加，false为覆盖
            FileOutputStream fos = new FileOutputStream(logFile, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            //日期格式;
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            msg = format.format(new Date()) + " — " + msg;
            bw.write(msg + "\n" + Log.getStackTraceString(tr) + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();//关闭缓冲流
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getUseMethodName() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int usePosition = 3;
        for (int i = 0; i < trace.length; i++) {
            String className = trace[i].getClassName();
            if (className.contains(Logger.class.getName())) {
                usePosition += i;
                break;
            }
        }
        return trace[usePosition].getClassName() +
                "." +
                trace[usePosition].getMethodName() +
                "  (" +
                trace[usePosition].getFileName() +
                ":" +
                trace[usePosition].getLineNumber() +
                ")";
    }

    private static synchronized void log(int priority, String... msgs) {
        TEMP_TAG = '@';
        if (DEBUG && msgs != null) {
            Log.println(priority, resolveTag(TAG), TOP_BORDER);
            Log.println(priority, resolveTag(TAG), HORIZONTAL_LINE + "\t" + getUseMethodName());
            Log.println(priority, resolveTag(TAG), MIDDLE_BORDER);

            for (String msg : msgs) {
                if (msg != null) {
                    String[] lines = msg.split(System.getProperty("line.separator"));
                    for (String line : lines) {
                        longLog(priority, HORIZONTAL_LINE + "\t" + line);
                    }
                }
            }
            Log.println(priority, resolveTag(TAG), BOTTOM_BORDER);
        }
    }

    /**
     * 单条日志打印
     *
     * @param priority 打印标签{@link Log#println(int, String, String)}
     * @param msgs     需要打印的信息
     */
    public static void singleLog(int priority, String... msgs) {
        if (DEBUG) {
            StringBuilder message = new StringBuilder();
            if (msgs != null) {
                for (String msg : msgs) {
                    if (msg != null) {
                        String[] lines = msg.split(System.getProperty("line.separator"));
                        for (String line : lines) {
                            message.append(HORIZONTAL_LINE + "\t").append(line).append("\n");
                        }
                    }
                }
            }
            String msg = " -> \n" +
                    TOP_BORDER + "\n" +
                    HORIZONTAL_LINE + "\t" +
                    getUseMethodName() + "\n" +
                    MIDDLE_BORDER + "\n" +
                    message.toString() +
                    BOTTOM_BORDER;

            longLog(priority, msg);
        }
    }

    /**
     * 对系统日志打印做了扩展，将长度超出系统限制的日志分段打印
     *
     * @param priority {@link Log#println(int, String, String)}
     * @param msg      日志内容
     */
    private static synchronized void longLog(int priority, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            int maxLength = 3072;
            if (msg.length() > maxLength) {
                int i = 0;
                String m;
                while (i < msg.length()) {
                    //当前截取的长度<总长度则继续截取最大的长度来打印
                    if (i + maxLength < msg.length()) {
                        m = (i == 0 ? "" : HORIZONTAL_LINE + "\t") + msg.substring(i, i + maxLength);
                    } else {
                        //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                        m = HORIZONTAL_LINE + "\t" + msg.substring(i, msg.length());
                    }

                    i += maxLength;
                    Log.println(priority, resolveTag(TAG), m);
                }
            } else {
                Log.println(priority, resolveTag(TAG), msg);
            }
        }
    }

    private static char TEMP_TAG = '@';

    /**
     * 解决在 AndroidStudio v3.1 以上， Logcat 输出的日志无法对齐的问题
     *
     * @param tag 原TAG
     * @return 因为多次使用同一个TAG 日志会被合并为同一条打印输出，所以对原有TAG进行了包装区分。例：-A-TAG
     */
    private static String resolveTag(String tag) {
        if (TEMP_TAG >= 'Z') {
            TEMP_TAG = '@';
        }
        return "-" + (++TEMP_TAG) + "-" + tag;
    }
}
