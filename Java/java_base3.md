
## JAVA多线程相关
Thread、Runnable、Callable、Futrue类关系与区别
### RUNNABLE
其中Runnable应该是我们最熟悉的接口，它只有一个run()函数，用于将耗时操作写在其中，该函数没有返回值。然后使用某个线程去执行该runnable即可实现多线程，Thread类在调用start()函数后就是执行的是Runnable的run()函数。Runnable的声明如下 :

	public interface Runnable {
	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 *
	 * @see     java.lang.Thread#run()
	*/
	  public abstract void run();
	}

###CALLABLE
Callable与Runnable的功能大致相似，Callable中有一个call()函数，但是call()函数有返回值，而Runnable的run()函数不能将结果返回给客户程序。Callable的声明如下 :

	public interface Callable<V> {
	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception if unable to compute a result
	 */
	V call() throws Exception;
	}

可以看到，这是一个泛型接口，call()函数返回的类型就是客户程序传递进来的V类型。

### FUTURE
Executor就是Runnable和Callable的调度容器，Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果、设置结果操作。get方法会阻塞，直到任务返回结果(Future简介)。Future声明如下

	/**
	* @see FutureTask
	* @see Executor
	* @since 1.5
	* @author Doug Lea
	* @param <V> The result type returned by this Future's <tt>get</tt> method
	*/
	public interface Future<V> {
	
	  /**
	   * Attempts to cancel execution of this task.  This attempt will
	   * fail if the task has already completed, has already been cancelled,
	   * or could not be cancelled for some other reason. If successful,
	   * and this task has not started when <tt>cancel</tt> is called,
	   * this task should never run.  If the task has already started,
	   * then the <tt>mayInterruptIfRunning</tt> parameter determines
	   * whether the thread executing this task should be interrupted in
	   * an attempt to stop the task.     *
	   */
	  boolean cancel(boolean mayInterruptIfRunning);
	
	  /**
	   * Returns <tt>true</tt> if this task was cancelled before it completed
	   * normally.
	   */
	  boolean isCancelled();
	
	  /**
	   * Returns <tt>true</tt> if this task completed.
	   *
	   */
	  boolean isDone();
	
	  /**
	   * Waits if necessary for the computation to complete, and then
	   * retrieves its result.
	   *
	   * @return the computed result
	   */
	  V get() throws InterruptedException, ExecutionException;
	
	  /**
	   * Waits if necessary for at most the given time for the computation
	   * to complete, and then retrieves its result, if available.
	   *
	   * @param timeout the maximum time to wait
	   * @param unit the time unit of the timeout argument
	   * @return the computed result
	   */
	  V get(long timeout, TimeUnit unit)
	      throws InterruptedException, ExecutionException, TimeoutException;
	    }

### FUTURETASK
FutureTask则是一个RunnableFuture，而RunnableFuture实现了Runnbale又实现了Futrue这两个接口，

	public class FutureTask<V> implements RunnableFuture<V>
	RunnableFuture
	
	public interface RunnableFuture<V> extends Runnable, Future<V> {
	/**
	 * Sets this Future to the result of its computation
	 * unless it has been cancelled.
	 */
	void run();
	}

另外它还可以包装Runnable和Callable， 由构造函数注入依赖。

	public FutureTask(Callable<V> callable) {
	    if (callable == null)
	        throw new NullPointerException();
	    this.callable = callable;
	    this.state = NEW;       // ensure visibility of callable
	}
	
	public FutureTask(Runnable runnable, V result) {
	    this.callable = Executors.callable(runnable, result);
	    this.state = NEW;       // ensure visibility of callable
	}
	```
	可以看到，Runnable注入会被Executors.callable()函数转换为Callable类型，即FutureTask最终都是执行Callable类型的任务。该适配函数的实现如下 ：
	```
	public static <T> Callable<T> callable(Runnable task, T result) {
	    if (task == null)
	        throw new NullPointerException();
	    return new RunnableAdapter<T>(task, result);
	}

RunnableAdapter适配器

	/**
	 * A callable that runs given task and returns given result
	 */
	static final class RunnableAdapter<T> implements Callable<T> {
	    final Runnable task;
	    final T result;
	    RunnableAdapter(Runnable task, T result) {
	        this.task = task;
	        this.result = result;
	    }
	    public T call() {
	        task.run();
	        return result;
	    }
	}

由于FutureTask实现了Runnable，因此它既可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行。并且还可以直接通过get()函数获取执行结果，该函数会阻塞，直到结果返回。因此FutureTask既是Future、
Runnable，又是包装了Callable(如果是Runnable最终也会被转换为Callable )， 它是这两者的合体。

