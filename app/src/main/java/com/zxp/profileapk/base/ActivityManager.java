package com.zxp.profileapk.base;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Activity堆栈管理类
 */
public class ActivityManager {

    private static Stack<Activity> activityStack;
    private volatile static ActivityManager manager;

    private ActivityManager() {
        activityStack = new Stack<>();
    }

    public static ActivityManager getInstance(){
        if(manager == null){
            synchronized (ActivityManager.class){
                if(manager == null){
                    manager = new ActivityManager();
                }
            }
        }
        return manager;
    }

    /**
     * 添加activity到堆栈
     * @param activity
     */
    public void addActivity(Activity activity){
        if(!activityStack.contains(activity)) {
            activityStack.add(activity);
        }
    }

    /**
     * 获取当前activity（最后一个入栈）
     * @return
     */
    public Activity currentActivity(){
        if(!activityStack.isEmpty())
            return activityStack.lastElement();
        else
            return null;
    }

    /**
     *当前activity出栈
     * @return
     */
    public Activity popActivity(){
        if(!activityStack.isEmpty())
            return activityStack.pop();
        else
            return null;
    }

    /**
     * 将指定activity移出堆栈并结束
     * @param activity
     */
    public void finishActivity(Activity activity){
        if(activity != null && !activityStack.isEmpty()){
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 根据类名获取activity
     * @param cls
     * @return
     */
    public Activity getActivity(Class<?> cls){
        Activity activity = null;
        if(!activityStack.isEmpty()){
            for(Activity a:activityStack){
                if(a.getClass() != cls)
                    continue;
                else
                    activity = a;
            }
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
//获取到当前Activity
        Activity activity = activityStack.lastElement();
//结束指定Activity
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        List<Activity> activities = new ArrayList<Activity>();
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
// finishActivity(activity);
                activities.add(activity);
            }
        }
// 结束所有类名相同activity
        activityStack.removeAll(activities);
        for (Activity activity : activities) {
            finishActivity(activity);
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     * 这里关闭的是所有的Activity，没有关闭Activity之外的其他组件;
     * android.os.Process.killProcess(android.os.Process.myPid())
     * 杀死进程关闭了整个应用的所有资源，有时候是不合理的，通常是用
     * 堆栈管理Activity;System.exit(0)杀死了整个进程，这时候活动所占的
     * 资源也会被释放,它会执行所有通过Runtime.addShutdownHook注册的shutdown hooks.
     * 它能有效的释放JVM之外的资源,执行清除任务，运行相关的finalizer方法终结对象，
     * 而finish只是退出了Activity。
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
//DalvikVM的本地方法
// 杀死该应用进程
//android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
//这些方法如果是放到主Activity就可以退出应用，如果不是主Activity
//就是退出当前的Activity
        } catch (Exception e) {
        }
    }
}
