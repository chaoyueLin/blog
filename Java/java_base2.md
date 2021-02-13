# Java基础要点2
# [Java基础要点1](./README.md)
## JAVA 集合类
### JAVA常用集合类功能、区别和性能
两大类：Collections,Map;Collections分List和Set

List接口与其实现类

List类似于数组，可以通过索引来访问元素，实现该接口的常用类有ArrayList、LinkedList、Vector、Stack等。

#### ARRAYLIST
ArrayList是动态数组，可以根据插入的元素的数量自动扩容，而使用者不需要知道其内部是什么时候进行扩展的，把它当作足够容量的数组来使用即可。
ArrayList访问元素的方法get是常数时间，因为是直接根据下标索引来访问的，而add方法的时间复杂度是O(n)，因为需要移动元素，将新元素插入到合适的位置。
ArrayList是非线程安全的，即它没有同步，不过，可以通过Collections.synchronizedList()静态方法返回一个同步的实例，如：
```
List synList = Collections.synchronizedList(list);
```
数组扩容：ArrayList在插入元素的时候，都会检查当前的数组大小是否足够，如果不够，将会扩容到当前容量 * 1.5 + 1（加1是为了当前容量为1时，也能扩展到2），即把原来的元素全部复制到一个两倍大小的新数组，将旧的数组抛弃掉(等待垃圾回收)，这个操作是比较耗时，因此建议在创建ArrayList的时候，根据要插入的元素的数量来初步估计Capacity，并初始化ArrayList，如：
```
ArrayList list = new ArrayList(100);
```
这样，在插入小于100个元素的时候都是不需要进行扩容的，能够带来性能的提升，当然，如果对这个容量估计大了，可能会带来一些空间的损耗。

#### LINKEDLIST
LinkedList也实现了List接口，其内部实现是使用双向链表来保存元素，因此插入与删除元素的性能都表现不错。它还提供了一些其它操作方法，如在头部、尾部插入或者删除元素，因此，可以用它来实现栈、队列、双向队列。
由于是使用链表保存元素的，所以随机访问元素的时候速度会比较慢(需要遍历链表找到目标元素)，这一点相比ArrayList的随机访问要差，ArrayList是采用数组实现方式，直接使用下标可以访问到元素而不需要遍历。因此，在需要频繁随机访问元素的情况下，建议使用ArrayList。
与ArrayList一样，LinkedList也是非同步的，如果需要实现多线程访问，则需要自己在外部实现同步方法。当然也可以使用Collections.synchronizedList()静态方法。

#### VECTOR
Vector是ArrayList的线程同步版本，即是说Vector是同步的，支持多线程访问。除此之外，还有一点不同时，当容量不够时，Vector默认扩展一倍容量，而ArrayList是当前容量 * 1.5 + 1

#### STACK
Stack是一种后进先出的数据结构，继承自Vector类，提供了push、pop、peek（获得栈顶元素）等方法。

#### SET接口
Set是不能包含重合元素的容器，其实现类有HashSet，继承于它的接口有SortedSet接口等。Set中提供了加、减、和交等集合操作函数。Set不能按照索引随机访问元素，这是它与List的一个重要区别。

#### HASHSET
HashSet实现了Set接口，其内部是采用HashMap实现的。放入HashSet的对象最好重写hashCode、equals方法，因为默认的这两个方法很可能与你的业务逻辑是不一致的，而且，要同时重写这两个函数，如果只重写其中一个，很容易发生意想不到的问题。
记住下面几条规则：

相等对象，hashCode一定相等。
不等对象，hashCode不一定不相等。
两个对象的hashCode相同，不一定相等。
两个对象的hashCode不同，一定相等。

#### TREESET
TreeSet同样的Set接口的实现类，同样不能存放相同的对象。它与HashSet不同的是，TreeSet的元素是按照顺序排列的，因此用TreeSet存放的对象需要实现Comparable接口。

#### MAP接口
Map集合提供了按照“键值对”存储元素的方法，一个键唯一映射一个值。集合中“键值对”整体作为一个实体元素时，类似List集合，但是如果分开来年，Map是一个两列元素的集合：键是一列，值是一列。与Set集合一样，Map也没有提供随机访问的能力，只能通过键来访问对应的值。
Map的每一个元素都是一个Map.Entry，这个实体的结构是< Key, Value >样式。