简单示例

	package com.effective.java.concurrent.task;
	
	import java.util.concurrent.Callable;
	import java.util.concurrent.ExecutionException;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	import java.util.concurrent.Future;
	import java.util.concurrent.FutureTask;
	
	/**
	*
	* @author mrsimple
	*
	*/
	public class RunnableFutureTask {
	
	/**
	* ExecutorService
	*/
	static ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	
	/**
	*
	* @param args
	*/
	public static void main(String[] args) {
	   runnableDemo();
	   futureDemo();
	}
	
	/**
	* runnable, 无返回值
	*/
	static void runnableDemo() {
	
	   new Thread(new Runnable() {
	
	       @Override
	       public void run() {
	           System.out.println("runnable demo : " + fibc(20));
	       }
	   }).start();
	}
	
	/**
	* 其中Runnable实现的是void run()方法，无返回值；Callable实现的是 V
	* call()方法，并且可以返回执行结果。其中Runnable可以提交给Thread来包装下
	* ，直接启动一个线程来执行，而Callable则一般都是提交给ExecuteService来执行。
	*/
	static void futureDemo() {
	   try {
	       /**
	        * 提交runnable则没有返回值, future没有数据
	        */
	       Future<?> result = mExecutor.submit(new Runnable() {
	
	           @Override
	           public void run() {
	               fibc(20);
	           }
	       });
	
	       System.out.println("future result from runnable : " + result.get());
	
	       /**
	        * 提交Callable, 有返回值, future中能够获取返回值
	        */
	       Future<Integer> result2 = mExecutor.submit(new Callable<Integer>() {
	           @Override
	           public Integer call() throws Exception {
	               return fibc(20);
	           }
	       });
	
	       System.out
	               .println("future result from callable : " + result2.get());
	
	       /**
	        * FutureTask则是一个RunnableFuture<V>，即实现了Runnbale又实现了Futrue<V>这两个接口，
	        * 另外它还可以包装Runnable(实际上会转换为Callable)和Callable
	        * <V>，所以一般来讲是一个符合体了，它可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行
	        * ，并且还可以通过v get()返回执行结果，在线程体没有执行完成的时候，主线程一直阻塞等待，执行完则直接返回结果。
	        */
	       FutureTask<Integer> futureTask = new FutureTask<Integer>(
	               new Callable<Integer>() {
	                   @Override
	                   public Integer call() throws Exception {
	                       return fibc(20);
	                   }
	               });
	       // 提交futureTask
	       mExecutor.submit(futureTask) ;
	       System.out.println("future result from futureTask : "
	               + futureTask.get());
	
	   } catch (InterruptedException e) {
	       e.printStackTrace();
	   } catch (ExecutionException e) {
	       e.printStackTrace();
	   }
	}
	
	/**
	* 效率底下的斐波那契数列, 耗时的操作
	*
	* @param num
	* @return
	*/
	static int fibc(int num) {
	   if (num == 0) {
	       return 0;
	   }
	   if (num == 1) {
	       return 1;
	   }
	   return fibc(num - 1) + fibc(num - 2);
	}
	
	}

## JDK中默认提供了哪些线程池，有何区别
### 线程池
好的软件设计不建议手动创建和销毁线程。线程的创建和销毁是非常耗 CPU 和内存的，因为这需要 JVM 和操作系统的参与。64位 JVM 默认线程栈是大小1 MB。这就是为什么说在请求频繁时为每个小的请求创建线程是一种资源的浪费。线程池可以根据创建时选择的策略自动处理线程的生命周期。重点在于：在资源（如内存、CPU）充足的情况下，线程池没有明显的优势，否则没有线程池将导致服务器奔溃。有很多的理由可以解释为什么没有更多的资源。例如，在拒绝服务（denial-of-service）攻击时会引起的许多线程并行执行，从而导致线程饥饿（thread starvation）。除此之外，手动执行线程时，可能会因为异常导致线程死亡，程序员必须记得处理这种异常情况。
合理利用线程池能够带来三个好处。第一：降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。第二：提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行。第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。

#### EXECUTOR任务提交接口与EXECUTORS工具类
Executor框架同java.util.concurrent.Executor 接口在Java 5中被引入。Executor框架是一个根据一组执行策略调用，调度，执行和控制的异步任务的框架。Executor存在的目的是提供一种将”任务提交”与”任务如何运行”分离开来的机制。定义如下：

	public interface Executor {
	    void execute(Runnable command);
	}

