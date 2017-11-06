package com.cdut.hzb.iplayerdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by hans on 2017/11/6 0006.
 */

public class App extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
