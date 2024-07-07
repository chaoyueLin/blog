# Window
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