虽然只有一个方法，但是却为灵活且强大的异步任务执行框架提供了基础。它提供了一种标准的方法将任务的提交过程与执行过程解耦开来，并用Runnable来表示任务。那么我们怎么得到Executor对象呢？这就是接下来要介绍的Exectors了。
Executors为Executor，ExecutorService，ScheduledExecutorService，ThreadFactory和Callable类提供了一些工具方法，类似于集合中的Collections类的功能。Executors方便的创建线程池。

##### newCachedThreadPool ：该线程池比较适合没有固定大小并且比较快速就能完成的小任务，它将为每个任务创建一个线程。那这样子它与直接创建线程对象（new Thread()）有什么区别呢？看到它的第三个参数60L和第四个参数TimeUnit.SECONDS了吗？好处就在于60秒内能够重用已创建的线程。下面是Executors中的newCachedThreadPool()的源代码：　　

	public static ExecutorService newCachedThreadPool() {
	        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	    }

##### newFixedThreadPool使用的Thread对象的数量是有限的,如果提交的任务数量大于限制的最大线程数，那么这些任务讲排队，然后当有一个线程的任务结束之后，将会根据调度策略继续等待执行下一个任务。下面是Executors中的newFixedThreadPool()的源代码：　　

	public static ExecutorService newFixedThreadPool(int nThreads) {
	      return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	  }

##### newSingleThreadExecutor就是线程数量为1的FixedThreadPool,如果提交了多个任务，那么这些任务将会排队，每个任务都会在下一个任务开始之前运行结束，所有的任务将会使用相同的线程。下面是Executors中的newSingleThreadExecutor()的源代码：　　

	public static ExecutorService newSingleThreadExecutor() {
	        return new FinalizableDelegatedExecutorService
	            (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
	    }

##### newScheduledThreadPool创建一个固定长度的线程池，而且以延迟或定时的方式来执行任务。
通过如上配置的线程池的创建方法源代码，我们可以发现：
* 除了CachedThreadPool使用的是直接提交策略的缓冲队列以外，其余两个用的采用的都是无界缓冲队列，也就说，FixedThreadPool和SingleThreadExecutor创建的线程数量就不会超过 corePoolSize。
* 我们可以再来看看三个线程池采用的ThreadPoolExecutor构造方法都是同一个，使用的都是默认的ThreadFactory和handler:

	private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
	
	 public ThreadPoolExecutor(int corePoolSize,
	                     int maximumPoolSize,
	                     long keepAliveTime,
	                     TimeUnit unit,
	                     BlockingQueue<Runnable> workQueue) {
	    this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
	        Executors.defaultThreadFactory(), defaultHandler);
	}

也就说三个线程池创建的线程对象都是同组，优先权等级为正常的Thread.NORM_PRIORITY（5）的非守护线程，使用的被拒绝任务处理方式是直接抛出异常的AbortPolicy策略（前面有介绍）。
#### ExecutorService任务周期管理接口
Executor的实现通常都会创建线程来执行任务，但是使用异步方式来执行任务时，由于之前提交任务的状态不是立即可见的，所以如果要关闭应用程序时，就需要将受影响的任务状态反馈给应用程序。
为了解决执行服务的生命周期问题，Executor扩展了EecutorService接口，添加了一些用于生命周期管理的方法。如下：

	public interface ExecutorService extends Executor {
	    void shutdown();
	    List<Runnable> shutdownNow();
	    boolean isShutdown();
	    boolean isTerminated();
	    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
	    // 省略部分方法
	}

### ThreadPoolExecutor线程池实现类
先来看一下这个类中定义的重要变量，如下：

	private final BlockingQueue<Runnable> workQueue;              // 阻塞队列
	private final ReentrantLock mainLock = new ReentrantLock();   // 互斥锁
	private final HashSet<Worker> workers = new HashSet<Worker>();// 线程集合.一个Worker对应一个线程
	private final Condition termination = mainLock.newCondition();// 终止条件
	private int largestPoolSize;           // 线程池中线程数量曾经达到过的最大值。
	private long completedTaskCount;       // 已完成任务数量
	private volatile ThreadFactory threadFactory;     // ThreadFactory对象，用于创建线程。
	private volatile RejectedExecutionHandler handler;// 拒绝策略的处理句柄
	private volatile long keepAliveTime;   // 线程池维护线程所允许的空闲时间
	private volatile boolean allowCoreThreadTimeOut;
	private volatile int corePoolSize;     // 线程池维护线程的最小数量，哪怕是空闲的
	private volatile int maximumPoolSize;  // 线程池维护的最大线程数量

