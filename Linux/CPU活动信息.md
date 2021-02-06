	/proc/stat
我们来解释一下这行数据各数值的含义：

1）这些数值的单位都是 jiffies，jiffies 是内核中的一个全局变量，用来记录系统启动以来产生的节拍数，在 Linux 中，一个节拍大致可以理解为操作系统进程调度的最小时间片，不同的 Linux 系统内核这个值可能不同，通常在 1ms 到 10ms 之间。

2）cpu 223447 240 4504182 410802165 59753 412 586209 0 0
user(223447) 从系统启动开始累积到当前时刻，处于用户态的运行时间，不包含 nice 值为负的进程。

nice(240) 从系统启动开始累积到当前时刻，nice 值为负的进程所占用的 CPU 时间。

system(4504182) 从系统启动开始累积到当前时刻，处于核心态的运行时间。

idle(410802165) 从系统启动开始累积到当前时刻，除 IO 等待时间以外的其他等待时间。

iowait(59753) 从系统启动开始累积到当前时刻，IO 等待时间。(since 2.5.41)

irq(412) 从系统启动开始累积到当前时刻，硬中断时间。(since 2.6.0-test4)

softirq(586209) 从系统启动开始累积到当前时刻，软中断时间。(since 2.6.0-test4)

stealstolen(0) Which is the time spent in other operating systems when running in a virtualized environment.(since 2.6.11)

guest(0) Which is the time spent running a virtual CPU for guest operating systems under the control of the Linux kernel.(since 2.6.24)

从以上信息我们可以得到总的 CPU 活动时间为：

totalCPUTime = user + nice + system + idle + iowait + irq + softirq + stealstolen + guest

	/proc/[PID]/stat

计算 CPU 占用率时用到：

pid 进程号。

utime 该任务在用户态运行的时间，单位为 jiffies。

stime 该任务在核心态运行的时间，单位为 jiffies。

cutime 累计的该任务的所有的 waited-for 进程曾经在用户态运行的时间，单位为 jiffies。

cstime 累计的该任务的所有的 waited-for 进程曾经在核心态运行的时间，单位为 jiffies。

该进程的 CPU 占用时间（该值包括其所有线程的 CPU 时间）：

processCPUTime = utime + stime + cutime + cstime



