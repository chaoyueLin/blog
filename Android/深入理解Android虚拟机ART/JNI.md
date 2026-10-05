# ART 中的 JNI

> 图片摘自《深入理解 Android：Java 虚拟机 ART》第 7、8、11 章，用源码剖析 JavaVM / JNIEnv 在 ART 中的实现。

```mermaid
mindmap
  root((ART 中的 JNI))
    JavaVM
      _JavaVM 与 JNInvokeInterface
      JavaVMExt
      checkJni 开关
    JNIEnv
      _JNIEnv 与 JNINativeInterface
      JNIEnvExt
      每线程一个
      jfieldID 即 ArtField
      jmethodID 即 ArtMethod
      jobject 即 mirror Object
    JNI 引用管理
      globals_ 全局引用
      locals_ 局部引用
      IRTable 间接引用表
      cookie 保存与还原
```

---

## 一、JavaVM：虚拟机的 JNI 抽象

- `_JavaVM` 是一个结构体（在 C++ 中，结构体也是一种类的类型）。当定义了 `__cplusplus` 宏时（按 C++ 来编译），`_JavaVM` 还有一个类型别名，即 `JavaVM`。所以，**JavaVM 的真实数据类型是 `_JavaVM`**。
- `JNIInvokeInterface` 也是结构体，其 `AttachCurrentThread`、`GetEnv` 等成员变量的数据类型都是函数指针。
- `_JavaVM` 结构体的第一个成员变量指向一个 `JNIInvokeInterface` 对象。
- `JavaVMExt` 是一个类，它从 `_JavaVM` 中派生。JNI 或 runtime 模块里往往通过一个 `JavaVM*` 类型的指针来引用一个 JavaVM 对象，因此 **ART 中 JavaVM 对象的真正数据类型是 JavaVMExt**。
- `JavaVMExt` 的构造函数中，`functions` 是基类 `_JavaVM` 的第一个成员（类型为 `JNIInvokeInterface*`），而 `unchecked_functions_` 是 JavaVMExt 的成员（类型也是 `JNIInvokeInterface*`）：
  - 如果不启用 jni 检查，二者指向同一个 `JNIInvokeInterface` 对象；
  - 如果启用 jni 检查，二者指向不同的对象：`unchecked_functions_` 代表**无需** jni 检查的对象，而 `functions_` 代表**需要** jni 检查的对象。当 `functions_` 做完 jni 检查后，会调用 `unchecked_functions_` 对应的函数。是否启用由 `CheckJni` 选项决定。

![JavaVM 类关系](images/f4f6fd54-ce46-47cf-9476-331c8061c5ef.jpg)

---

## 二、JNIEnv：线程私有的 JNI 上下文

`JNIEnvExt` 的思路和 `JavaVMExt` 类似：`_JNIEnv` 是结构体，`JNIEnv` 是它在 C++ 下的类型别名；`JNINativeInterface` 是包含了很多函数指针的结构体；`_JNIEnv` 中的 `functions` 成员指向 `JNINativeInterface`，`GetVersion`、`DefineClass`、`FindClass` 等函数最终都是调用 `functions` 中同名的函数。

`JNIEnvExt` 是 `JNIEnv` 的派生类，其创建通过 `JNIEnvExt::Create` 完成，同时会做 `CheckLocalsValid` 检查。`JNIEnvExt` 的构造依赖当前线程（`self_in`）和 JavaVMExt（`vm_in`）——**每个线程都有自己的 JNIEnv**。和 JavaVMExt 一样，它也有 `functions` 和 `unchecked_functions` 这对成员，且启用 checkJni 时 `functions` 指向 `gCheckNativeInterface`。

几个 JNI 类型在 ART 中的真实身份（由 `DecodeJObject`、`AddLocalReference` 等函数的实现可以看出）：

- **jfieldID** 其实就是 `ArtField*`；
- **jmethodID** 其实就是 `ArtMethod*`；
- **jobject** 指向一个 mirror Object 对象，其具体类型需要再由 `mirror::Object*` 向下转换为指定类型；`jobject` 在 JNI 层代表一个 Java Object，与 mirror Object 在虚拟机中代表一个 Java Object 的作用是一致的。

