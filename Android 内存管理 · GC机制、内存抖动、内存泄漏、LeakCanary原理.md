# Android 内存管理 · GC机制、内存抖动、内存泄漏、LeakCanary原理（Markdown面试完整版）
## 一、Android 垃圾回收 GC 机制
### 1. 基础概念
GC（Garbage Collection）：虚拟机自动回收**无有效引用指向、不再使用的堆内存对象**，释放内存给系统复用；Android 使用 ART 虚拟机 GC，废弃了早期 Dalvik 标记清除。
内存划分：
- 栈内存：局部变量、方法调用帧，自动入栈出栈，无需GC
- 堆内存：所有new出来的对象、集合、Bitmap、图片资源，GC管理核心区域

### 2. 主流GC算法
#### （1）标记清除（Mark-Sweep）
1. 标记：遍历所有存活对象
2. 清除：回收未标记对象
缺点：产生大量**内存碎片**，大对象分配容易OOM

#### （2）标记整理（Mark-Compact）
标记存活对象后，把存活对象向内存一端平移整理，消除碎片
缺点：移动对象需要更新所有引用地址，开销大、耗时久

#### （3）复制算法（Copy）
把堆分为 From、To 两块同等空间，存活对象复制到To区，清空From；新生代大量短命对象使用
缺点：内存空间浪费一半

### 3. ART 分代GC（最核心考点）
把堆分为两大代，不同代使用不同GC算法，优化效率：
1. **新生代（Young Generation）**
   - 存放短期临时对象（循环临时对象、方法内临时实例）
   - 分为：Eden区、Survivor(S0、S1)
   - 采用**复制算法**，GC频率高、速度快，叫Minor GC
2. **老年代（Old Generation）**
   - 长期存活对象、大对象（Bitmap、大图、长生命周期单例）
   - 标记清除/标记整理，GC耗时更长，Major GC
3. 晋升规则：对象多次熬过新生代GC，自动晋升到老年代

### 4. GC 触发时机
1. 堆内存占用达到阈值，系统自动触发
2. 手动调用`System.gc()`：仅建议虚拟机GC，不保证立即执行
3. App进入后台、内存紧张，系统主动调度GC
4. 创建超大对象，剩余堆空间不足

### 5. GC 停顿（STW Stop-The-World）
GC标记阶段会暂停所有用户线程，卡顿、掉帧根源；
频繁GC、长时间STW → UI卡顿、ANR、内存抖动。

## 二、内存抖动（Memory Churn）
### 1. 本质
短时间内**频繁创建大量临时小对象**，快速分配、快速被GC回收，造成连续高频Minor GC，持续STW卡顿。
### 2. 常见场景
1. 循环、onDraw、onTouchEvent 频繁new对象（String、数组、Paint、Bitmap、自定义实体）
2. 动画、滑动列表中不断创建临时对象
3. 字符串频繁拼接`+`创建大量String临时对象
4. 集合在循环里反复创建、销毁

### 3. 危害
- 频繁GC产生持续STW，页面滑动卡顿、动画掉帧
- 加剧内存碎片，诱发OOM
- 耗电增加

### 4. 解决方案
1. 把循环内对象提升为成员变量，复用实例，避免重复new
2. 使用对象池（ObjectPool）复用对象，减少创建销毁
3. 字符串拼接改用`StringBuilder`
4. 自定义View的Paint、Rect、Matrix放到构造初始化，不在onDraw新建
5. 避免在MotionEvent.MOVE中创建对象

## 三、内存泄漏（Memory Leak）
### 1. 本质
**本应该被GC回收的对象，被一个更长生命周期的引用持有，导致无法被虚拟机回收，对象常驻堆内存**；
内存泄漏不断堆积 → 可用内存持续减少 → OOM崩溃。

生命周期大小关系：
`Application > Activity/Fragment > View > 临时方法对象`
短生命周期对象被长生命周期持有，最容易泄漏。

### 2. 高频内存泄漏场景&解决方案
#### 场景1：静态变量持有Activity/Fragment上下文
```java
// 错误：static 全局持有Activity，页面销毁无法回收
static Activity mActivity;
```
解决：使用`ApplicationContext`应用上下文；页面销毁手动置空静态引用。

#### 场景2：非静态内部类持有外部类隐式引用（Handler经典泄漏）
非静态内部类、匿名内部类默认持有外部Activity引用；Handler延迟消息排队，页面finish后消息未执行，Activity被持有泄漏。
解决：
1. Handler改为静态内部类
2. 使用WeakReference弱引用包裹Activity
3. Activity销毁`removeCallbacksAndMessages(null)`清空消息队列

