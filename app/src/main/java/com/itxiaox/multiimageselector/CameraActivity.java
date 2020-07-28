package com.itxiaox.multiimageselector;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

//import com.google.android.cameraview.CameraView;


public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
//    CameraView cameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        cameraView = findViewById(R.id.camera);
//        cameraView.addCallback(new CameraView.Callback() {
//            @Override
//            public void onCameraOpened(CameraView cameraView) {
//                super.onCameraOpened(cameraView);
//                Log.i(TAG, "onCameraOpened: ");
//            }
//
//            @Override
//            public void onCameraClosed(CameraView cameraView) {
//                super.onCameraClosed(cameraView);
//                Log.i(TAG, "onCameraClosed: ");
//            }
//
//            @Override
//            public void onPictureTaken(CameraView cameraView, byte[] data) {
//                super.onPictureTaken(cameraView, data);
//                Log.i(TAG, "onPictureTaken: ");
//
//                File saveFile = new File(Environment.getExternalStorageDirectory(),"test.jpg");
//                if(!saveFile.exists()){
//                    try {
//                        saveFile.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i(TAG, "onPictureTaken: "+saveFile.getAbsolutePath());
//                FileOutputStream fo = null;
//                try {
//                    fo = new FileOutputStream(saveFile);
//                    fo.write(data);
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }


//    public void camera(View view) {
//        cameraView.takePicture();
//    }
//
//    public void start(View view) {
//        cameraView.start();
//    }
//
//    public void stop(View view) {
//        cameraView.stop();
//    }


}
