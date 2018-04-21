# Camera
### 直接调用系统的相机
### 自定义相机
1. 相机参数的设置

```
Camera camera = Camera.open();   
Camera.Parameters parameters = camera.getParameters();
```

获取预览的各种分辨率

```
List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes(); 
```
获取摄像头支持的各种分辨率

```
List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes(); 
```
设置照片格式 

```
parameters.setPictureFormat(PixelFormat.JPEG); 
```
设置预浏尺寸，注意要在摄像头支持的范围内选择

```
parameters.setPreviewSize(WIDTH, HEIGHT); 
```
设置照片分辨率，注意要在摄像头支持的范围内选择

```
parameters.setPictureSize(WIDTH, HEIGHT); 
```
设置照相机参数

```
camera.setParameters(parameters); 
```
开始拍照 

```
camera.startPreview();
```





