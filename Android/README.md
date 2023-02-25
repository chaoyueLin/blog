# Android
[Android系统启动](./Android系统启动/Android系统启动.md)
## Framework

### 四大组件

	* Activity
	* Service
	* Broadcast
	* ContentProvider


### Handler机制
### [Binder机制](./Binder/Binder.md)
### 渲染机制,View,Touch事件分发

[UI优化](https://github.com/chaoyueLin/uiDemo),[View的绘制--Project Butter](./View的绘制/View的绘制.md),[View绘制的顺序缓存](./View绘制的顺序缓存/View绘制的顺序缓存.md),[Android屏幕刷新](./Android屏幕刷新/Android屏幕刷新.md)

### Sharepreference
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
    找出 AndroidStudio 构建出来的 apk 和已经安装到手机设备 apk 的差异。找出差异后，然后将差异发送到手机上执行差异合并。
    Apply Changes 的限制某些代码和资源更改必须在重启应用之后才能应用，其中包括以下更改
        添加或删除方法或字段
        更改方法签名
        更改方法或类的修饰符
        更改类继承行为
        更改枚举中的值
        添加或移除资源
        更改应用清单
        更改原生库（SO 文件）



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

## 卡顿
[什么是卡顿](https://github.com/Tencent/matrix/wiki/Matrix-Android-TraceCanary),[Matrix-TraceCanary解析](https://blog.yorek.xyz/android/3rd-library/matrix-trace/)

[什么是ANR](https://mp.weixin.qq.com/s?__biz=MzI1MzYzMjE0MQ==&mid=2247488116&idx=1&sn=fdf80fa52c57a3360ad1999da2a9656b&chksm=e9d0d996dea750807aadc62d7ed442948ad197607afb9409dd5a296b16fb3d5243f9224b5763&scene=178&cur_album_id=1780091311874686979#rd),[ANR dump](https://blog.csdn.net/stone_cold_cool/article/details/119464855),[高版本ANR日志获取](https://github.com/chaoyueLin/AnrTracerDemo),[BlockCanary 源码分析](https://blog.csdn.net/Love667767/article/details/106302877),[WatchDog原理](https://juejin.cn/post/6844904015524954126),[获取线程堆栈，cpu使用日志](https://github.com/chaoyueLin/threadDemo)

[获取线程锁](https://github.com/chaoyueLin/MonitorDemo),[卡顿、ANR、死锁，线上如何监控？](https://juejin.cn/post/6973564044351373326),[手Q Android线程死锁监控与自动化分析实践](https://cloud.tencent.com/developer/article/1064396)


## NDK
[NDK开发](https://github.com/chaoyueLin/ndkDemo),[cmake](https://github.com/chaoyueLin/cmakeDemo)

## [Flutter](https://github.com/chaoyueLin/flutterDemo)

## [组件化](https://github.com/chaoyueLin/componentDemo)
[ceventbus](https://github.com/chaoyueLin/cevnetbus)
[架构演进](./架构演进.md)

## [插件化](https://github.com/chaoyueLin/pluginDemo)
## [平台化](./平台化.md)
## [动态化](./动态化/动态化.md)