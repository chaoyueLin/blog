
## 预处理

 #include”其实是非常“弱”的，不做什么检查，就是“死脑筋”把数据合并进源文件

	static uint32_t  calc_table[] = {
	#  include "calc_values.inc"        // 非常大的一个数组，细节被隐藏
	};

可以让 GCC 使用“-E”选项，略过后面的编译链接，只输出预处理后的源码

	g++ test03.cpp -E -o a.cxx    #输出预处理后的源码

写头文件的时候，为了防止代码被重复包含，通常要加上“Include Guard”，也就是用“#ifndef/#define/#endif”来保护整个头文件

	#ifndef _XXX_H_INCLUDED_
	#define _XXX_H_INCLUDED_
	
	...    // 头文件内容
	
	#endif // _XXX_H_INCLUDED_

宏定义前先检查，如果之前有定义就先 undef，然后再重新定义

	#ifdef AUTH_PWD                  // 检查是否已经有宏定义
	#  undef AUTH_PWD                // 取消宏定义
	#endif                           // 宏定义检查结束
	#define AUTH_PWD "xxx"           // 重新宏定义

_cplusplus”，它标记了 C++ 语言的版本号，使用它能够判断当前是 C 还是 C++，是 C++98 还是 C++11。
	
	#ifdef __cplusplus                      // 定义了这个宏就是在用C++编译
	    extern "C" {                        // 函数按照C的方式去处理
	#endif
	    void a_c_function(int a);
	#ifdef __cplusplus                      // 检查是否是C++编译
	    }                                   // extern "C" 结束
	#endif
	
	#if __cplusplus >= 201402                // 检查C++标准的版本号
	    cout << "c++14 or later" << endl;    // 201402就是C++14
	#elif __cplusplus >= 201103              // 检查C++标准的版本号
	    cout << "c++11 or before" << endl;   // 201103是C++11
	#else   // __cplusplus < 201103          // 199711是C++98
	#   error "c++ is too old"               // 太低则预处理报错
	#endif  // __cplusplus >= 201402         // 预处理语句结束

使用“#if 1”“#if 0”来显式启用或者禁用大段代码，要比“/* … */”的注释方式安全得多，也清楚得多


	#if 0          // 0即禁用下面的代码，1则是启用
	  ...          // 任意的代码
	#endif         // 预处理结束
	
	#if 1          // 1启用代码，用来强调下面代码的必要性
	  ...          // 任意的代码
	#endif         // 预处理结束


## 编译
“属性”没有新增关键字，而是用两对方括号的形式“[[…]]”，方括号的中间就是属性标签，在 C++11 里只定义了两个属性：“noreturn”和“carries_dependency”，C++14 的情况略微好了点，增加了一个比较实用的属性“deprecated”，用来标记不推荐使用的变量、函数或者类，也就是被“废弃”。

	[[noreturn]]              // 属性标签
	int func(bool flag)       // 函数绝不会返回任何值
	{
	    throw std::runtime_error("XXX");
	}


	[[deprecated("deadline:2020-12-31")]]      // C++14 or later
	int old_func();

好在“属性”也支持非标准扩展，允许以类似名字空间的方式使用编译器自己的一些“非官方”属性，比如，GCC 的属性都在“gnu::”里。

* deprecated：与 C++14 相同，但可以用在 C++11 里。
* unused：用于变量、类型、函数等，表示虽然暂时不用，但最好保留着，因为将来可能会用。
* constructor：函数会在 main() 函数之前执行，效果有点像是全局对象的构造函数。
* destructor：函数会在 main() 函数结束之后执行，有点像是全局对象的析构函数。
* always_inline：要求编译器强制内联函数，作用比 inline 关键字更强。
* hot：标记“热点”函数，要求编译器更积极地优化。

### 断言
assert 虽然是一个宏，但在预处理阶段不生效，而是在运行阶段才起作用，所以又叫“动态断言”。有了“动态断言”

那么相应的也就有“静态断言”，名字也很像，叫“static_assert”，不过它是一个专门的关键字，而不是宏。因为它只在编译时生效，运行阶段看不见，所以是“静态”的。

## 异常
总结了几个应当使用异常的判断准则：

* 不允许被忽略的错误；
* 极少数情况下才会发生的错误；
* 严重影响正常流程，很难恢复到正常状态的错误；
* 无法本地处理，必须“穿透”调用栈，传递到上层才能被处理的错误。

规则听起来可能有点不好理解，我给你举几个例子。比如说构造函数，如果内部初始化失败，无法创建，那后面的逻辑也就进行不下去了，所以这里就可以用异常来处理。

再比如，读写文件，通常文件系统很少会出错，总会成功，如果用错误码来处理不存在、权限错误等，就显得太啰嗦，这时也应该使用异常。

相反的例子就是 socket 通信。因为网络链路的不稳定因素太多，收发数据失败简直是“家常便饭”。虽然出错的后果很严重，但它出现的频率太高了，使用异常会增加很多的处理成本，为了性能考虑，还是检查错误码重试比较好。

建议自定义异常处理

	class my_exception : public std::runtime_error
	{
	public:
	    using this_type     = my_exception;        // 给自己起个别名
	    using super_type    = std::runtime_error;  // 给父类也起个别名
	public:
	    my_exception(const char* msg):            // 构造函数
	        super_type(msg)                      // 别名也可以用于构造
	    {}  
	
	    my_exception() = default;                // 默认构造函数
	   ~my_exception() = default;                // 默认析构函数
	private:
	    int code = 0;                            // 其他的内部私有数据
	};


	[[noreturn]]                      // 属性标签
	void raise(const char* msg)      // 函数封装throw，没有返回值
	{
	    throw my_exception(msg);     // 抛出异常，也可以有更多的逻辑
	}