## 线程同步有几种方式，分别阐述在项目中的用法
### 为何要使用同步？
java允许多线程并发控制，当多个线程同时操作一个可共享的资源变量时（如数据的增删改查），将会导致数据不准确，相互之间产生冲突，因此加入同步锁以避免在该线程没有完成操作之前，被其他线程的调用，从而保证了该变量的唯一性和准确性。

#### 同步方法
即有synchronized关键字修饰的方法。
由于java的每个对象都有一个内置锁，当用此关键字修饰方法时，
内置锁会保护整个方法。在调用该方法前，需要获得内置锁，否则就处于阻塞状态。
代码如：

	public synchronized void save(){}

注： synchronized关键字也可以修饰静态方法，此时如果调用该静态方法，将会锁住整个类
#### 同步代码块
即有synchronized关键字修饰的语句块。
被该关键字修饰的语句块会自动被加上内置锁，从而实现同步
代码如：

	synchronized(object){
	}

注：同步是一种高开销的操作，因此应该尽量减少同步的内容。
通常没有必要同步整个方法，使用synchronized代码块同步关键代码即可。
#### 使用特殊域变量(volatile)实现线程同步
* volatile关键字为域变量的访问提供了一种免锁机制，
* 使用volatile修饰域相当于告诉虚拟机该域可能会被其他线程更新，
* 因此每次使用该域就要重新计算，而不是使用寄存器中的值
* volatile不会提供任何原子操作，它也不能用来修饰final类型的变量
注：多线程中的非同步问题主要出现在对域的读写上，如果让域自身避免这个问题，则就不需要修改操作该域的方法。
用final域，有锁保护的域和volatile域可以避免非同步的问题。
#### 使用重入锁实现线程同步
在JavaSE5.0中新增了一个java.util.concurrent包来支持同步。ReentrantLock类是可重入、互斥、实现了Lock接口的锁， 它与使用synchronized方法和快具有相同的基本行为和语义，并且扩展了其能力ReenreantLock类的常用方法有：ReentrantLock() : 创建一个ReentrantLock实例
* lock() : 获得锁
* unlock() : 释放锁
注：ReentrantLock()还有一个可以创建公平锁的构造方法，但由于能大幅度降低程序运行效率，不推荐使用
注：关于Lock对象和synchronized关键字的选择：
* 最好两个都不用，使用一种java.util.concurrent包提供的机制， 能够帮助用户处理所有与锁相关的代码。
* 如果synchronized关键字能满足用户的需求，就用synchronized，因为它能简化代码
* 如果需要更高级的功能，就用ReentrantLock类，此时要注意及时释放锁，否则会出现死锁，通常在finally代码释放锁

#### 使用局部变量实现线程同步
如果使用ThreadLocal管理变量，则每一个使用该变量的线程都获得该变量的副本， 副本之间相互独立，这样每一个线程都可以随意修改自己的变量副本，而不会对其他线程产生影响。
ThreadLocal 类的常用方法
ThreadLocal() : 创建一个线程本地变量
get() : 返回此线程局部变量的当前线程副本中的值
initialValue() : 返回此线程局部变量的当前线程的”初始值”
set(T value) : 将此线程局部变量的当前线程副本中的值设置为value

### Volatile与Synchronized
对于volatile修饰的变量，当一条线程修改了这个变量的值，新值对于其他线程来说是可以立即得知的
volatile变量对所有线程是立即可见的，对volatile变量的所有写操作都能立刻反应到其他线程之中
JVM规范规定了任何一个线程修改了volatile变量的值都需要立即将新值更新到主内存中, 任何线程任何时候使用到volatile变量时都需要重新获取主内存的变量值

#### 两者区别
* volatile本质是在告诉jvm当前变量在寄存器（工作内存）中的值是不确定的，需要从主存中读取；synchronized则是锁定当前变量，只有当前线程可以访问该变量，其他线程被阻塞住。
* volatile仅能使用在变量级别；synchronized则可以使用在变量、方法、和类级别的
* volatile仅能实现变量的修改可见性，不能保证原子性；而synchronized则可以保证变量的修改可见性和原子性
* volatile不会造成线程的阻塞；synchronized可能会造成线程的阻塞。
* volatile标记的变量不会被编译器优化（禁止指令重排序优化，即执行顺序与程序顺序一致）；synchronized标记的变量可以被编译器优化
## 在理解默认线程池的前提下，自己实现线程池
