# ViewModel 屏幕旋转不重建 + onCleared 调用时机（面试Markdown）
## 📑大纲
1. 屏幕旋转Activity重建本质
2. ViewModel为什么旋转不会销毁（核心原理）
3. ViewModelStore 存储位置
4. `onCleared()` 调用时机（高频坑）
5. 常见误区
6. 面试背诵短句

---

## 1. 屏幕旋转发生了什么
屏幕旋转 → **Activity 销毁重建**
- `onDestroy()` → 新Activity实例创建 `onCreate()`
- 普通成员变量全部丢失，Activity对象直接回收。
> 注意：**只是Activity销毁，所属的Activity非配置变更范围的对象不会被销毁**

## 2. ViewModel 为什么屏幕旋转不重建
### 核心链路
ViewModel 的实例不是保存在 Activity 内部，而是保存在 **`ViewModelStore`**。
`ViewModelStore` 对象存放在 **`Activity#onRetainNonConfigurationInstance()` 返回的 NonConfigurationInstances**。

1. 配置变更（屏幕旋转、语言切换）触发Activity销毁
2. 系统不会销毁 `NonConfigurationInstances`，把这个对象临时保留在系统Activity管理器中
3. 新Activity重建，系统取出旧的 `NonConfigurationInstances`，拿到里面的 `ViewModelStore`
4. 新Activity通过ViewModelProvider从复用的ViewModelStore取出旧ViewModel实例直接使用
5. **ViewModel 引用继续存活，不会重新new**

> 关键点：
> ✅ ViewModel 不持有 Activity 引用；持有 `SavedStateHandle` 可以保存少量配置变更数据
> ❌ 不是ViewModel不会销毁，是**配置变更场景下ViewModelStore被系统暂存，不回收**

### 流程图一句话
配置变更 → Activity销毁 → **ViewModelStore 保留在 NonConfigurationInstances** → 新Activity复用上一份ViewModelStore → ViewModel复用

> Fragment场景：Fragment的ViewModelStore存在FragmentManager，Fragment重建同样复用ViewModel。

## 3. ViewModel什么时候真正销毁？
只有**Activity真正finish（彻底退出）**，不是配置变更：
- 用户back退出Activity
- `finish()` 调用
- 系统内存不足杀死App进程

此时 `ViewModelStore` 被清空，遍历所有ViewModel调用 `onCleared()`。

## 4. onCleared() 调用时机【高频考点】
> `onCleared()`：ViewModel销毁前回调，适合关闭协程、取消Flow订阅、释放资源。

### ✅ 会调用 onCleared 的场景
1. Activity **正常finish结束**，ViewModelStore被清空 → `onCleared()`执行
2. Fragment被彻底remove、detach不再复用时，Fragment的ViewModelStore销毁 → `onCleared()`执行
3. 手动调用 `viewModelStore.clear()`，会立刻触发所有ViewModel的`onCleared()`

### ❌ **屏幕旋转（配置变更），不会调用 onCleared！！（最容易踩坑）**
旋转只是Activity销毁重建，ViewModel继续复用，**onCleared不会跑**。

### 误区
> 很多人误以为旋转会调用onCleared，这是错误。旋转只是销毁旧Activity，ViewModel还活着，不会执行onCleared。

### 额外：ViewModel里的viewModelScope
`viewModelScope` 是ViewModel自带协程作用域，**onCleared内部自动cancel这个协程**。
- 配置变更：viewModelScope不会取消，协程继续跑
- Activity finish：onCleared触发 → viewModelScope.cancel()，所有子协程全部停止

> 坑：如果网络请求在旋转期间，**旋转不会终止请求，请求继续执行**；只有页面真正退出才会cancel。

## 5. SavedStateHandle补充（拓展面试）
ViewModel本身内存保存，进程被杀重建后数据丢失；
`SavedStateHandle` 借助 `onSaveInstanceState`，可以保存少量键值，进程重启恢复数据。

## 6. 高频面试问答
### Q1：为什么屏幕旋转ViewModel不会重建？
> 配置变更时Activity销毁，ViewModel存放在ViewModelStore，ViewModelStore保存在Activity的NonConfigurationInstances对象，系统不会回收该对象；新Activity重建后复用同一个ViewModelStore，拿到旧ViewModel实例。

### Q2：onCleared什么时候调用？旋转会调用吗？
> Activity真正finish、Fragment彻底销毁、手动clear viewModelStore时才会调用。**屏幕旋转配置变更不会调用onCleared**。viewModelScope会在onCleared里自动取消协程。

### Q3：viewModelScope协程，屏幕旋转请求会断吗？
> 不会。旋转ViewModel实例存活，viewModelScope不会cancel，网络请求继续执行；只有页面真正finish退出才会终止。

### Q4：ViewModel能不能持有Context？
> 禁止持有Activity上下文；可以使用`getApplication()`获取Application Context，Application全局不会销毁。

## 7. 口述极简背诵版
> ViewModel实例存储在ViewModelStore，配置变更时ViewModelStore保存在NonConfigurationInstances，系统保留不销毁，新Activity复用，所以屏幕旋转不会重建。
> onCleared 在Activity真正finish、Fragment彻底销毁、手动clear才回调；**屏幕旋转不会触发onCleared**，viewModelScope在onCleared内部自动取消协程。

如果你需要，我可以顺带整理：**ViewModel、SavedStateHandle、ViewLifecycleOwner 三者对比速记**。

内容由 AI 生成