# Kotlin Flow基础知识点与高频面试题
## 一、Flow基础概念
Flow是基于协程实现的异步数据流框架，可依次发射多个值，具备冷流特性：若无收集者，上游代码不会执行；每次触发收集都会完整执行一次生产者逻辑。
1. 分类
    - 普通Flow：无状态，每次collect都会重执行生产逻辑，仅主动发送数据。
    - StateFlow：热流，始终保存最新值，新订阅者可立刻获取缓存值，必须设置初始值，仅有一个订阅者时无需配置额外参数，多用于UI状态托管。
    - SharedFlow：热流，支持多订阅，可自定义重放数值、缓冲区策略，无强制初始值，适配一次性事件。
2. 核心构成
    生产者：flow{}、MutableStateFlow、MutableSharedFlow等构建数据流；
    中间运算符：转换、过滤数据流，属于惰性执行，如map、filter、flatMap系列；
    末端运算符：触发数据流执行，如collect、collectLatest、first等。
3. 冷流与热流区分
    冷流：flow构建的常规Flow，订阅时才运行，多订阅会多次执行上游逻辑，无内存常驻数据；
    热流：StateFlow、SharedFlow，创建后常驻内存发射数据，订阅与否都会运行，多订阅共享同一份数据源。

## 二、常用运算符
1. 转换类
map：逐个转换发射的数据；
transform：灵活发送多个值，自由度高于map；
flatMapConcat：串行处理上游数据，等上一条处理完毕再执行下一条；
flatMapMerge：并发处理多条上游数据；
flatMapLatest：新数据抵达时取消上一条正在执行的任务，搜索场景核心算子。
2. 过滤类
filter：筛选满足条件的数据；
distinctUntilChanged：过滤连续重复的值，StateFlow自带该特性；
take(n)：仅获取前n条数据。
3. 生命周期管控
repeatOnLifecycle：绑定页面生命周期，生命周期不达标时直接取消收集释放资源；
launchWhenStarted：仅暂停协程，上游持续运行，存在资源损耗。
4. 收集算子
collect：完整接收每一条数据；
collectLatest：丢弃未执行完的旧任务，只执行最新数据逻辑。

## 三、StateFlow与SharedFlow详细对比
1. StateFlow
    - 强制初始值，内部永久缓存最新值；
    - 等效自带distinctUntilChanged，相同值不会重复下发；
    - 默认重放数量为1，新订阅者直接拿到缓存状态；
    - 适合界面状态：加载中、成功、失败、列表数据等，屏幕旋转可恢复状态。
2. SharedFlow
    - 无需初始值，默认无缓存；
    - 可自定义replay重放条数、extraBufferCapacity缓冲区；
    - 相同值允许重复推送，不会自动去重；
    - 适配一次性事件：弹窗、页面路由、吐司，规避重建重复消费问题。
3. 一次性事件解决方案
若强行用StateFlow传递事件，配置包装类Event<T>，通过isConsumed标记事件是否已消费；更简洁方案是使用SharedFlow，replay设为0避免重建重放。

## 四、高频面试问题及解答
1. Flow冷流特性是什么？带来什么优缺点？
Flow属于冷流，只有调用末端收集算子时，上游生产者代码才会执行，每一次collect都会从头执行生产逻辑。优点是节约资源，无订阅时不会发起网络、数据库请求；缺点是多次collect会重复执行耗时操作，多订阅场景推荐改用SharedFlow。

2. Flow和LiveData区别？
LiveData是生命周期感知的热数据流，依附Android框架，仅支持单个值存储，只能在主线程更新；Flow基于协程，跨平台可用，支持多值连续发射，具备丰富运算符，可灵活调度线程。StateFlow可替代LiveData，兼具生命周期感知与协程优势，且LiveData存在转换繁琐、异步能力薄弱的短板。

3. StateFlow为什么必须提供初始值？
StateFlow的设计初衷是承载界面当前状态，页面初始化时界面必须拿到一个有效状态，因此强制初始化。而SharedFlow面向瞬时事件，不存在初始状态的需求，无该约束。

