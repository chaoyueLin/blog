# STL
## Vector
vector 会试图把元素“移动”到新的内存区域。vector 通常保证强异常安全性，如果元素类型没有提供一个保证不抛异常的移动构造函数，vector 通常会使用拷贝构造函数。因此，对于拷贝代价较高的自定义元素类型，我们应当定义移动构造函数，并标其为 noexcept，或只在容器中放置对象的智能指针。


	#include <iostream>
	#include <vector>
	
	using namespace std;
	
	class Obj1 {
	public:
	  Obj1()
	  {
	    cout << "Obj1()\n";
	  }
	  Obj1(const Obj1&)
	  {
	    cout << "Obj1(const Obj1&)\n";
	  }
	  Obj1(Obj1&&)
	  {
	    cout << "Obj1(Obj1&&)\n";
	  }
	};
	
	class Obj2 {
	public:
	  Obj2()
	  {
	    cout << "Obj2()\n";
	  }
	  Obj2(const Obj2&)
	  {
	    cout << "Obj2(const Obj2&)\n";
	  }
	  Obj2(Obj2&&) noexcept
	  {
	    cout << "Obj2(Obj2&&)\n";
	  }
	};
	
	int main()
	{
	  vector<Obj1> v1;
	  v1.reserve(2);
	  v1.emplace_back();
	  v1.emplace_back();
	  v1.emplace_back();
	
	  vector<Obj2> v2;
	  v2.reserve(2);
	  v2.emplace_back();
	  v2.emplace_back();
	  v2.emplace_back();
	}

假如要把vector里的两个对象移到一个新的vector，移第一个成功，第二个时有异常，然后vector该怎么办？现在两个vector都废掉了。所以移动需要异常安全。

拷贝不影响旧的容器。即使发生异常，至少老的那个还是好的。这就是异常安全。

### resize和reverse区别

* reserve()只修改capacity大小，不修改size大小，
* resize()既修改capacity大小，也修改size大小。

### 遍历并删除
	
	  vector<int>::iterator it = vec.begin();
	  for (; it != vec.end();) {
	    it = vec.erase(it);
	  }


## list
因为某些标准算法在 list 上会导致问题，list 提供了成员函数作为替代，包括下面几个：
* merge
* remove
* remove_if
* reverse
* sort
* unique


	#include "output_container.h"
	#include <iostream>
	#include <algorithm>
	#include <list>
	#include <vector>
	using namespace std;
	
	int main()
	{
	  list<int> lst{1, 7, 2, 8, 3};
	  vector<int> vec{1, 7, 2, 8, 3};
	
	  sort(vec.begin(), vec.end());    // 正常
	  // sort(lst.begin(), lst.end()); // 会出错
	  lst.sort();                      // 正常
	
	  cout << lst << endl;
	  // 输出 { 1, 2, 3, 7, 8 }
	
	  cout << vec << endl;
	  // 输出 { 1, 2, 3, 7, 8 }
	}