#### 场景3：线程/协程持有页面引用，页面销毁未取消
子线程、ViewModelScope外的协程、RxJava异步任务持有View、Activity，页面关闭任务还在执行。
解决：
1. 使用`viewModelScope`、`lifecycleScope`绑定生命周期，页面销毁自动取消
2. Activity onDestroy手动终止线程、dispose Rx订阅

#### 场景4：集合静态常驻，不断添加对象不清理
全局List、Map持续add页面资源，不移除。
解决：页面销毁清空集合对应数据。

#### 场景5：资源未关闭释放
File流、Cursor、Bitmap、Camera、MediaPlayer忘记close/recycle。

#### 场景6：第三方监听、广播注册未反注册
注册广播、EventBus、第三方SDK监听，onDestroy没有unregister。

#### 场景7：Fragment静态复用、View引用未解绑
Fragment detached后View引用没清空；静态Fragment持有页面View。

### 3. 引用类型区分（辅助理解泄漏）
1. **强引用**：默认引用，只要存在，GC永远不回收（泄漏元凶）
2. **软引用 SoftReference**：内存不足时GC回收，适合缓存
3. **弱引用 WeakReference**：下次GC直接回收，Handler防泄漏常用
4. **虚引用 PhantomReference**：仅监控回收状态，几乎不用

## 四、LeakCanary 核心检测原理（面试高频）
### 1. 整体作用
自动检测Activity/Fragment/View等内存泄漏，抓取堆快照，分析引用链，展示泄漏路径。

### 2. 完整核心原理流程
#### 步骤1：监听目标对象生命周期
- 监听Activity：注册`Application.ActivityLifecycleCallbacks`，捕获`Activity.onDestroy()`
- 监听Fragment：监听FragmentManager生命周期回调，捕获销毁事件

#### 步骤2：延迟主动触发GC，判断是否回收成功
Activity销毁后，**延迟一段时间**（主线程空闲、几秒延时），手动触发GC；
目的：区分「临时内存占用」和「真正泄漏」，避免误判。

#### 步骤3：判断对象是否存活
1. 使用`WeakReference`弱引用封装销毁的Activity，搭配ReferenceQueue引用队列
2. 正常无泄漏：GC后弱引用入队，对象被回收
3. 发生泄漏：强引用还持有对象，弱引用不会入队，判定泄漏

#### 步骤4：生成Heap Dump堆快照（hprof文件）
确认泄漏后，抓取当前APP堆内存快照，保存所有对象、引用关系。

#### 步骤5：分析hprof，解析最短泄漏引用链
内置HAHA库解析快照，查找泄漏对象到GC Roots的强引用链路，找到泄漏源头（如静态变量、Handler、线程）。

#### 步骤6：弹窗展示泄漏详情、引用链，方便修复

### 3. GC Roots 概念（判断存活的基准）
GC Roots 是虚拟机认定的存活起点，被GC Roots直达强引用的对象全部存活，不会回收；
常见GC Roots：
1. Application静态变量
2. 主线程运行栈局部变量
3. 系统Native层引用
4. 活跃线程、虚拟机全局引用

### 4. LeakCanary 优化点
不会频繁dump hprof（dump卡顿极大），只确认泄漏后抓取；低版本ART兼容；自动过滤系统非业务泄漏。

## 五、面试高频汇总问答
1. 内存泄漏和内存抖动区别？
内存泄漏：对象永久无法回收，内存持续上涨；内存抖动：频繁创建销毁小对象，内存锯齿状起伏，高频GC卡顿。

2. 为什么不能随便用Activity上下文给静态组件？
Activity持有窗口、View资源，生命周期短；静态变量生命周期等于App，强持有后Activity销毁无法GC，造成泄漏。

3. 弱引用为什么不会内存泄漏？
弱引用不阻止GC，只要没有强引用，GC到来直接回收对象。

4. OOM常见原因？
大量内存泄漏堆积、超大Bitmap未压缩加载、一次性创建超多对象、内存碎片分配失败。

5. 怎么手动排查内存问题？
Android Studio Profiler（内存面板）、MAT分析hprof、LeakCanary自动化检测。

## 六、优化总结
1. 规避内存泄漏：及时取消异步、反注册监听、静态慎用页面引用、销毁置空资源
2. 解决内存抖动：复用对象、减少循环内临时创建、优化onDraw逻辑
3. GC优化：减少短命大对象，图片压缩采样加载，使用内存缓存
4. 检测工具：LeakCanary、Profiler、MAT

内容由 AI 生成