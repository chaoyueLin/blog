# 自定义Lint


## 检测时机


- 本地手动检查


- 编码实时检查


- 编译时检查


- commit检查


- 在CI系统中提Pull Request时检查


- 打包发版时检查



## 主要目标


- 重点关注高优先级问题，屏蔽低优先级问题。正如前面所说，如果代码检查报告中夹杂了大量无关紧要的问题，反而影响了关键问题的发现。


- 高优问题的解决，要有一定的强制性。当检查发现高优先级的代码问题时，给开发者明确直接的报错，并通过技术手段约束，强制要求开发者修复。



## 检查哪些问题


- Crash预防
    - 原生的NewApi，用于检查代码中是否调用了Android高版本才提供的API。在低版本设备中调用高版本API会导致Crash。

    - 自定义的SerializableCheck。实现了Serializable接口的类，如果其成员变量引用的对象没有实现Serializable接口，序列化时就会Crash。我们制定了一条代码规范，要求实现了Serializable接口的类，其成员变量（包括从父类继承的）所声明的类型都要实现Serializable接口。

    - 自定义的ParseColorCheck。调用 Color.parseColor() 方法解析后台下发的颜色时，颜色字符串格式不正确会导致IllegalArgumentException，我们要求调用这个方法时必须处理该异常


- 性能/安全问题

    - ThreadConstruction：禁止直接使用 new Thread() 创建线程（线程池除外），而需要使用统一的工具类在公用线程池执行后台操作。

    - LogUsage：禁止直接使用android.util.Log ，必须使用统一工具类。工具类中可以控制Release包不输出Log，提高性能，也避免发生安全问题。

