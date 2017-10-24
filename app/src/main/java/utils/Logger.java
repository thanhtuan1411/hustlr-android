package utils;

/**
 * Created by tuando on 10/19/17.
 */

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public final class Logger {
    public static final String TAG = "Urbani";

    public static void v(String msg, Object... args) {
        log(Log.VERBOSE, msg, args);
    }

    public static void v(String msg) {
        log(Log.VERBOSE, msg);
    }

    public static void d(String msg, Object... args) {
        log(Log.DEBUG, msg, args);
    }

    public static void d(String msg) {
        log(Log.DEBUG, msg);
    }

    public static void i(String msg, Object... args) {
        log(Log.INFO, msg, args);
    }

    public static void i(String msg) {
        log(Log.INFO, msg);
    }

    public static void w(String msg, Object... args) {
        log(Log.WARN, msg, args);
    }

    public static void w(String msg) {
        log(Log.WARN, msg);
    }

    public static void w(Throwable msg) {
        log(Log.WARN, msg);
    }

    public static void d(Throwable msg) {
        log(Log.DEBUG, msg);
    }

    public static void e(String msg, Object... args) {
        log(Log.ERROR, msg, args);
    }

    public static void e(String msg) {
        log(Log.ERROR, msg);
    }

    public static void e(Throwable msg) {
        log(Log.ERROR, msg);
    }

    private static void log(int level, Object msg, Object... args) {
        if(BuildConfig.DEBUG) {
            if (msg instanceof Throwable) {
                Throwable throwable = (Throwable) msg;
                Log.println(level, TAG, Log.getStackTraceString(throwable));
            } else {

                StringBuilder sb = null;

                if (args == null || args.length == 0) {
                    sb = new StringBuilder(getLocation() + msg);
                } else {
                    sb = new StringBuilder(getLocation() + getLocation() + String.format(msg.toString(), args));
                }

                if (sb.length() > 4000) {
                    Log.v(TAG, "sb.length = " + sb.length());
                    int chunkCount = sb.length() / 4000;     // integer division
                    for (int i = 0; i <= chunkCount; i++) {
                        int max = 4000 * (i + 1);
                        if (max >= sb.length()) {
                            Log.println(level, TAG, "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i));
                        } else {
                            Log.println(level, TAG, "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i, max));
                        }
                    }
                } else {
                    if (args == null || args.length == 0) {
                        Log.println(level, TAG, getLocation() + msg);
                    } else {
                        Log.println(level, TAG, getLocation() + String.format(msg.toString(), args));
                    }
                }

            /*if (args == null || args.length == 0) {
                Log.println(level, TAG, getLocation() + msg);
            } else {
                Log.println(level, TAG, getLocation() + String.format(msg.toString(), args));
            }*/

            }
        }
    }

    private static String getLocation() {
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
                // no need, it`s not fatal
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            String simpleName = clazz.getSimpleName();
            if (!TextUtils.isEmpty(simpleName)) return simpleName;

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}
