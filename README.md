# DEBUG_UDP_CAT

## 摘要
这是一个udp工具，可以在无法连接adb的时候利用此工具进行实时查看log  
有时候某些设备无法进行WIFI ADB，而刚好要调试的内容又是外接摄像头这类需要用到数据口的玩意，那么就没办法进行实时log查看，一般只能选择将log保存到本地，完成调试操作后进行本地查看，很不方便，所以就诞生了 UDP调试猫，利用udp可以很轻松的查看调试设备的log

## 使用:  
  1、 调试设备A， 辅助设备B， 连到同一个WIFI  
  2、 A的代码中，将logcat数据以广播形式发送出去  
  3、 A安装调试猫，设置发送IP和发送端口（ip即B设备的ip，端口是B设备的接收端口）  
  4、 A设备进入调试猫的设置中，添加刚刚在代码中设置的广播Action和filter（filter就是广播Intent的putExtra的关键字<key>）  
  5、 B安装调试猫，设置接收端口，开始接收  
  *注：B设备可以是PC，可以不安装调试猫，使用任意UDP工具都是可以的*