### function-try

	void f(int i)
	try
	{
	   if ( i  < 0 )
	      throw"less than zero";
	   std::cout <<"greater than zero" << std::endl;
	}
	catch(const char* e)
	{
	    std::cout << e << std::endl;
	}
	
	int main() {
	        f(1);
	        f(-1);
	        return 0;
	}

## 处理文本
string 其实并不是一个“真正的类型”，而是模板类 basic_string 的特化形式，是一个 typedef：

	using string = std::basic_string<char>;  // string其实是一个类型别名
把每个字符串都看作是一个不可变的实体，你才能在 C++ 里真正地用好字符串。需要存储字符的容器，比如字节序列、数据缓冲区，建议最好改用vector<char>

### 字面量后缀
C++14 为方便使用字符串，新增了一个字面量的后缀“s”，明确地表示它是 string 字符串类型，而不是 C 字符串，这就可以利用 auto 来自动类型推导，而且在其他用到字符串的地方，也可以省去声明临时字符串变量的麻烦，效率也会更高


	using namespace std::literals::string_literals;  //必须打开名字空间
	
	auto str = "std string"s;      // 后缀s，表示是标准字符串，直接类型推导
	
	assert("time"s.size() == 4);   // 标准字符串可以直接调用成员函数

### 原始字符串
C++11 还为字面量增加了一个“原始字符串”（Raw string literal）的新表示形式，比原来的引号多了一个大写字母 R 和一对圆括号

	auto str = R"(nier:automata)";    // 原始字符串：nier:automata


## 多线程
### once_flag
要先声明一个 once_flag 类型的变量，最好是静态、全局的（线程可见），作为初始化的标志：

	static std::once_flag flag;        // 全局的初始化标志

然后调用专门的 call_once() 函数，以函数式编程的方式，传递这个标志和初始化函数。这样 C++ 就会保证，即使多个线程重入 call_once()，也只能有一个线程会成功运行初始化。

	void case1()
	{
	    static once_flag flag;
	
	    auto f = []()
	    {
	        cout << "tid=" <<
	            this_thread::get_id() << endl;
	
	        std::call_once(flag,
	            [](){
	                cout << "only once" << endl;
	            }
	        );
	    };
	
	    thread t1(f);
	    thread t2(f);
	
	    t1.join();
	    t2.join();
	}

单例模式，使用once_flag

	class Singleton{
	private:
	    static Singleton* _ptr;
	    static once_flag _flag;
	    Singleton(){}
	    Singleton(const Singleton&) = delete;
	public:
	    static Singleton* getInstance(){
	        call_once(_flag, [](){
	            _ptr = new Singleton();
	        });
	        return _ptr;
	    }
	};
	Singleton* Singleton::_ptr = nullptr;
	once_flag _flag;

使用静态变量

	class Singleton{
	private:
	    Singleton(){}
	    Singleton(const Singleton&) = delete;
	public:
	    static Singleton* getInstance(){
	        static Singleton instance;
	        return &instance;
	    }
	};

### thread_local

	void case2()
	{
	    //static int n = 0;
	    thread_local int n = 0;
	
	    auto f = [&](int x)
	    {
	        n += x;
	
	        cout << n;
	        cout << ", tid=" <<
	            this_thread::get_id() << endl;
	    };
	
	    thread t1(f, 10);
	    thread t2(f, 20);
	
	    t1.join();
	    t2.join();
	}

### 原子变量
C++ 只能让一些最基本的类型原子化，比如 atomic_int、atomic_long

	using atomic_bool = std::atomic<bool>;    // 原子化的bool
	using atomic_int  = std::atomic<int>;      // 原子化的int
	using atomic_long = std::atomic<long>;    // 原子化的long

除了模拟整数运算，原子变量还有一些特殊的原子操作，比如 store、load、fetch_add、fetch_sub、exchange、compare_exchange_weak/compare_exchange_strong，最后一组就是著名的 CAS（Compare And Swap）操作。而另一个同样著名的 TAS（Test And Set）操作，则需要用到一个特殊的原子类型 atomic_flag。它不是简单的 bool 特化（atomic），没有 store、load 的操作，只用来实现 TAS，保证绝对无锁。

	void case3()
	{
	    atomic_int  x {0};
	    atomic_long y {1000L};
	
	    assert(++x == 1);
	    y += 200;
	    assert(y < 2000);
	
	    static atomic_flag flag {false};
	    static atomic_int  n;
	
	    auto f = [&]()
	    {
	        auto value = flag.test_and_set();
	
	        if (value) {
	            cout << "flag has been set." << endl;
	        } else {
	            cout << "set flag by " <<
	                this_thread::get_id() << endl;
	        }
	
	        n += 100;
	
	        //using namespace std::chrono_literals;
	
	        this_thread::sleep_for(n.load() * 10ms);
	        cout << n << endl;
	
	    };
	
	    thread t1(f);
	    thread t2(f);
	
	    t1.join();
	    t2.join();
	}

### async

有一个很隐蔽的“坑”，如果你不显式获取 async() 的返回值（即 future 对象），它就会同步阻塞直至任务完成（由于临时对象的析构函数），于是“async”就变成了“sync”。所以，即使我们不关心返回值，也总要用 auto 来配合 async()，避免同步阻塞

	std::async(task, ...);            // 没有显式获取future，被同步阻塞
	auto f = std::async(task, ...);   // 只有上一个任务完成后才能被执行


## 混合系统
### pybind11
### LuaJIT
### LuaBridge