# static

## 函数中的静态变量

当变量声明为static时，空间将在程序的生命周期内分配。

线程安全，C++ 11 之后），就是直接使用函数内部的 static 静态变量。C++ 语言会保证静态变量的初始化是线程安全的，绝对不会有线程冲突


    auto& instance()    // 生产单件对象的函数
    {
    static T obj;     // 静态变量
    return obj;       // 返回对象的引用
    }

## 静态成员变量
静态成员变量必须在cpp文件里定义实现，头文件里只是声明。

    #include<iostream> 
    using namespace std; 

    class Apple 
    { 
    public: 
        static int i; 
        
        Apple() 
        { 
            // Do nothing 
        }; 
    }; 

    int Apple::i = 1; 

    int main() 
    { 
        Apple obj; 
        // prints value of i 
        cout << obj.i; 
    } 

## 静态成员函数

    #include<iostream> 
    using namespace std; 

    class Apple 
    { 
        public: 
            
            static void printMsg() 
            {
                cout<<"Welcome to Apple!"; 
            }
    }; 

    // main function 
    int main() 
    { 
        // invoking a static member function 
        Apple::printMsg(); 
    } 


技巧：静态成员函数代替静态成员变量,只需要在声明中

    template<int thread_num = 1>        // 使用整数模板参数来指定线程数
    class ZmqContext final
    {
    public:
        static                          // 静态成员函数代替静态成员变量
        zmq_context_type& context()
        {
            static zmq_context_type ctx(thread_num);
            return ctx;
        }
    };