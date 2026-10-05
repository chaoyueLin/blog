# ART Runtime

> 本篇为《深入理解 Android：Java 虚拟机 ART》中 Runtime 相关章节的书页截图归档，按主题分组，配合每节导读使用。

```mermaid
mindmap
  root((ART Runtime))
    虚拟机创建
      JNI_CreateJavaVM 调用链
      Runtime Create 与 Start
      加载关键动态库
    Thread
      Startup 与 Attach
      pthread TLS 线程特有数据
      Resume 条件变量
    线程锁
      Monitor 与 Mutex
      MonitorId 与 LockWord
      wait notify notifyAll
    Mirror
      Java 类的 C++ 镜像
      Object Class String Array
    ArtField 与 ArtMethod
      dex 索引与偏移
    SystemClassLoader
      创建与设置 contextClassLoader
```

---

## 一、虚拟机创建

`JNI_CreateJavaVM` 从 `AndroidRuntime.cpp` 出发，经 `JniInvocation` 转到 `libart.so` 中真正的实现：先为虚拟机准备参数，然后 `Runtime::Create` 创建 Runtime 对象（**它就是 ART 虚拟机的化身**），再调用 `Runtime::Start` 启动虚拟机，最后取出 JNIEnv 和 JavaVM 返回 `JNI_OK`。期间还会通过 `/etc/public.libraries.txt` 中描述的文件路径加载关键动态库。

![虚拟机创建一](images/35b88e51-c918-47e4-9485-df6acfbc0019.jpg)

![虚拟机创建二](images/8ee8d69c-74f6-470e-8179-3d55a64fcfff.jpg)

![虚拟机创建三](images/3ca169f1-f5b5-4d72-80b6-45bd7f1989ed.jpg)

![虚拟机创建四](images/4fafcc89-aa14-474e-ae73-23cfadb58b1f.jpg)

---

## 二、Thread

`Runtime::Init` 中与 Thread 相关的两个关键函数是 `Thread::Startup`（初始化线程模块）和 `Thread::Attach("main", ...)`（把主线程挂载到虚拟机）。`Thread::Startup` 里会调用 `pthread_key_create` 创建线程特有数据（Thread Local Storage）区域，并用一个 key（`pthread_key_self_`）来索引；同时创建用于线程 resume 的条件变量。本节图片覆盖 Thread 的启动、挂载与线程本地存储等实现细节。

![Thread 一](images/17abe534-2766-47d5-bcfd-d6a17b446b26.jpg)

![Thread 二](images/61d72728-ef8c-4c90-b705-91861ea872d8.jpg)

![Thread 三](images/7978f252-68e5-49eb-a8cf-2a7dd5ccbf9c.jpg)

![Thread 四](images/31c7e30b-ac18-48b8-862d-6abbbea7956e.jpg)

![Thread 五](images/ce0e99c0-c03d-4e73-aa39-b56a2410b5ea.jpg)

![Thread 六](images/f0e158a7-0364-45ba-bd40-a89c05531aa4.jpg)

![Thread 七](images/b5d70459-c8ba-4b8f-b6f5-c8f7090d8e0e.jpg)

![Thread 八](images/4adfc727-58f5-4b41-baf6-3e6f5e61b816.jpg)

![Thread 九](images/4430a060-437e-4b66-abac-2d9958b70605.jpg)

![Thread 十](images/23d9b537-16df-4217-bc46-7850d600ba0d.jpg)

![Thread 十一](images/9cc6c377-d453-4d1b-8f2e-e45f108f2036.jpg)

![Thread 十二](images/b1c33756-840c-470f-ab4c-1e902c5aa66e.jpg)

![Thread 十三](images/a6061b3b-b28f-4e40-9337-a92dbacc3dbf.jpg)

![Thread 十四](images/fa9c09bf-d0da-4067-a892-8e09efdc07ec.jpg)

![Thread 十五](images/ed3faa43-2f7e-476d-ba8d-70ce44849f49.jpg)

![Thread 十六](images/254a7c10-c5aa-4cbc-a097-7f3b9d9c04fc.jpg)

![Thread 十七](images/7391b612-2bc0-4668-9fa4-c39fce1189c7.jpg)