#### HASHMAP
HashMap实现了Map接口，但它是非线程安全的。HashMap允许key值为null，value也可以为null。

#### HASHTABLE
Hashtable也是Map的实现类，继承自Dictionary类。它与HashMap不同的是，它是线程安全的。而且它不允许key为null，value也不能为null。
由于它是线程安全的，在效率上稍差于HashMap。

#### LIST总结
ArrayList内部实现采用动态数组，当容量不够时，自动扩容至（当前容量*1.5+1）。元素的顺序按照插入的顺序排列。默认初始容量为10。
contains复杂度为O(n)，add复杂度为分摊的常数，即添加n个元素需要O(n)时间，remove为O(n)，get复杂度为O(1)
随机访问效率高，随机插入、删除效率低。ArrayList是非线程安全的。

LinkedList内部使用双向链表实现，随机访问效率低，随机插入、删除效率高。可以当作堆栈、队列、双向队列来使用。LinkedList也是非线程安全的。

Vector跟ArrayList是类似的，内部实现也是动态数组，随机访问效率高。Vector是线程安全的。

Stack是栈，继承于Vector，其各种操作也是基于Vector的各种操作，因此其内部实现也是动态数组，先进后出。Stack是线程安全的。

#### LIST使用场景
对于需要快速插入、删除元素，应该使用LinkedList
对于需要快速随机访问元素，应该使用ArrayList
如果List需要被多线程操作，应该使用Vector，如果只会被单线程操作，应该使用ArrayList
#### Set总结

HashSet内部是使用HashMap实现的，HashSet的key值是不允许重复的，如果放入的对象是自定义对象，那么最好能够同时重写hashCode与equals函数，这样就能自定义添加的对象在什么样的情况下是一样的，即能保证在业务逻辑下能添加对象到HashSet中，保证业务逻辑的正确性。另外，HashSet里的元素不是按照顺序存储的。HashSet是非线程安全的。

TreeSet存储的元素是按顺序存储的，如果是存储的元素是自定义对象，那么需要实现Comparable接口。TreeSet也是非线程安全的。

LinkedHashSet继承自HashSet，它与HashSet不同的是，LinkedHashSet存储元素的顺序是按照元素的插入顺序存储的。LinkedHashSet也是非线程安全的。

#### MAP总结
HashMap存储键值对。当程序试图将一个key-value对放入 HashMap 中时，程序首先根据该key的hashCode()返回值决定该Entry的存储位置：如果两个Entry的key的hashCode() 返回值相同，那它们的存储位置相同。如果这两个Entry的key通过equals比较返回true，新添加Entry的value将覆盖集合中原有Entry的 value，但key不会覆盖。如果这两个Entry的key通过equals 比较返回false，新添加的Entry将与集合中原有Entry形成Entry 链，而且新添加的 Entry 位于 Entry 链的头部。看下面HashMap添加键值对的源代码：
```
public V put(K key, V value) {
   if (key == null)
       return putForNullKey(value);
   int hash = hash(key.hashCode());
   int i = indexFor(hash, table.length);
   for (Entry<K,V> e = table[i]; e != null; e = e.next) {
       Object k;
       if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
           V oldValue = e.value;
           e.value = value;
           e.recordAccess(this);
           return oldValue;
       }
   }
   modCount++;
   addEntry(hash, key, value, i);
   return null;
}
void addEntry(int hash, K key, V value, int bucketIndex) {
   Entry<K,V> e = table[bucketIndex];
   table[bucketIndex] = new Entry<>(hash, key, value, e);
   if (size++ >= threshold)
       resize(2 * table.length);
}
````
HashMap允许key、value值为null。HashMap是非线程安全的。

Hashtable是HashMap的线程安全版本。而且，key、value都不允许为null。

哈希值的使用不同: Hashtable直接使用对象的hashCode，如下代码：
```
int hash = key.hashCode();
int index = (hash & 0x7FFFFFFF) % tab.length;
而HashMap重新计算hash值，如下代码：
```
int hash = hash(key.hashCode());
int i = indexFor(hash, table.length);
static int hash(int h) {
   // This function ensures that hashCodes that differ only by
   // constant multiples at each bit position have a bounded
   // number of collisions (approximately 8 at default load factor).
   h ^= (h >>> 20) ^ (h >>> 12);
   return h ^ (h >>> 7) ^ (h >>> 4);
}
static int indexFor(int h, int length) {
   return h & (length-1);
}
```
扩展容量不同： Hashtable中hash数组默认大小是11，增加的方式是 old*2+1。HashMap中hash数组的默认大小是16，而且一定是2的指数。

