# Android

## Framework

## 虚拟机
[深入理解Android虚拟机ART](./深入理解Android虚拟机ART/README.md),
[Java 虚拟机和Dalvik区别](./Java虚拟机和Dalvik区别/Java虚拟机和Dalvik区别.md),
[Dalvik和ART和HotSpot](./Dalvik和ART和HotSpot/Dalvik和ART和HotSpot.md),
[ClassLoader双亲委派机制](./ClassLoader双亲委派机制/双亲委派机制.md)
[Android系统启动](./Android系统启动/Android系统启动.md)

### [Context](./Context/Context.md)
### [Handler](./Handler.md)
### [Binder机制](./Binder/Binder.md),[IPC](./IPC.md)
### Sharepreference
### [Window](./Window/README.md)


### 四大组件

* [Activity](./Activity/Activity.md)
* [Service](./Service/Service.md)
* Broadcast
	* 注册类型
	* 发送类型
	* 带权限
	* LocalBroadcast
* ContentProvider,
	* ContentProvider的底层是Binder，除了onCreate由系统回调运行在主线程中，其他五个方法运行在Binder线程池中，不是线程安全的。而如果在同一个进程访问ContentProvider，根据Binder的原理，同进程的Binder调用就是直接的对象调用，这个时候CRUD运行在调用者的线程中。
	* 另外，ContentProvider的内部存储不一定是sqlite，它可以是任意数据。


### [View](./View/View.md)

* [UI优化](https://github.com/chaoyueLin/uiDemo),
* [View的绘制](./View的绘制/View的绘制.md),[View绘制的顺序缓存](./View绘制的顺序缓存/View绘制的顺序缓存.md),[Android屏幕刷新](./Android屏幕刷新/Android屏幕刷新.md)
* [RecycleView](./RecycleView/RecycleView.md),[ViewStub](./ViewStub/ViewStub.md)

### Fragment
* 生命周期
* FragmentTransaction
* 回退栈
* Activity通信

### [webview](https://github.com/chaoyueLin/webviewDemo)
### 重要的Service
* AMS
* PMS
* WMS

[ASM](https://github.com/chaoyueLin/asmDemo)

[Gradle](https://github.com/chaoyueLin/GradleDemo)

[代码混淆](./代码混淆/代码混淆.md),[ProGuard使用](./代码混淆/ProGuard使用.md)

[infer代码检查](https://github.com/chaoyueLin/infer_code_check),[空指针异常](https://github.com/chaoyueLin/NPE_avoid),
[Android兼容Java8语法特性的原理分析](./Android兼容Java8语法特性的原理分析/Android兼容Java8语法特性的原理分析.md)

## 安全
[apktool](https://github.com/chaoyueLin/apktoolDemo),[Android签名校验流程](./Android签名校验流程/Android签名校验流程.md)

## [单元测试在Android](https://github.com/chaoyueLin/UnitTestInAndroid)

## 三方
[Jetpack使用](https://github.com/chaoyueLin/jetpackDemo),
[OKHttp与拦截链模式](https://github.com/chaoyueLin/okhttpDemo),[RxJava](https://github.com/chaoyueLin/reactive),[Retrofit](https://github.com/chaoyueLin/retrofitDemo),[Gson](./Gson/Gson.md),
[mmkv源码](https://github.com/chaoyueLin/mmkvDemo),[MMAP在Android中使用](./MMAP在Android中使用/MMAP在Android中使用.md)
[Glide](https://github.com/chaoyueLin/glideDemo)


## SDK
[SDK开发指南](./SDK开发.md)

## 设计模式
[MVVM](https://github.com/chaoyueLin/mvvmDemo),[MVI](https://github.com/chaoyueLin/mviDemo)

## 性能优化
### 卡顿
[什么是卡顿](https://github.com/Tencent/matrix/wiki/Matrix-Android-TraceCanary),[Matrix-TraceCanary解析](https://blog.yorek.xyz/android/3rd-library/matrix-trace/)

[什么是ANR](https://mp.weixin.qq.com/s?__biz=MzI1MzYzMjE0MQ==&mid=2247488116&idx=1&sn=fdf80fa52c57a3360ad1999da2a9656b&chksm=e9d0d996dea750807aadc62d7ed442948ad197607afb9409dd5a296b16fb3d5243f9224b5763&scene=178&cur_album_id=1780091311874686979#rd),[ANR dump](https://blog.csdn.net/stone_cold_cool/article/details/119464855),[高版本ANR日志获取](https://github.com/chaoyueLin/AnrTracerDemo),[BlockCanary 源码分析](https://blog.csdn.net/Love667767/article/details/106302877),[WatchDog原理](https://juejin.cn/post/6844904015524954126),[获取线程堆栈，cpu使用日志](https://github.com/chaoyueLin/threadDemo)

[获取线程锁](https://github.com/chaoyueLin/MonitorDemo),[卡顿、ANR、死锁，线上如何监控？](https://juejin.cn/post/6973564044351373326),[手Q Android线程死锁监控与自动化分析实践](https://cloud.tencent.com/developer/article/1064396)

### [内存优化](./内存优化/内存优化.md)

### [启动优化](./启动优化.md)

### [IO优化](./IO优化/IO优化.md)

### [Crash治理](./Crash治理/Crash治理.md)

### [自定义Lint](自定义Lint.md),[Profiler](./Profiler/Profiler.md)

### [网络优化](./网络优化/网络优化.md)

## NDK
[NDK开发](https://github.com/chaoyueLin/ndkDemo),[cmake](https://github.com/chaoyueLin/cmakeDemo)

## [Flutter](https://github.com/chaoyueLin/flutterDemo)

## [组件化](https://github.com/chaoyueLin/componentDemo)
[ceventbus](https://github.com/chaoyueLin/cevnetbus)
[架构演进](./架构演进.md)

## [插件化](https://github.com/chaoyueLin/pluginDemo)
## [平台化](./平台化.md)
## [动态化](./动态化/动态化.md)