![Thread 十八](images/73ba10a3-2134-44b3-8b18-2522f3518b2f.jpg)

![Thread 十九](images/ae6afe22-8b49-4d10-b682-fe153747da6f.jpg)

---

## 三、线程锁

ART 中真正实现线程同步功能的是 **Monitor** 类：它包含一个 `monitor_lock_`（类型为 Mutex，Mutex 是 ART 在操作系统同步机制之上的封装，底层可用 futex 系统调用或 pthread_mutex，由 `ART_USE_FUTEXES` 宏控制）；每一个 Monitor 对象都有一个 32 位无符号的 MonitorId，并关联一个 Object（由 `object_` 成员指向）；Monitor 提供 `Lock`、`Unlock`、`Wait`、`Notify`、`NotifyAll` 等成员函数。围绕它的还有 MonitorPool、MonitorList、LockWord 等结构。

![线程锁一](images/fffd6396-d5b8-4fc2-9034-9479250da426.jpg)

![线程锁二](images/80d3a8bc-c1f0-4b2f-b16e-a86534560823.jpg)

![线程锁三](images/eaf8b927-1206-4d49-af78-47e30be10476.jpg)

![线程锁四](images/c127f6a2-9b1d-41ff-b033-7dd745bfe87a.jpg)

![线程锁五](images/f186aa03-bc62-4364-a805-f834fa605311.jpg)

![线程锁六](images/24b132a7-2e9b-441b-bb9a-9737c6e496a7.jpg)

![线程锁七](images/5a7743db-cc6b-42e8-b63f-6bf80ffd6bc2.jpg)

![线程锁八](images/2247e5bc-71df-4c23-bcdb-fa8bbe5d2490.jpg)

![线程锁九](images/b793fd2e-dd0a-4739-8b32-2dbc8b03dee4.jpg)

---

## 四、Mirror：Java 对象的 C++ 镜像

ART 源码中有一个 mirror 子文件夹，其中定义的类都位于 mirror 命名空间中。Java 的某些类在虚拟机层也有对应的 C++ 类：Object 对应 Java 的 Object 类，Class 对应 Java 的 Class 类；DexCache、String、Throwable、StackTraceElement 等与同名 Java 类相对应。Array 对应 Java 的 Array 类，基础数据类型的数组（如 int[]、long[]）对应 `PrimitiveArray<int>`、`PrimitiveArray<long>`，其他类型的数组用 `ObjectArray<T>` 模板类描述。注意 IfTable 在 Java 层没有对应类。

![Mirror Object 家族](images/3c7a0b88-894f-4d06-8166-e3d5e852189f.jpg)

---

## 五、ArtField 与 ArtMethod

Java 源码中的 class 可以包含成员变量和成员函数。当 class 经过 dex2oat 编译转换后，一个类的成员变量和成员函数的信息将转换为对应的 C++ 类，即 **ArtField** 和 **ArtMethod**：

- `declaring_class_` 成员变量指向声明该成员的类是谁；
- `access_flags_` 描述该成员的访问权限，比如是 public 还是 private；
- ArtField 的 `field_dex_idx_` 为该成员在 dex 文件中 field_ids 数组里的索引；
- ArtMethod 的成员变量同样与 dex 文件格式密切相关，如 `dex_code_item_offset_` 为该函数对应字节码在 dex 文件里的偏移量，`dex_method_index_` 为该成员在 dex 文件中 method_ids 数组里的索引。

![ArtField 与 ArtMethod](images/cd30ac76-53d3-478c-be3c-095b2b431eae.jpg)

---

## 六、SystemClassLoader

`Runtime::CreateSystemClassLoader` 负责创建系统类加载器：先取得 `java.lang.ClassLoader` 对应的 Class 对象，找到并调用其 `getSystemClassLoader` 方法；拿到返回的 jobject 后，一方面通过 `SetClassLoaderOverride` 记录到当前线程，另一方面取出 `java.lang.Thread` 类的 `contextClassLoader` 成员变量并设置该对象，最后创建该对象的全局引用返回。

![SystemClassLoader](images/13d3fc20-3def-4ab3-82fd-74d1d632e0ed.jpg)
