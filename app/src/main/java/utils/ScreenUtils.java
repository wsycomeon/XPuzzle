package utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by WSY on 2016-02-02-0002.
 * 屏幕工具类：实现获取屏幕相关参数
 */
public class ScreenUtils {

    /**
     * 获取 矩阵信息
     */
    public static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);//将该屏幕的矩阵信息写入新建的矩阵！
        return metrics;
    }

    /**
     * 获取屏幕密度
     */
    public static float getDensity(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }
}
