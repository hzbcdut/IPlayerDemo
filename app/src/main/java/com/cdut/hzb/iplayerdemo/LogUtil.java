package com.cdut.hzb.iplayerdemo;

import android.util.Log;


/**
 * Created by Administrator on 2016/5/9 0009.
 */
public class LogUtil {
    private static boolean showLog = true;

    public static void i(String tag, String msg) {
        if (showLog) {
            Log.i(tag, msg);
        }
    }
    public static void d(String tag,String msg){
        if(showLog){
            Log.d(tag,msg);
        }
    }
    public static void e(String tag,String msg){
        if(showLog){
            Log.e(tag,msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (showLog) {
            Log.e(tag, msg, e);
        }
    }
}