4. 使用SharedFlow传递事件时replay、buffer、onBufferOverflow参数如何配置？
replay代表向新订阅者重放历史数据条数，事件场景设0，防止页面重建重复推送弹窗；extraBufferCapacity设置缓冲区大小，应对短时间高频发送；onBufferOverflow配置缓冲区溢出策略，可选丢弃最新、丢弃最旧、挂起阻塞。常规事件场景配置：MutableSharedFlow(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)。

5. repeatOnLifecycle优于launchWhenStarted的原因？
launchWhenStarted仅暂停协程，上游数据流持续运行，网络请求、数据库查询不会中断，持续消耗流量与内存；repeatOnLifecycle在生命周期低于指定状态时直接取消协程，上游同步停止执行，彻底释放资源，杜绝无效请求，避免内存泄漏。

6. collectLatest工作原理，适用场景？
每次上游发射新数据，collectLatest会取消上一轮未完成的收集逻辑，只运行最新数据对应的代码。适用于输入实时校验、列表下拉刷新、接口防抖，防止旧请求延迟返回覆盖最新结果。

7. flatMapLatest实现搜索防抖的原理？
上游每收到新的搜索关键词，flatMapLatest会取消上一个关键词发起的网络请求，立刻发起新检索，最终仅有最新关键词的请求能够返回并渲染页面，省去手动防抖延时逻辑。

8. Flow切换线程的两种方式flowOn与dispatchIn/collect的区别？
flowOn作用于上游整个数据流，修改生产者执行的调度器，多次调用flowOn分段切换线程；collect、launch中指定的调度器仅作用于下游收集逻辑，不会影响上游生产代码。

9. StateFlow没有生命周期感知，如何安全在UI层收集？
借助viewModelScope.launch搭配repeatOnLifecycle绑定Fragment/Activity生命周期，页面进入STOPED状态自动取消收集；若直接collect，页面销毁后数据流持续持有页面引用，极易引发内存泄漏。

10. Flow如何处理异常？try-catch、catch算子区别？
在flow{}内部使用try-catch可捕获单段逻辑异常；catch中间运算符捕获上游抛出的异常，同时可发送备用值，不会中断整个数据流。末端collect中捕获异常仅能捕获下游收集阶段错误，无法拦截上游生产异常。

11. MutableStateFlow赋值用value和emit的差异？
value是同步赋值，主线程子线程均可调用；emit是挂起函数，只能在协程内调用。二者都会更新缓存状态并分发数据，无本质效果区别，仅调用环境不同。

12. 多订阅普通Flow会出现什么问题？怎么解决？
普通冷Flow每次订阅都会重新执行上游逻辑，多订阅会重复发起多次请求。解决方案：使用shareIn转换为SharedFlow或者stateIn转为StateFlow，转化为热流实现多订阅共享数据源。

13. shareIn、stateIn三个参数（scope、started、initialValue/replay）如何选择？
started参数三种枚举：
- SharingStarted.Eagerly：作用域创建后立刻启动数据流，常驻消耗资源；
- SharingStarted.Lazily：首个订阅者出现时启动，最后一个订阅者取消后持续持有；
- WhileSubscribed：无订阅者时停止上游，释放资源，UI场景首选。

14. distinctUntilChanged的作用，StateFlow是否自带？
该算子过滤连续重复的值，避免重复刷新UI。StateFlow分发数据时内部自动执行distinctUntilChanged，值无变更不会通知订阅者；SharedFlow默认没有，需要手动添加。

15. Flow会不会造成内存泄漏？常见泄漏场景？
会。常见场景：未绑定生命周期直接collect、作用域提前销毁但数据流未取消、全局SharedFlow持有页面实例。规避方案：repeatOnLifecycle绑定生命周期、使用viewModelScope管控数据流、页面销毁主动关闭订阅。

内容由 AI 生成