package com.yurnero.annotationprefs;

import android.app.Application;


/**
 * description:.
 *
 * @author：qiangjie
 * @version：17/7/16 上午12:24
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        MySharedPreferences.init(getApplicationContext());
    }
}
