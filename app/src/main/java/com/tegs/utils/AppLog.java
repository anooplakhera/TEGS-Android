package com.tegs.utils;

import android.util.Log;

/**
 * Created
 * by heena on 5/1/18.
 */

public class AppLog {

    private static boolean isDebug = true;

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }

}
