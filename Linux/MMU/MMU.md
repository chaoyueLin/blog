每个进程都有自己独立的虚拟地址空间。
什么是MMU
![MMU_principle_updated](./80d178e0-e0f4-47c5-9fbf-db15c458e376.png)

MMU全称是内存管理单元，它将物理内存分割成多个pages，MMU管理进程的虚拟地址空间中的PAGE和物理内存中的PAGE之间的映射关系。 
因为是映射，所以随时都可能发生变化，例如某个进程虚拟内存空间中的PAGE1，在不同的时间点，可能出现在物理内存中的不同位置（当发生了页交换时）。
什么是page fault
当进程访问它的虚拟地址空间中的PAGE时，如果这个PAGE目前还不在物理内存中，此时CPU是不能干活的， 
Linux会产生一个hard page fault中断。 
系统需要从慢速设备（如磁盘）将对应的数据PAGE读入物理内存，并建立物理内存地址与虚拟地址空间PAGE的映射关系。 
然后进程才能访问这部分虚拟地址空间的内存。
page fault 又分为几种，major page fault、 minor page fault、 invalid(segment fault)。
major page fault也称为hard page fault, 指需要访问的内存不在虚拟地址空间，也不在物理内存中，需要从慢速设备载入。从swap回到物理内存也是hard page fault。
minor page fault也称为soft page fault, 指需要访问的内存不在虚拟地址空间，但是在物理内存中，只需要MMU建立物理内存和虚拟地址空间的映射关系即可。 
（通常是多个进程访问同一个共享内存中的数据，可能某些进程还没有建立起映射关系，所以访问时会出现soft page fault）
invalid fault也称为segment fault, 指进程需要访问的内存地址不在它的虚拟地址空间范围内，属于越界访问，内核会报segment fault错误。

虚拟页可能有这几种状态

* 第一种,未分配
* 第二种,已经分配但是未映射到物理内存;
* 第三种,已经分配并且已经映射到物理内存
* 第四种,已经分配并映射到Swap分区(在 Android中此种情况基本不存在)。

希望物理内存的利用最大化，只不过两者采用不同的策略，一个是Swap，一个是LowMemoryKill。
比较一下Swap和LowMemoryKill的区别：
Swap通过将最不频繁使用的进程数据写入磁盘，来给物理内存腾出空间。
LowMemoryKill通过给进程分等级，来选择级别最低去杀死来腾出物理内存空间。Android系统会考虑进程的回收利益，当Android系统开始杀死LRU缓存中的进程时，系统会判断每个进程杀死后带来的回收收益。因为Android总是倾向于杀死一个能回收更多内存的进程，从而可以杀死更少的进程，来获取更多的内存。杀死的进程越少，对用户体验的影响就越小。
相比较而言，LowMemoryKill这种策略更符合手机OS的用户体验需求。

