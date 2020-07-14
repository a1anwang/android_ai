package com.a1anwang.ai_master;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2019/9/23.
 */
public class NavigationManager {

    private static final String TAG = NavigationManager.class.getSimpleName();

    private volatile static NavigationManager instance;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            synchronized (NavigationManager.class) {
                if (instance == null) {
                    instance = new NavigationManager();
                }
            }
        }
        return instance;
    }

    private int rawHeight=-1;

    private Application application;
    public void init(Application application) {
        this.application=application;
        rawHeight=getNavHeight(application);
    }
    private List<NaviStateListener> naviStateListenerList;

    public void addNaviStateListener(final Activity activity, final NaviStateListener naviStateListener) {
        if (activity == null) {
            return;
        }
        if(naviStateListenerList==null){
            naviStateListenerList=new ArrayList<>();
        }
        if(!naviStateListenerList.contains(naviStateListener)) {
            naviStateListenerList.add(naviStateListener);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets windowInsets) {
                    boolean isShowing = false;
                    int b = 0;
                    if (windowInsets != null) {
                        b = windowInsets.getSystemWindowInsetBottom();
                        isShowing = (b == rawHeight);
                    }
                    for (NaviStateListener listener:naviStateListenerList){
                        listener.onVisibleChanged(isShowing);
                    }
                    return windowInsets;
                }
            });
        }
    }


    public int getNavHeight() {
        if(rawHeight==-1){
            rawHeight=getNavHeight(application);
        }
        return rawHeight;
    }

    private int getNavHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        return 0;
    }
    public void removeListener(NaviStateListener naviStateListener){
        if(naviStateListenerList!=null&&naviStateListenerList.contains(naviStateListener)){
            naviStateListenerList.remove(naviStateListener);
        }
    }
    public interface NaviStateListener{
        void onVisibleChanged(boolean visible);
    }

}
