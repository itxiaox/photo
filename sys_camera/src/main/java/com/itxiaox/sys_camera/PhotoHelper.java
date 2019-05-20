package com.itxiaox.sys_camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.itxiaox.sys_camera.utils.ConvertUtils;
import com.itxiaox.sys_camera.utils.RxOnResult;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @Title: Android 拍照/相册 功能辅助类
 * @Description:  Android 调用系统的拍照/相册 辅助类，
 *
 * 注意要添加动态权限，解决Android 6.0以上的版本的适配问题
 *    <uses-feature android:name="android.hardware.camera" />
 *     <!--相机权限-->
 *     <uses-permission android:name="android.permission.CAMERA" />
 *     <!--写入SD卡的权限：如果你希望保存相机拍照后的照片-->
 *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *     <!--读取SD卡的权限:打开相册选取图片所必须的权限-->
 *     <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * @author: xiao
 * @date:  2019/5/16 18:09
 * @version v1.0
 */
public class PhotoHelper {

    private Activity mActivity;
    ResultListener<File> resultListener;
    File saveFile  = new File(Environment.getExternalStorageDirectory(), "test.png");

    private static WeakReference<Activity>  weakReference;
    private boolean enableCrop = false;//是否开启剪裁功能

    private PhotoHelper(){}

    public PhotoHelper enableCrop(boolean enable){
        enableCrop = enable;
        return this;
    }

    public PhotoHelper setSaveFile(File file){
        saveFile = file;
        return this;
    }

    public static PhotoHelper build() {
        return new PhotoHelper();
    }

    /**
     * 打开系统相机
     */
    public   void openSysCamera(Activity activity,final ResultListener<File> resultListener) {
        this.resultListener = resultListener;
        this.mActivity = activity;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(saveFile==null){
            resultListener.onFail("saveFile is null");
            return;
        }
        Uri imageUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){// android 7.0适配，Android 7.0 就是 File 路径的变更，需要使用 FileProvider 来做
            imageUri = FileProvider.getUriForFile(mActivity, "com.itxiaox.sys_camera.provider", saveFile);
        }else {
            imageUri = Uri.fromFile(saveFile);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        RxOnResult rxOnResult = new RxOnResult(activity);
        rxOnResult.startForResult(cameraIntent, new RxOnResult.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent intent) {
                Uri data;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    data = ConvertUtils.getImageContentUri(mActivity,saveFile);
                }else {
                    data = Uri.fromFile(saveFile);
                }
                if (enableCrop) {
                    cropPic(data, resultListener);
                } else {
                    handleResult(data);
                }
            }
        });
    }

    /**
     * 裁剪图片
     *
     * @param data
     */
    private  <T> void cropPic(Uri data,final ResultListener<File> resultListener) {
        if (data == null) {
            return;
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(data, "image/*");

        // 开启裁剪：打开的Intent所显示的View可裁剪
        cropIntent.putExtra("crop", "true");
        // 裁剪宽高比
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // 裁剪输出大小
        cropIntent.putExtra("outputX", 320);
        cropIntent.putExtra("outputY", 320);
        cropIntent.putExtra("scale", true);
        /**
         * return-data
         * 这个属性决定我们在 onActivityResult 中接收到的是什么数据，
         * 如果设置为true 那么data将会返回一个bitmap
         * 如果设置为false，则会将图片保存到本地并将对应的uri返回，当然这个uri得有我们自己设定。
         * 系统裁剪完成后将会将裁剪完成的图片保存在我们所这设定这个uri地址上。我们只需要在裁剪完成后直接调用该uri来设置图片，就可以了。
         */

        cropIntent.putExtra("return-data", false);
        // 当 return-data 为 false 的时候需要设置这句
//        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // 图片输出格式
//        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 头像识别 会启动系统的拍照时人脸识别
//        cropIntent.putExtra("noFaceDetection", true);
//        startActivityForResult(cropIntent, CROP_RESULT_CODE);
        RxOnResult rxOnResult = new RxOnResult(mActivity);
        rxOnResult.startForResult(cropIntent, new RxOnResult.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent intent) {
                //todo 剪裁完成
                if (intent == null) return;
                Uri data = intent.getData();
                handleResult(data);
            }
        });
    }
    /**
     * 打开系统相册
     */
    public void openSysAlbum(Activity activity, final ResultListener<File> resultListener) {
        this.resultListener = resultListener;
        this.mActivity = activity;
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        RxOnResult rxOnResult = new RxOnResult(mActivity);
        rxOnResult.startForResult(albumIntent, new RxOnResult.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                Uri uri = data.getData();
                if (enableCrop) {
                    cropPic(uri, resultListener);
                } else {
                    handleResult(uri);
                }
            }
        });
    }


    private void  handleResult(Uri data){
        if(data==null){
            if(resultListener!=null)
                resultListener.onFail("data is null");
            return;
        }
        String filePath = ConvertUtils.getPathFromUri(mActivity, data);
        saveFile = new File(filePath);
        if (resultListener != null)
            resultListener.onSuccess(saveFile);
    }



}