![JNIEnv 声明](images/f96328ed-6fad-435e-b837-b9c9bce55b11.jpg)

![JNIEnvExt 实现](images/9125f5a1-5b32-4918-9d6d-fb45f53ae3b9.jpg)

![JNIEnv 创建](images/1129447c-dda5-4294-bd85-8d1e8bf76dd6.jpg)

![jfieldID 与 jmethodID](images/7eac290e-17b7-44cd-b5ee-5ba98aca08b3.jpg)

![JNIEnv 源码一](images/744ae43d-bc37-4ee2-b37b-bfdc717e2791.jpg)

![JNIEnv 源码二](images/0f081855-b419-48de-b57b-d4127d3e3e56.jpg)

---

## 三、JNI 引用管理：全局引用与局部引用

- **全局引用**：创建借助 JavaVMExt 的 `AddGlobalRef` 完成。一个 Java 进程中只有一个 JavaVMExt 对象（代表虚拟机本身），其中有一个 `globals_` 成员变量，类型是 `IndirectReferenceTable`，可存储进程中创建的所有全局引用对象。每个全局引用对象添加到 `globals_` 容器后都会得到一个 `IndirectRef` 值，外界需通过 IRTable 的 Get 函数将一个 IndirectRef 值还原为对应的 mirror Object 对象。
- **局部引用**：创建使用 JNIEnvExt 的 `AddLocalRef` 完成，每一个 JNIEnvExt 对象都包含一个 `locals_` 成员变量，用于存储在这个 JNIEnv 环境里创建的局部引用对象。
- **IRTable（IndirectReferenceTable）**：将 IrtEntry 元素按照数组的方式管理，数组的头由 `table_` 成员变量表示。`max_entries_` 指明这个 IRTable 能包含多少 IrtEntry 元素：Global 和 WeakGlobal 类型（由成员变量 `kind_` 指明）的 IRTable 能保存最多 **51200** 个元素，而 Local 类型的 IRTable 只能保存最多 **512** 个元素。添加或删除一个引用对象就是围绕 `table_` 数组展开的，有两个状态信息需要知道：目前 `table_` 中被占用的索引的最高位（`parts.topIndex`，不能超过 `max_entries`），以及 `table_[0, topIndex]` 这段数组中是否有空洞（`parts.numHoles`，空洞的原因是删除了非尾部的元素，新 new 出来的对象可以保存在空洞的索引位）。
- **JniMethodStart / JniMethodEnd**：对表达 IRTable 存储状态的 **cookie** 做了精心的保护和还原——`JniMethodStart` 先保存旧值到 `saved_local_ref_cookie`，并把当前 locals IRTable 的存储空间状态存入 `env->local_ref_cookie`；`JniMethodEnd` 则调用 `PopLocalReferences`，把 IRTable 的存储状态和 `local_ref_cookie` 恢复回来，并 `PopHandleScope`。

![全局引用创建](images/66d11237-6e4e-4db8-905c-7f72829c5010.jpg)

![NewGlobalRef 实现](images/8aec237f-0e60-4b86-970e-9854c6f0245b.jpg)

![引用管理一](images/d4c621e2-7c06-4f14-b3b2-fdb4967771c0.jpg)

![引用管理二](images/05315ae8-ef68-4b90-bffe-8a74971ac068.jpg)

![引用管理三](images/6dfd2b7c-aeed-404b-a7a7-bc8e0e4d276c.jpg)

![引用管理四](images/8d1ebd25-5428-4abe-92eb-73764b501368.jpg)

![引用管理五](images/f0078b76-02e1-402e-a3b8-059e2ad21912.jpg)

![IRTable 构造函数](images/e86e7955-0408-4aed-98f8-9911a9f84e17.jpg)

![JniMethodStart 与 JniMethodEnd](images/d4f1e277-74d0-4370-9327-ee4c34a3d04d.jpg)

![引用管理六](images/d389cf77-fadf-416e-a133-c5e01fe11944.jpg)
