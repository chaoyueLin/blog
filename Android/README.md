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
[OKHttp与拦截链模式](https://github.com/chaoyueLin/okhttpDemo),[RxJava](https://github.com/chaoyueLin/reactive),[Retrofit](https://github.com/chaoyueLin/retrofitDemo),[Gson](./Gson/Gson.md),
[mmkv源码](https://github.com/chaoyueLin/mmkvDemo),[MMAP在Android中使用](./MMAP在Android中使用/MMAP在Android中使用.md)
[Glide](https://github.com/chaoyueLin/glideDemo)


## Jetpack
[Jetpack使用](https://github.com/chaoyueLin/jetpackDemo),
[LiveData与Lifecycle剖析](./Jetpack/LiveData%20与%20Lifecycle%20剖析.md),[ViewModel屏幕旋转不重建 + onCleared调用时机](./Jetpack/ViewModel%20屏幕旋转不重建%20+%20onCleared%20调用时机.md)


## SDK
[SDK开发指南](./SDK开发.md)

## 设计模式
[MVVM](https://github.com/chaoyueLin/mvvmDemo),[MVI](https://github.com/chaoyueLin/mviDemo)

## 性能优化

### [卡顿优化](./卡顿优化.md)

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