## 并发相关的集合类
并发效能的提升是需要从底层JVM指令级别开始重新设计，优化，改进同步锁的机制才能实现的，java.util.concurrent 的目的就是要实现 Collection 框架对数据结构所执行的并发操作。通过提供一组可靠的、高性能并发构建块，开发人员可以提高并发类的线程安全、可伸缩性、性能、可读性和可靠性。JDK 5.0 中的并发改进有以下三组：

* JVM 级别更改。大多数现代处理器对并发对某一硬件级别提供支持，通常以 compare-and-swap （CAS）指令形式。CAS 是一种低级别的、细粒度的技术，它允许多个线程更新一个内存位置，同时能够检测其他线程的冲突并进行恢复。它是许多高性能并发算法的基础。在 JDK 5.0 之前，Java 语言中用于协调线程之间的访问的惟一原语是同步，同步是更重量级和粗粒度的。公开 CAS 可以开发高度可伸缩的并发 Java 类。这些更改主要由 JDK 库类使用，而不是由开发人员使用。

* 低级实用程序类 – 锁定和原子类。使用 CAS 作为并发原语，ReentrantLock 类提供与 synchronized 原语相同的锁定和内存语义，然而这样可以更好地控制锁定（如计时的锁定等待、锁定轮询和可中断的锁定等待）和提供更好的可伸缩性（竞争时的高性能）。大多数开发人员将不再直接使用 ReentrantLock 类，而是使用在 ReentrantLock 类上构建的高级类。

* 高级实用程序类。这些类实现并发构建块，每个计算机科学文本中都会讲述这些类 – 信号、互斥、闩锁、屏障、交换程序、线程池和线程安全集合类等。大部分开发人员都可以在应用程序中用这些类，来替换许多（如果不是全部）同步、wait() 和 notify() 的使用，从而提高性能、可读性和正确性。

### HASHTABLE
Hashtable 提供了一种易于使用的、线程安全的、关联的map功能，这当然也是方便的。然而，线程安全性是凭代价换来的―― Hashtable 的所有方法都是同步的。 此时，无竞争的同步会导致可观的性能代价。 Hashtable 的后继者 HashMap 是作为JDK1.2中的集合框架的一部分出现的，它通过提供一个不同步的基类和一个同步的包装器 Collections.synchronizedMap ，解决了线程安全性问题。 通过将基本的功能从线程安全性中分离开来， Collections.synchronizedMap 允许需要同步的用户可以拥有同步，而不需要同步的用户则不必为同步付出代价。
Hashtable 和 synchronizedMap 所采取的获得同步的简单方法（同步 Hashtable 中或者同步的 Map 包装器对象中的每个方法）有两个主要的不足。首先，这种方法对于可伸缩性是一种障碍，因为一次只能有一个线程可以访问hash表。 同时，这样仍不足以提供真正的线程安全性，许多公用的混合操作仍然需要额外的同步。虽然诸如 get() 和 put() 之类的简单操作可以在不需要额外同步的情况下安全地完成，但还是有一些公用的操作序列 ，例如迭代或者put-if-absent（空则放入），需要外部的同步，以避免数据争用。

#### 减小锁粒度
提高 HashMap 的并发性同时还提供线程安全性的一种方法是废除对整个表使用一个锁的方式，而采用对hash表的每个bucket都使用一个锁的方式（或者，更常见的是，使用一个锁池，每个锁负责保护几个bucket） 。这意味着多个线程可以同时地访问一个 Map 的不同部分，而不必争用单个的集合范围的锁。这种方法能够直接提高插入、检索以及移除操作的可伸缩性。不幸的是，这种并发性是以一定的代价换来的――这使得对整个 集合进行操作的一些方法（例如 size() 或 isEmpty() ）的实现更加困难，因为这些方法要求一次获得许多的锁，并且还存在返回不正确的结果的风险。然而，对于某些情况，例如实现cache，这样做是一个很好的折衷――因为检索和插入操作比较频繁，而 size() 和 isEmpty() 操作则少得多。

