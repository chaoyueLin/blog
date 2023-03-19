# Handler
Android 应用是通过消息驱动运行的，在 Android 中一切皆消息，包括触摸事件，视图的绘制、显示和刷新等等都是消息。Handler 是消息机制的上层接口，平时开发中我们只会接触到 Handler 和 Message，内部还有 MessageQueue 和 Looper 两大助手共同实现消息循环系统。

## Handler

- sendMessage,sendMessageDelayed,post,postDelayed最后还是调用sendMessageAtTime

    - SystemClock.uptimeMillis(),系统开机到现在的时间

    - post后把runable包装成message


- dispatchMessage处理消息

    - msg.callback != null

    - mCallback.handleMessage(msg)

    - handleMessage(msg);



## Message

- obtain取


- recycleUnchecked链表头结点插入来回收


- 同步消息：正常情况下我们通过Handler发送的Message都属于同步消息，除非我们在发送的时候执行该消息是一个异步消息。同步消息会按顺序排列在队列中，除非指定Message的执行时间，否咋Message会按顺序执行。


- 异步消息： 想要往消息队列中发送异步消息，我们必须在初始化Handler的时候通过构造函数public Handler(boolean async)中指定Handler是异步的，这样Handler在讲Message加入消息队列的时候就会将Message设置为异步的。


- 障栅(Barrier)： 障栅(Barrier) 是一种特殊的Message，它的target为null(只有障栅的target可以为null，如果我们自己视图设置Message的target为null的话会报异常)，并且arg1属性被用作障栅的标识符来区别不同的障栅。障栅的作用是用于拦截队列中同步消息，放行异步消息。就好像交警一样，在道路拥挤的时候会决定哪些车辆可以先通过，这些可以通过的车辆就是异步消息。



## Looper


- Looper.myLooper()通过ThreadLocal保存当前线程的looper对象


- loop()循环从MessageQueue取消息处理消息



## MessageQueue

- enqueueMessage，当头节点为空或当前 Message 需要立即执行或当前 Message 的相对执行时间比头节点早，则把当前 Message 插入头节点

- next

    - msg.target == null内存屏障一直等待到msg被移除

    - IdleHandler ，接口里的 boolean queueIdle 方法。这个方法的返回 true 则调用后保留，下次队列空闲时还会继续调用；而如果返回 false 调用完就被 remove 了。可以用到做延时加载，而且是在空闲时加载。


- 通过 nativeInit 关联了 Native 层的 MessageQueue，在 Native 层的 MessageQueue 中创建了 Native 层的 Looper。mWakeEventFd = eventfd(0, EFD_NONBLOCK | EFD_CLOEXEC);int result = epoll_ctl(mEpollFd, EPOLL_CTL_ADD, mWakeEventFd, & eventItem);mWakeEventFd当有写入被唤醒

    - nativePollOnce 是用来检查当前线程的消息队列中是否有新的消息需要处理，nextPollTimeoutMills 用来描述当消息队列没有新的消息需要处理时，当前线程需要进去睡眠等待状态的时间。如果监听的文件描述符没有发生 IO 读写事件，那么当前线程就会在 epoll_wait 中进入睡眠等待状态，等待的时间由最后一个参数 timeoutMillis 来指定。

    - ssize_t nWrite = TEMP_FAILURE_RETRY(write(mWakeEventFd, &inc, sizeof(uint64_t)));被唤醒，线程有新的消息需要处理时，它首先会在 C++ 层的 Looper 类的成员函数 pollInnter 中被唤醒，然后沿着之前的调用路径一直返回到 Java 层的 Looper 类的静态成员函数 loop 中，最后就可以对新的消息进行处理了。

- MessageQueue.postSyncBarrier()，本质上就是通过创建一个 target 成员为 null 的 Message 并插入到消息队列中，最经典的实现就是 ViewRootImpl 调用 scheduleTraversals 方法进行视图更新时的使用，在执行 doTraversal 方法后，才会移除分割栏.在next()中如果设置了同步屏障，那么就会通过do..while()循环优秀去找消息列表中的异步消息，找到后返回。所以所有的异步消息都处理完后，才会处理同步消息。同步屏障就是添加了一个标识，这个标识是一个没有target的Message。如果有这个标识，就先去处理异步消息。


- setAsynchronous ,而异步消息则没有影响，消息默认就是同步消息，除非我们调用了 Message 的 setAsynchronous


## I/O多路复用的三种形式


- select:知道了有I/O事件发生了，却并不知道是哪那几个流（可能有一个，多个，甚至全部），我们只能无差别轮询所有流，找出能读出数据，或者写入数据的流，对他们进行操作。所以select具有O(n)的无差别轮询复杂度，同时处理的流越多，无差别轮询时间就越长


- poll:本质上和select没有区别，它将用户传入的数组拷贝到内核空间，然后查询每个fd对应的设备状态， 但是它没有最大连接数的限制，原因是它是基于链表来存储的


- epoll(Linux内核所特有):可以理解为event poll，不同于忙轮询和无差别轮询，epoll会把哪个流发生了怎样的I/O事件通知我们。所以我们说epoll实际上是事件驱动（每个事件关联上fd）的，此时我们对这些流的操作都是有意义的。（复杂度降低到了O(1)）(Epoll最大的优点就在于它只管你“活跃”的连接，而跟连接总数无关，因此在实际的网络环境中，Epoll的效率就会远远高于select和poll)



## 管道
Linux的IPC之一

