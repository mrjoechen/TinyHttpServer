package com.chenqiao.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


import com.chenqiao.App;
import com.chenqiao.tinyhttpserver.R;

import java.lang.ref.WeakReference;

/**
 * Show Toast.
 * Created by chenqiao on 18/6/11.
 */
public class T {

    private T() {
    }

    // an weak handle of the real toast object, we check it every time before we
    // pop up a new toast
    // to cancel the old one
    private static WeakReference<Toast> sCache = null;
    // run all show action in ui thread to eliminate lock needed to access sCache
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static final long MAIN_THREAD_ID = Looper.getMainLooper().getThread().getId();

    public static void show(Context context, int resId) {
        String str = context.getString(resId);
        show(context, str);
    }

    /**
     * 显示指定的字符串，若当前有正在显示的字符串Toast，直接替换文字，显示时间为 {@link Toast#LENGTH_SHORT}
     *
     * @param context
     * @param cs
     */
    public static void show(Context context, CharSequence cs) {
        show(context, cs, Toast.LENGTH_SHORT, false);
    }

    /**
     * 收起正在显示的toast
     */
    public static void dismiss() {
        Toast curToast;
        if (sCache != null && (curToast = sCache.get()) != null) {
            curToast.cancel();
            sCache = null;
        }
    }

    public static void show(final Context context, final CharSequence string, final boolean newToast) {
        if (Thread.currentThread().getId() == MAIN_THREAD_ID) {
            runInMainThread(context, string, Toast.LENGTH_SHORT, newToast);
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    runInMainThread(context, string, Toast.LENGTH_SHORT, newToast);
                }
            });
        }
    }

    private static void show(final Context context, final CharSequence string, final int duration, final boolean newToast) {
        if (Thread.currentThread().getId() == MAIN_THREAD_ID) {
            runInMainThread(context, string, duration, newToast);
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    runInMainThread(context, string, duration, newToast);
                }
            });
        }
    }

    // 只能在Ui线程执行
    private static void runInMainThread(Context context, CharSequence string, int duration, boolean newToast) {
        Toast oldToast;
        if (context == null) {
            context = App.getInstance();
        }
        if (context == null) {
            return;
        }

        if (sCache != null && (oldToast = sCache.get()) != null) {
            if (newToast) {
                oldToast.cancel();
            } else {
                ((TextView) oldToast.getView()).setText(string);
                oldToast.setDuration(duration);
                oldToast.show();
                return;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        if (inflater == null) {
            return;
        }
        @SuppressLint("InflateParams") TextView textView = (TextView) inflater.inflate(
                R.layout.default_toast, null);
        textView.setText(string);
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        sCache = new WeakReference<>(toast);
        toast.show();
    }
}