#### CONCURRENTHASHMAP
util.concurrent 包中的 ConcurrentHashMap 类（也将出现在JDK 1.5中的 java.util.concurrent 包中）是对 Map 的线程安全的实现，比起 synchronizedMap 来，它提供了好得多的并发性。多个读操作几乎总可以并发地执行，同时进行的读和写操作通常也能并发地执行，而同时进行的写操作仍然可以不时地并发进行（相关的类也提供了类似的多个读线程的并发性，但是，只允许有一个活动的写线程） 。ConcurrentHashMap 被设计用来优化检索操作；实际上，成功的 get() 操作完成之后通常根本不会有锁着的资源。要在不使用锁的情况下取得线程安全性需要一定的技巧性，并且需要对Java内存模型（Java Memory Model）的细节有深入的理解。

#### COPYONWRITEARRAYLIST
在那些遍历操作大大地多于插入或移除操作的并发应用程序中，一般用 CopyOnWriteArrayList 类替代 ArrayList 。如果是用于存放一个侦听器（listener）列表，例如在AWT或Swing应用程序中，或者在常见的JavaBean中，那么这种情况很常见（相关的 CopyOnWriteArraySet 使用一个 CopyOnWriteArrayList 来实现 Set 接口） 。
如果您正在使用一个普通的 ArrayList 来存放一个侦听器列表，那么只要该列表是可变的，而且可能要被多个线程访问，您 就必须要么在对其进行迭代操作期间，要么在迭代前进行的克隆操作期间，锁定整个列表，这两种做法的开销都很大。当对列表执行会引起列表发生变化的操作时， CopyOnWriteArrayList 并不是为列表创建一个全新的副本，它的迭代器肯定能够返回在迭代器被创建时列表的状态，而不会抛出 ConcurrentModificationException 。在对列表进行迭代之前不必克隆列表或者在迭代期间锁 定列表，因为迭代器所看到的列表的副本是不变的。换句话说， CopyOnWriteArrayList 含有对一个不可变数组的一个可变的引用，因此，只要保留好那个引用，您就可以获得不可变的线程安全性的好处，而且不用锁 定列表。

#### BLOCKINGQUEUE
阻塞队列（BlockingQueue）是一个支持两个附加操作的队列。这两个附加的操作是：在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。阻塞队列常用于生产者和消费者的场景，生产者是往队列里添加元素的线程，消费者是从队列里拿元素的线程。阻塞队列就是生产者存放元素的容器，而消费者也只从容器里拿元素。

阻塞队列提供了四种处理方法:

方法处理方式	抛出异常	返回特殊值	一直阻塞	超时退出
插入方法	add(e)	offer(e)	put(e)	offer(e,time,unit)
移除方法	remove()	poll()	take()	poll(time,unit)
检查方法	element()	peek()	不可用	不可用
抛出异常：是指当阻塞队列满时候，再往队列里插入元素，会抛出IllegalStateException(“Queue full”)异常。当队列为空时，从队列里获取元素时会抛出NoSuchElementException异常 。
返回特殊值：插入方法会返回是否成功，成功则返回true。移除方法，则是从队列里拿出一个元素，如果没有则返回null
一直阻塞：当阻塞队列满时，如果生产者线程往队列里put元素，队列会一直阻塞生产者线程，直到拿到数据，或者响应中断退出。当队列空时，消费者线程试图从队列里take元素，队列也会阻塞消费者线程，直到队列可用。
超时退出：当阻塞队列满时，队列会阻塞生产者线程一段时间，如果超过一定的时间，生产者线程就会退出。

##### Java里的阻塞队列
JDK7提供了7个阻塞队列。分别是

* ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。
* LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。
* PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列。
* DelayQueue：一个使用优先级队列实现的无界阻塞队列。
* SynchronousQueue：一个不存储元素的阻塞队列。
* LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
* LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。

###### ArrayBlockingQueue
ArrayBlockingQueue是一个用数组实现的有界阻塞队列。此队列按照先进先出（FIFO）的原则对元素进行排序。默认情况下不保证访问者公平的访问队列，所谓公平访问队列是指阻塞的所有生产者线程或消费者线程，当队列可用时，可以按照阻塞的先后顺序访问队列，即先阻塞的生产者线程，可以先往队列里插入元素，先阻塞的消费者线程，可以先从队列里获取元素。

