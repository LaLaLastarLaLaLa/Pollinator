This is an Android app for recording wild plants.
The main functions are:
1. Display the plants you have recorded in the form of entries on the main interface;
2. There are three buttons below the main interface:
   1) Map, mark the plants you have recorded on the map, click the mark button to mark, and click the clear button to clear all marks;
   2) Camera, open the camera, and the button below is used to take photo or turn on the torch.
   After taking photo is completed, you will be asked to enter family, genus, and species information.
   If left blank, it will be automatically recorded as Unknown.
   Please turn on GPS when taking photo to record the geographical information of the photos;
   3) Album, a quiz on plant taxonomy.

Since this application uses Google Map, please register the Google service before compiling this project in Android Studio. You will then get a key for the Google service.
After you get the key, add a line at the end of the local.properties file: MAPS_API_KEY= Fill in your key here.
I did not upload my local.properties file to this project.

这是一个用于记录野生植物的安卓应用。
主要功能有：
1. 在主界面以条目的方式显示您已记录的植物；
2. 主界面下方有三个按钮：
   1) Map，在地图中标记您已记录过的植物，点击mark按钮标记，点击clear按钮清除所有标记；
   2) Camera，打开相机，下方按钮用于拍摄或者打开闪光灯。拍摄完成后会要求您输入科、属、种信息。若留白，则会自动记录为Unknown。在拍摄时请打开GPS，用于记录照片的地理信息；
   3) Album，植物分类学的小测验。

由于本应用使用了Google Map，所以您在Android Studio中编译本项目前，请先注册Google服务。之后您会获得一个谷歌服务的密钥。
在您获得密钥后，请在local.properties文件的末尾添加一行：MAPS_API_KEY= 此处填写您的密钥。
我并未上传我的local.properties文件到此项目中。
