package com.ljp.testplaytheme;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

public class HHQYApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        refWatcher = LeakCanary.install(this);
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        HHQYApplication application = (HHQYApplication) context
                .getApplicationContext();
        return application.refWatcher;
    }

    private static HHQYApplication application;

    public static HHQYApplication getInstance() {
        if (application == null) {
            application = new HHQYApplication();
        }
        return application;
    }

    private List<Activity> activityList = new ArrayList<Activity>();

    public void addActivity(Activity activity) {
        activityList.add(activity);
        activity = null;
    }

    @Deprecated
    public void removeActivity(Activity activity) {
        for (int i = 0; i < activityList.size(); i++) {
            if (null != activityList.get(i) && activityList.get(i) == activity) {
                activityList.get(i).finish();
                activityList.remove(i);
            }
        }
        activity = null;
    }

    public void clearActivity() {
        for (int i = 0; i < activityList.size(); i++) {
            if (null != activityList.get(i)) {
                activityList.get(i).finish();
            }
        }
        activityList.clear();
    }
}
