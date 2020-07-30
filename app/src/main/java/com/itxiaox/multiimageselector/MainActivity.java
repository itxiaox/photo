package com.itxiaox.multiimageselector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itxiaox.multi_image_selector.MultiPhotoUtils;
import com.itxiaox.multi_image_selector.MultiResultListener;
import com.itxiaox.permission.annotation.NeedsPermission;
import com.itxiaox.permission.annotation.OnNeverAskAgain;
import com.itxiaox.permission.annotation.OnPermissionDenied;
import com.itxiaox.permission.annotation.OnShowRationale;
import com.itxiaox.permission.library.PermissionDialog;
import com.itxiaox.permission.library.PermissionManager;
import com.itxiaox.permission.library.listener.PermissionRequest;
import com.itxiaox.photo.PhotoHelper;
import com.itxiaox.photo.ResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.itxiaox.multi_image_selector.MultiImageSelectorActivity;
import com.itxiaox.photo.utils.BitmapUtils;
import com.itxiaox.photo.utils.ConvertUtils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mResultText;
    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;
    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private ImageView iv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_show = findViewById(R.id.iv_show);
        mResultText = (TextView) findViewById(R.id.result);
        mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
        mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
        mRequestNum = (EditText) findViewById(R.id.request_num);

        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.multi) {
                    mRequestNum.setEnabled(true);
                } else {
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "selectPicture";
                PermissionManager.request(MainActivity.this, permissions);

            }
        });

/*        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GestureImageActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @NeedsPermission
    public void needPermission() {
        switch (type) {
            case "selectPicture":
                selectPicture();
                break;
            case "camera":
                sysCamera();
                break;
            case "album":
                sysAlbum();
                break;
        }
    }

    @OnPermissionDenied
    public void permissionDenied() {
        Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限说明的拒绝，一般这里可以采用一个对话框说明该权限的作用
     *
     * @param request
     */
    @OnShowRationale()
    void showRationaleForCamera(final PermissionRequest request) {
        PermissionDialog.showRationale(this,request,"权限说明","拍照需要此权限，否则不能进行拍照");
    }

    @OnNeverAskAgain()
    void onNeverAgain() {
        PermissionDialog.showNeverAgain(this,"权限已拒绝","您已经拒绝了此权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    private void selectPicture() {
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        if (mChoiceMode.getCheckedRadioButtonId() == R.id.single) {
            selectedMode = MultiImageSelectorActivity.MODE_SINGLE;
        } else {
            selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        }

        boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;

        int maxNum = 9;
        if (!TextUtils.isEmpty(mRequestNum.getText())) {
            maxNum = Integer.valueOf(mRequestNum.getText().toString());
        }

        MultiPhotoUtils
                .build()
                //选择的结果回调
                .callback(new MultiResultListener<String>() {
                    @Override
                    public void onSuccess(List<String> filePath) {
                        StringBuilder sb = new StringBuilder();
                        for (String p : filePath) {
                            sb.append(p);
                            sb.append("\n");
                        }
                        mResultText.setText(sb.toString());
                    }

                    @Override
                    public void onFail(String error) {
                        Toast.makeText(MainActivity.this, "失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                })
                //是否支持拍照，默认支持
                .enableCamera(showCamera)
                //最多图片数，默认为9张，九宫格
                .maxPictures(maxNum)
                //图片选择模式：0：单选；1：多选，默认多选
                .selectMode(selectedMode)
                //显示，必须调用
                .show(MainActivity.this);
    }



    private String type = "camera";

    public void sysCamera(View view) {
        type = "camera";
//        startActivityForResult(new Intent(this, PermissionActivity.class), 1);
        PermissionManager.request(this, permissions);
    }

    public void sysAlbum(View view) {
        type = "album";
        PermissionManager.request(this, permissions);
//        startActivityForResult(new Intent(this, PermissionActivity.class), 1);
    }


    public void sysCamera() {

        PhotoHelper.build()
                //是否剪裁
                .enableCrop(true)
                .openSysCamera(this, new ResultListener<File>() {
                    @Override
                    public void onSuccess(File filePath) {
                        Log.i(TAG, "sysCamera-onSuccess: filePath=" + filePath);
                        Bitmap bitmap = BitmapUtils.getSmallBitmap(filePath.getAbsolutePath(), 200, 300);

                        //todo 解决拍照旋转问题
                        int degree = ConvertUtils.getPictureDegree(filePath.getAbsolutePath());
                        Log.i(TAG, "onSuccess: degree=" + degree);
                        iv_show.setImageBitmap(ConvertUtils.rotateBitmap(bitmap, degree));
                    }

                    @Override
                    public void onFail(String error) {
                        Log.i(TAG, "sysCamera-onFail: error=" + error);
                    }
                });
    }

    public void sysAlbum() {
        type = "album";
        PhotoHelper.build()
                .openSysAlbum(this, new ResultListener<File>() {
                    @Override
                    public void onSuccess(File filePath) {//返回选中的图片路径

                        Bitmap bitmap = BitmapUtils.getSmallBitmap(filePath.getAbsolutePath(), 200, 300);
                        int degree = ConvertUtils.getPictureDegree(filePath.getAbsolutePath());
                        iv_show.setImageBitmap(ConvertUtils.rotateBitmap(bitmap, degree));
                    }

                    @Override
                    public void onFail(String error) {
                        Log.i(TAG, "sysAlbum-onFail: filePath=" + error);
                    }
                });
    }


    public void goCameraView(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }
}
