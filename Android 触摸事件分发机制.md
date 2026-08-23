# Android 触摸事件分发机制（面试完整版）
## 一、基础概念
触摸事件载体：`MotionEvent`
一次完整事件序列：**DOWN → 若干MOVE → UP / CANCEL**
> 重点：**DOWN事件如果没有任何View消费，整条事件序列直接作废，后续MOVE、UP不会下发**

事件传递链路：
`Activity → DecorView(根ViewGroup) → ViewGroup → 子ViewGroup → View`

三大核心方法：
1. `dispatchTouchEvent()`：事件分发入口，**Activity、ViewGroup、View全部拥有**
2. `onInterceptTouchEvent()`：**仅ViewGroup独有**，判断是否拦截事件
3. `onTouchEvent()`：消费事件，ViewGroup、View都具备

## 二、完整分发流程
### 1. Activity
`Activity.dispatchTouchEvent()` → Window → DecorView（根ViewGroup）

### 2. ViewGroup 分发逻辑
```
ViewGroup.dispatchTouchEvent()
    ↓
执行 onInterceptTouchEvent()
├─ 返回 true：拦截事件，不再下发子View，交给当前ViewGroup onTouchEvent()
└─ 返回 false：不拦截，从上层子View到下层（倒序遍历）递归分发
        ↓
        子View.dispatchTouchEvent()
            ├─ 子View消费成功(true) → 终止传递，逐层向上返回true
            └─ 所有子View都不消费 → 当前ViewGroup执行自身onTouchEvent()
```

> 关键规则：
> 如果在**DOWN事件返回true拦截**，同一事件序列后续MOVE、UP，**不会再次调用onInterceptTouchEvent**！

### 3. View（普通控件，无拦截方法）
```
View.dispatchTouchEvent()
    1. 优先执行 OnTouchListener -> onTouch()
        ├ onTouch返回true：直接消费，不再执行onTouchEvent
        └ onTouch返回false：继续调用onTouchEvent()
    2. onTouchEvent()
        ├ true：消费事件，传递终止
        └ false：事件向上抛给父容器尝试处理
```
执行优先级：
`onTouchListener > onTouchEvent > onClickListener`
> onClick触发必要条件：收到UP事件，且中途没有被拦截、提前消费。

## 三、三大方法返回值含义
1. **boolean onInterceptTouchEvent()（ViewGroup专属）**
- `true`：拦截事件，停止向下分发
- `false`：放行，事件继续传递给子View

2. **boolean onTouchEvent()**
- `true`：消费事件，传递结束
- `false`：不消费，事件向上回溯父容器

3. **requestDisallowInterceptTouchEvent(boolean)**
- `true`：子View请求**禁止父ViewGroup拦截**
- `false`：恢复父容器拦截权限
⚠️ 生效范围：**同一条触摸事件序列（DOWN~UP）**
⚠️ 最佳调用时机：ACTION_DOWN中调用

## 四、滑动冲突两大解决方案（面试核心）
### 方案1：外部拦截法【推荐】
逻辑写在父ViewGroup，在`onInterceptTouchEvent`根据滑动方向、区域动态决定是否拦截。
优点：逻辑收敛在父容器，耦合低，不容易出现CANCEL异常。

```kotlin
class ConflictParentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
    private var lastX = 0f
    private var lastY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                // DOWN禁止拦截，否则子View无法接收点击事件
                false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX
                val dy = y - lastY
                // 示例：纵向滑动父容器拦截，横向交给子View
                kotlin.math.abs(dy) > kotlin.math.abs(dx)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> false
            else -> false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean = true
    override fun layout(l: Int, t: Int, r: Int, b: Int) {}
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {}
}
```

### 方案2：内部拦截法
子View主动调用`requestDisallowInterceptTouchEvent`阻止父容器拦截。
适合无法修改父容器源码场景。

```kotlin
childView.setOnTouchListener { view, event ->
    val parent = view.parent as ViewGroup
    when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }
    false // 不消费事件，继续走原有onTouchEvent逻辑
}
```

## 五、经典冲突实战场景
### 场景1：ScrollView 嵌套 RecyclerView（竖向冲突）
现象：滑动RecyclerView时，ScrollView优先滚动。
解决思路：
- 内部拦截：RecyclerView滑动时禁止父ScrollView拦截
- 外部拦截：自定义ScrollView，判断触摸区域，RecyclerView区域不拦截纵向滑动

### 场景2：ViewPager(横向) 嵌套横向RecyclerView
横向滑动冲突。
方案：RecyclerView检测到左右滑动时，禁止ViewPager拦截。

### 场景3：ViewGroup局部可滑动、区域可点击
重写onInterceptTouchEvent：
DOWN一律放行；MOVE滑动距离超过阈值后再拦截。
> 禁忌：不要拦截DOWN事件，否则子View无法响应点击。

## 六、高频坑点 & 面试追问
1. DOWN事件一旦被父容器拦截，子View收不到任何事件，点击直接失效。
2. `requestDisallowInterceptTouchEvent` **仅对直接父容器生效，不能跨隔代ViewGroup**。
3. ACTION_CANCEL触发时机：父容器中途拦截事件，子View收到CANCEL，业务需要重置拖拽、按压状态。
4. onClick失效常见原因：父提前拦截、onTouch返回true消费事件、UP事件被截断。
5. 事件序列断裂：DOWN无人消费，后续MOVE/UP全部丢弃。

## 七、面试背诵标准答案
> Android触摸事件从Activity开始分发，传递顺序：Activity → ViewGroup → View。
> ViewGroup通过`onInterceptTouchEvent`决定是否拦截；不拦截则继续向下分发子View。View没有拦截方法，依靠`onTouchEvent`消费事件。整体遵循**先下发，消费后向上回溯**的规则。
>
> 滑动冲突主流两种方案：
> 外部拦截法：重写父ViewGroup `onInterceptTouchEvent`，根据滑动方向动态拦截，优先推荐；
> 内部拦截法：子View调用`requestDisallowInterceptTouchEvent`禁止父拦截。
>
> 外部拦截优势：代码集中在父容器，耦合更低，减少CANCEL事件带来的状态异常。

## 八、延伸面试题（可自行思考）
1. 外部拦截与内部拦截各自优缺点？
2. 多层嵌套ViewGroup滑动冲突怎么处理？
3. CANCEL事件业务上有哪些注意事项？
4. 为什么DOWN事件尽量不要拦截？

---

如果你需要，我可以把【Kotlin Flow全套笔记 + 事件分发这份文档】合并成一份完整的**Android高级面试.md**汇总文件。

内容由 AI 生成