package com.itxiaox.multi_image_selector;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

/**
 * 仿微信拍照、选择图片功能
 *
 */
public class MultiPhotoUtils {
    private boolean showCamera = true;//是否显示拍摄图片，即是否显示拍照功能
    private int maxNum = 9;//最多可选择的图片
    private ArrayList<String> mSelectPath;
    private int selectedMode; //    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;
    /**
     * 是否开启拍照功能
     * @param enable 开启
     * @return
     */
    public MultiPhotoUtils enableCamera(boolean enable){
        this.showCamera = enable;
        return this;
    }

    /**
     * 最大可选择的照片书，默认是9个，九宫格
     * @param max
     * @return
     */
    public MultiPhotoUtils maxPictures(int max){
        this.maxNum = max;
        if (max==0){
            throw new IllegalArgumentException("max must be > 0");
        }
        return this;
    }

    public static MultiPhotoUtils build() {
        return new MultiPhotoUtils();
    }
    /**
     * 选择模式
     * 0: MODE_SINGLE 单选 </br>
     * 1: MODE_MULTI  多选 </br>
     * @param mode
     * @return
     */
    public MultiPhotoUtils selectMode(int mode){
        this.selectedMode = mode;
        return this;
    }
    MultiResultListener<String> multiResultListener;
    public MultiPhotoUtils callback(MultiResultListener<String> callback){
        this.multiResultListener = callback;
        return this;
    }
    private static final int REQUEST_IMAGE = 2;
    public void show(Activity activity){
        Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
        MultiImageSelectorActivity.multiResultListener = multiResultListener;
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }

        activity.startActivity(intent);
    }
}
