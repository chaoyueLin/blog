# Android
[Android系统启动](./Android系统启动/Android系统启动.md)
## Framework

### [Context](./Context/Context.md)
### [Handler](./Handler.md)
### [Binder机制](./Binder/Binder.md),[IPC](./IPC.md)
### Sharepreference
### Window
* Window类的唯一实现是PhoneWindow
* Window三种类型都存在的情况下，显示层级用Type表示Window的类型，一共三种：
    * 应用Window。对应着一个Activity，Window层级为1~99，在视图最下层。
    * 子Window。不能单独存在，需要附属在特定的父Window之中(如Dialog就是子Window)，Window层级为1000~1999。
    * 系统Window。需要声明权限才能创建的Window，比如Toast和系统状态栏，Window层级为2000-2999，处在视图最上层。

* flags属性表示Window的属性，它有很多选项，我们这里只说三种，剩下的可以查看官方文档
    * FLAG_NOT_FOCUSABLEWindow不需要获得焦点，因此也不会接收各种输入事件，此标记会同时启用* FLAG_NOT_TOUCH_MODAL标记位，无论代码中有没有明确设置这个标记位。设置了此状态意味着不需要和软键盘进行交互，因为它是Z-ordered的，独立于任何激活状态的软键盘。因此，它可以处于激活状态软键盘的上面，如果必要，可以覆盖软键盘。我们可以使用FLAG_ALT_FOCUSABLE_IM修改这个行为。
    * FLAG_NOT_TOUCH_MODAL，Window是否是modal状态。即使Window是可以获得焦点的（FLAG_NOT_FOCUSABLE没有设置），在Window外面的点击事件都会传递给后面的Window。否则，Window将会处理所有的点击事件，无论是否在它的范围内。
    * FLAG_SHOW_WHEN_LOCKED，Window可以显示在KeyGuard或者其他锁屏界面上。和FLAG_KEEP_SCREEN_ON一起使用可以在屏幕打开后直接显示Window，而不用经历KeyGuard。与FLAG_DISMISS_KEYGUARD一起使用可以自动跳过non-secure KeyGuard。此Flag只能用于最顶层全屏Window上。

* 创建时机在ActivityThread的performLaunchActivity中创建Activity，并调用attach方法进行一些数据的初始化

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


## 效率

* implementation，特点是 将该依赖隐藏在内部，而不对外部公开。比如在组件化项目中，有一个 app module 和一个 base module，app moudle 引入了 base module。其中 base module 使用 implementation 依赖了 Glide 库，因为 implementation 是内部依赖，所以是无法调用到 Glide 库的功能的。因此 implementation 可 以 对外隐藏不必要的接口，并且，使用它可以有效地 提高编译速度。比如，在组件化项目中一般含有多个 Moudle 模块，如 Module A => Module B => Moudle C, 比如 改动 Moudle C 接口的相关代码，如果使用的是 implementation，这时候编译只需要单独编译 Module B 模块就行，但是如果使用 api 或者旧版本的 compile，由 于Module A 也可以访问到 Moudle C，所以 Module A  部分也需要重新编译。所以，在使用无错的情况下，可以优先使用 implementation。

* InstantRun
    1、减少构建和部署 app 到手机的时间。
    2、热更新代码改动，无需重启 app 或者 activity。

* ApplyChanges
找出 AndroidStudio 构建出来的 apk 和已经安装到手机设备 apk 的差异。找出差异后，然后将差异发送到手机上执行差异合并。Apply Changes 的限制某些代码和资源更改必须在重启应用之后才能应用，其中包括以下更改
        
	* 添加或删除方法或字段
    * 更改方法签名
    * 更改方法或类的修饰符
    * 更改类继承行为
    * 更改枚举中的值
    * 添加或移除资源
    * 更改应用清单
    * 更改原生库（SO 文件）



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

## 虚拟机
[深入理解Android虚拟机ART](./深入理解Android虚拟机ART/README.md),
[Java 虚拟机和Dalvik区别](./Java虚拟机和Dalvik区别/Java虚拟机和Dalvik区别.md),
[Dalvik和ART和HotSpot](./Dalvik和ART和HotSpot/Dalvik和ART和HotSpot.md)


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