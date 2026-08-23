# Activity 启动流程「思维导图式突击版」

这是为你准备的 **Activity 启动流程「思维导图式突击版」**。我把复杂的底层流程拆解成**三大阶段、四个核心角色、一条主线**，面试前只要过一遍这个逻辑，就能完整讲出来。

---

### 🧠 Activity 启动流程 · 脑图记忆法

#### 一、 总纲（一句话定调）

**应用进程发请求 → 系统进程管调度 → 应用进程建实例。**

核心是：**两次 Binder 通信 + 一次线程切换**。

---

#### 二、 第一阶段：应用发起（Client 端）

目标：从 App 点按钮，到跨进程发出请求。

1. **入口**：`Context.startActivity()`

2. **跳转**：`Instrumentation.execStartActivity()`

3. **跨进程**：通过 Binder 拿到 `ATMS` 的代理，发起启动请求。

4. **关键点**：这一步是 App 主动去“求”系统。

---

#### 三、 第二阶段：系统调度（System 进程）

目标：系统检查权限、管理栈、准备进程。

1. **ATMS 处理**（ActivityTaskManagerService）：

    - ✅ **权限检查**

    - 📶 **Task 栈管理**（决定放在哪个任务栈）

2. **进程创建**（如果 App 进程没启动）：

    - ATMS 通知 AMS → 通过 **Zygote** Fork 出新进程。

3. **回调应用**（核心 Binder 路）：

    - 系统进程通过 Binder 回调应用的 `ApplicationThread`。

---

#### 四、 第三阶段：应用接收（App 落地）

目标：从系统回调，到真正执行 `onCreate`。

**这是面试必讲的一段「黄金链路」：**

1. **系统回调**：`ApplicationThread.scheduleTransaction()`

    - 这是系统告诉 App：“该启动了”。

    - *注意：运行在 Binder 线程池。*

2. **线程切换**：`ActivityThread.H` 发送 `EXECUTE_TRANSACTION`

    - *核心原理*：Binder 线程不能操作 UI/生命周期，必须切回**主线程**。

3. **应用入口**：`ActivityThread.handleLaunchActivity()`

    - *你指定的终点*。App 端真正的创建入口。

4. **最终构建**：

    - 创建 `ContextImpl`

    - 反射创建 Activity 实例

    - 调用 `attach()` 绑定 Window

    - 触发 `onCreate()`

---

### ⚡️ 面试口述速记（30 秒逻辑串）

面试官问：讲一下 Activity 启动流程，从 startActivity 到 handleLaunchActivity。

**你直接背这段：**

整个流程分**三步走**。

**第一步，应用发起**：App 调用 startActivity，最终通过 Instrumentation 跨进程调用到 ATMS。

**第二步，系统调度**：ATMS 做权限检查和 Task 栈管理，如果进程不存在，会通过 Zygote fork 出新进程，然后通过 Binder 回调应用的 ApplicationThread。

**第三步，应用落地**：因为 Binder 线程不能执行生命周期，所以通过 ActivityThread 的 H Handler 切换到主线程，最终调用 **handleLaunchActivity**，完成 Activity 实例化、attach 并回调 onCreate。

---

### 📌 核心加分点（P7 必挖）

1. **ATMS vs AMS**：一定要说 Android Q 之后调度逻辑移到了 **ATMS**。

2. **线程切换**：必须强调 `ApplicationThread` 在 Binder 线程，必须通过 `H` Handler 切回主线程。

3. **Zygote**：解释清楚进程孵化是通过 Zygote fork，效率高。

按这个脑图逻辑讲，条理最清晰，逻辑最严谨，祝你面试拿高分！
> （注：文档部分内容可能由 AI 生成）