package com.bestcoolweather.android.util;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by zxit on 2018/4/19.
 */

public class ScreenUtil {

    /**
     * 获取手机状态栏的高度
     */
    public static int getStatusBarHeight(Context context){
        Class<?> c;
        Object obj;
        Field filed;
        int x,statusBarHeight=0;

        try {
            c=Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            filed = c.getField("status_bar_height");
            x=Integer.parseInt(filed.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return statusBarHeight;
    }
}
