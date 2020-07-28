# photo 
     Android 拍照/相册，调用系统拍照，图库功能，仿微信实现多图片选择功能,应用于修改图像，拍几张图片上传，发布商品介绍等需要精选拍照或从相册选则图片的应用场景
     通过模块化封装，使得可用直接在其它项目中能够快速集成调用。
## model 1: multi-picture
    实现仿微信多图片选择效果

###截图

![Example1](img/photo_test.jpg) ![Select1](img/mulit_picture.jpg) ![Select2](picture_camera.jpg) ![Select3](picture_crop.jpg)
## mode 2: picture
	Android调用系统拍照、图库功能
   * 解决了Android 7.0适配问题：FileProvider的问题
   
将libary发布到jitpack方便大家使用

### 1. 项目根目录build.gradle文件添加

```

	allprojects {
		repositories {
			jcenter()
			maven {
				url = "https://dl.bintray.com/itxiaox/android"
			}
		}
	}
	compile 'com.itxiaox:cameraview:1.0.0'
	
```
### 2. 在要使用的moudle中添加

```
//选择多张图片发圈
 implementation "com.github.itxiaox.photo:multi-picture:0.0.5"
 //调用系统的相机或相册的方法，兼容性封装
 implementation "com.github.itxiaox.photo:picture:0.0.5"

```
### 3. 代码使用

- 多图片选择：
```
//注意需要，拍照权限，读写存储卡权限，动态权限
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
                        Toast.makeText(MainActivity.this, "失败："+error, Toast.LENGTH_SHORT).show();
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

```

- 调用系统拍照或选择照片，一般选择单张图片的情况，如修改人物图像

-- 调用系统相机拍照
```
//注意需要，拍照权限，读写存储卡权限，动态权限
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

```

-- 从相册中选择照片
```
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
```