###### LINKEDBLOCKINGQUEUE
LinkedBlockingQueue是一个用链表实现的有界阻塞队列。此队列的默认和最大长度为Integer.MAX_VALUE。此队列按照先进先出的原则对元素进行排序。
LinkedBlockingDeque是一个由链表结构组成的双向阻塞队列。所谓双向队列指的你可以从队列的两端插入和移出元素。双端队列因为多了一个操作队列的入口，在多线程同时入队时，也就减少了一半的竞争。相比其他的阻塞队列，LinkedBlockingDeque多了addFirst，addLast，offerFirst，offerLast，peekFirst，peekLast等方法，以First单词结尾的方法，表示插入，获取（peek）或移除双端队列的第一个元素。以Last单词结尾的方法，表示插入，获取或移除双端队列的最后一个元素。另外插入方法add等同于addLast，移除方法remove等效于removeFirst。但是take方法却等同于takeFirst，不知道是不是Jdk的bug，使用时还是用带有First和Last后缀的方法更清楚。在初始化LinkedBlockingDeque时可以初始化队列的容量，用来防止其再扩容时过渡膨胀。另外双向阻塞队列可以运用在“工作窃取”模式中。

###### PRIORITYBLOCKINGQUEUE
PriorityBlockingQueue是一个支持优先级的无界队列。默认情况下元素采取自然顺序排列，也可以通过比较器comparator来指定元素的排序规则。元素按照升序排列。

###### DelayQueue
DelayQueue是一个支持延时获取元素的无界阻塞队列。队列使用PriorityQueue来实现。队列中的元素必须实现Delayed接口，在创建元素时可以指定多久才能从队列中获取当前元素。只有在延迟期满时才能从队列中提取元素。我们可以将DelayQueue运用在以下应用场景：

缓存系统的设计：可以用DelayQueue保存缓存元素的有效期，使用一个线程循环查询DelayQueue，一旦能从DelayQueue中获取元素时，表示缓存有效期到了。
定时任务调度。使用DelayQueue保存当天将会执行的任务和执行时间，一旦从DelayQueue中获取到任务就开始执行，从比如TimerQueue就是使用DelayQueue实现的。
队列中的Delayed必须实现compareTo来指定元素的顺序。比如让延时时间最长的放在队列的末尾。实现代码如下：
```
 public int compareTo(Delayed other) {
    if (other == this) // compare zero ONLY if same object
         return 0;
     if (other instanceof ScheduledFutureTask) {
         ScheduledFutureTask x = (ScheduledFutureTask)other;
         long diff = time - x.time;
         if (diff < 0)
             return -1;
         else if (diff > 0)
             return 1;
else if (sequenceNumber < x.sequenceNumber)
             return -1;
         else
             return 1;
     }
     long d = (getDelay(TimeUnit.NANOSECONDS) -
               other.getDelay(TimeUnit.NANOSECONDS));
     return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
 }
```
如何实现Delayed接口

我们可以参考ScheduledThreadPoolExecutor里ScheduledFutureTask类。这个类实现了Delayed接口。首先：在对象创建的时候，使用time记录前对象什么时候可以使用，代码如下：
```
ScheduledFutureTask(Runnable r, V result, long ns, long period) {
    super(r, result);
    this.time = ns;
    this.period = period;
    this.sequenceNumber = sequencer.getAndIncrement();
}
```
然后使用getDelay可以查询当前元素还需要延时多久，代码如下：
```
public long getDelay(TimeUnit unit) {
             return unit.convert(time - now(), TimeUnit.NANOSECONDS);
}
```
通过构造函数可以看出延迟时间参数ns的单位是纳秒，自己设计的时候最好使用纳秒，因为getDelay时可以指定任意单位，一旦以纳秒作为单位，而延时的时间又精确不到纳秒就麻烦了。使用时请注意当time小于当前时间时，getDelay会返回负数。

如何实现延时队列

