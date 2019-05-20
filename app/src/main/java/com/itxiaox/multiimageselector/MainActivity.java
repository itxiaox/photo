package com.itxiaox.multiimageselector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itxiaox.sys_camera.PhotoHelper;
import com.itxiaox.sys_camera.ResultListener;

import java.io.File;
import java.util.ArrayList;

import com.itxiaox.multi_image_selector.MultiImageSelectorActivity;
import com.itxiaox.sys_camera.utils.ConvertUtils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE = 2;

    private TextView mResultText;
    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;

    private ImageView iv_show;
    private ArrayList<String> mSelectPath;

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
                if(checkedId == R.id.multi){
                    mRequestNum.setEnabled(true);
                }else{
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedMode = MultiImageSelectorActivity.MODE_MULTI;

                if(mChoiceMode.getCheckedRadioButtonId() == R.id.single){
                    selectedMode = MultiImageSelectorActivity.MODE_SINGLE;
                }else{
                    selectedMode = MultiImageSelectorActivity.MODE_MULTI;
                }

                boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;

                int maxNum = 9;
                if(!TextUtils.isEmpty(mRequestNum.getText())){
                    maxNum = Integer.valueOf(mRequestNum.getText().toString());
                }

                Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
                // 默认选择
                if(mSelectPath != null && mSelectPath.size()>0){
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                }
                startActivityForResult(intent, REQUEST_IMAGE);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for(String p: mSelectPath){
                    sb.append(p);
                    sb.append("\n");
                }
                mResultText.setText(sb.toString());
            }
        }else if(requestCode == 1){
            if("camera".equals(type)){
                sysCamera();
            }else {
                sysAlbum();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String type = "camera";
    public void sysCamera(View view) {
        type = "camera";
        startActivityForResult(new Intent(this, PermissionActivity.class),1);

    }

    public void sysAlbum(View view) {
        type = "album";
        startActivityForResult(new Intent(this, PermissionActivity.class),1);
    }


    public void sysCamera() {

        PhotoHelper.build()
                .enableCrop(true)
                .openSysCamera(this,new ResultListener<File>() {
            @Override
            public void onSuccess(File filePath) {
                Log.i(TAG, "sysCamera-onSuccess: filePath="+filePath);
                Bitmap bitmap = getSmallBitmap(filePath.getAbsolutePath(),200,300);

                //解决拍照旋转问题
                int degree = ConvertUtils.getPictureDegree(filePath.getAbsolutePath());
                Log.i(TAG, "onSuccess: degree="+degree);
                iv_show.setImageBitmap(ConvertUtils.rotateBitmap(bitmap,degree));
            }

            @Override
            public void onFail(String error) {
                Log.i(TAG, "sysCamera-onFail: error="+error);
            }
        });
    }

    public void sysAlbum() {
        type = "album";
        PhotoHelper.build().openSysAlbum(this,new ResultListener<File>() {
            @Override
            public void onSuccess(File filePath) {
                Log.i(TAG, "sysAlbum-onSuccess: filePath="+filePath);

                Bitmap bitmap = getSmallBitmap(filePath.getAbsolutePath(),200,300);
                int degree = ConvertUtils.getPictureDegree(filePath.getAbsolutePath());
                Log.i(TAG, "onSuccess: degree="+degree);
                iv_show.setImageBitmap(ConvertUtils.rotateBitmap(bitmap,degree));
            }

            @Override
            public void onFail(String error) {
                Log.i(TAG,
                        "sysAlbum-onFail: filePath="+error);
            }
        });
    }

    public static Bitmap getSmallBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // 计算 缩略图大小为原始图片大小的几分之一 inSampleSize:缩略图大小为原始图片大小的几分之一
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {


        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }
        return inSampleSize;

    }
}
