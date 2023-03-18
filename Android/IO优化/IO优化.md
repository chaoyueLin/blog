# IO优化


## native hook
![](./1.jpg)

* 主线程IO
* buffer太小，
读取buffer大小，那应该选用多大的 Buffer 呢？我们可以跟据文件保存所挂载的目录的 block size 来确认 Buffer 大小
* 重复读取
* 资源泄漏CloseGuard.java

