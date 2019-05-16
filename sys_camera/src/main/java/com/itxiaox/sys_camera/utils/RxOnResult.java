package com.itxiaox.sys_camera.utils;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;


/**
 * @Title: 通过回调方法实现Activity的onActivityResult代理
 * @Description:
 *
 * @author: xiao
 * @date:  2019/5/16 18:08
 * @version v1.0
 */
public class RxOnResult {
    private static final String TAG = "AvoidOnResult";
    private RxResultFragment rxResultFragment;

    public RxOnResult(Activity activity) {
        rxResultFragment = getResultFragment(activity);
    }

    public RxOnResult(Fragment fragment){
        this(fragment.getActivity());
    }

    private RxResultFragment getResultFragment(Activity activity) {
        RxResultFragment rxOnResultfragment = findAvoidOnResultFragment(activity);
        if (rxOnResultfragment == null) {
            rxOnResultfragment = new RxResultFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(rxOnResultfragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return rxOnResultfragment;
    }

    private RxResultFragment findAvoidOnResultFragment(Activity activity) {
        return (RxResultFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }


    public void startForResult(Intent intent, Callback callback) {
        rxResultFragment.startForResult(intent, callback);
    }

    public void startForResult(Class<?> clazz, Callback callback) {
        Intent intent = new Intent(rxResultFragment.getActivity(), clazz);
        startForResult(intent, callback);
    }

    public interface Callback {
        void onActivityResult(int resultCode, Intent data);
    }
}