延时队列的实现很简单，当消费者从队列里获取元素时，如果元素没有达到延时时间，就阻塞当前线程。
```
long delay = first.getDelay(TimeUnit.NANOSECONDS);
            if (delay <= 0)
                return q.poll();
            else if (leader != null)
                available.await();
```
#### 阻塞队列的实现原理
如果队列是空的，消费者会一直等待，当生产者添加元素时候，消费者是如何知道当前队列有元素的呢？如果让你来设计阻塞队列你会如何设计，让生产者和消费者能够高效率的进行通讯呢？让我们先来看看JDK是如何实现的。

使用通知模式实现。所谓通知模式，就是当生产者往满的队列里添加元素时会阻塞住生产者，当消费者消费了一个队列中的元素后，会通知生产者当前队列可用。通过查看JDK源码发现ArrayBlockingQueue使用了Condition来实现，代码如下：
```
    private final Condition notFull;
    private final Condition notEmpty;

    public ArrayBlockingQueue(int capacity, boolean fair) {
    //省略其他代码
    notEmpty = lock.newCondition();
    notFull = lock.newCondition();
}

    public void put(E e) throws InterruptedException {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == items.length)
            notFull.await();
        insert(e);
    } finally {
        lock.unlock();
    }
    }

    public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == 0)
            notEmpty.await();
        return extract();
              } finally {
        lock.unlock();
    }
        }

        private void insert(E x) {
    items[putIndex] = x;
    putIndex = inc(putIndex);
    ++count;
    notEmpty.signal();
}
```
当我们往队列里插入一个元素时，如果队列不可用，阻塞生产者主要通过LockSupport.park(this);来实现
```
public final void await() throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    Node node = addConditionWaiter();
    int savedState = fullyRelease(node);
    int interruptMode = 0;
    while (!isOnSyncQueue(node)) {
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    if (interruptMode != 0)

                reportInterruptAfterWait(interruptMode);
}
```
继续进入源码，发现调用setBlocker先保存下将要阻塞的线程，然后调用unsafe.park阻塞当前线程。
```
    public static void park(Object blocker) {
    Thread t = Thread.currentThread();
    setBlocker(t, blocker);
    unsafe.park(false, 0L);
    setBlocker(t, null);
}
```
unsafe.park是个native方法，代码如下：

park这个方法会阻塞当前线程，只有以下四种情况中的一种发生时，该方法才会返回。

与park对应的unpark执行或已经执行时。注意：已经执行是指unpark先执行，然后再执行的park。
线程被中断时。
如果参数中的time不是零，等待了指定的毫秒数时。
发生异常现象时。这些异常事先无法确定。
我们继续看一下JVM是如何实现park方法的，park在不同的操作系统使用不同的方式实现，在linux下是使用的是系统方法pthread_cond_wait实现。实现代码在JVM源码路径src/os/linux/vm/os_linux.cpp里的 os::PlatformEvent::park方法，代码如下：
```
   void os::PlatformEvent::park() {
         int v ;
    for (;;) {
            v = _Event ;
    if (Atomic::cmpxchg (v-1, &_Event, v) == v) break ;
    }
    guarantee (v >= 0, "invariant") ;
    if (v == 0) {
    // Do this the hard way by blocking ...
    int status = pthread_mutex_lock(_mutex);
    assert_status(status == 0, status, "mutex_lock");
    guarantee (_nParked == 0, "invariant") ;
    ++ _nParked ;
    while (_Event < 0) {
    status = pthread_cond_wait(_cond, _mutex);
    // for some reason, under 2.7 lwp_cond_wait() may return ETIME ...
    // Treat this the same as if the wait was interrupted
    if (status == ETIME) { status = EINTR; }
    assert_status(status == 0 || status == EINTR, status, "cond_wait");
    }
    -- _nParked ;

    // In theory we could move the ST of 0 into _Event past the unlock(),
    // but then we'd need a MEMBAR after the ST.
    _Event = 0 ;
    status = pthread_mutex_unlock(_mutex);
    assert_status(status == 0, status, "mutex_unlock");
    }
    guarantee (_Event >= 0, "invariant") ;
    }

}
```
pthread_cond_wait是一个多线程的条件变量函数，cond是condition的缩写，字面意思可以理解为线程在等待一个条件发生，这个条件是一个全局变量。这个方法接收两个参数，一个共享变量_cond，一个互斥量_mutex。而unpark方法在linux下是使用pthread_cond_signal实现的。park 在windows下则是使用WaitForSingleObject实现的。

## 部分常用集合类的内部实现方式