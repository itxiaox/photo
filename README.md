# XPhoto 
     Android 拍照/相册，调用系统拍照，图库功能，仿微信实现多图片选择功能
## model 1: multi-image-selector
    实现仿微信多图片选择效果
[参考文档](README_zh.md)

[参考来源](https://github.com/lovetuzitong/MultiImageSelector)

###截图

![Example1](art/example_1.png) ![Select1](art/select_1.png) ![Select2](art/select_2.png) ![Select3](art/select_3.png)
## mode 2: sys_camera
    

	
	Android调用系统拍照、图库功能
   * 解决了Android 7.0适配问题：FileProvider的问题
   
## [CameraView](https://github.com/itxiaox/cameraview)


博客 [Google封装类CameraView的使用](https://www.jianshu.com/p/eb095f925ef4)

增加将libary上传到bintray仓库，方便使用：

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


## [CameraX](https://developer.android.com/training/camerax?hl=zh-CN)
