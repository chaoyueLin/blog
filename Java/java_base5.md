## JAVA注解
### 注解的使用
定义：注解（Annotation），也叫元数据。一种代码级别的说明。它是JDK1.5及以后版本引入的一个特性，与类、接口、枚举是在同一个层次。它可以声明在包、类、字段、方法、局部变量、方法参数等的前面，用来对这些元素进行说明，注释。
作用分类：
编写文档：通过代码里标识的元数据生成文档【生成文档doc文档】
代码分析：通过代码里标识的元数据对代码进行分析【使用反射】
编译检查：通过代码里标识的元数据让编译器能够实现基本的编译检查【Override】
Annotation(注解)是JDK1.5及以后版本引入的。它可以用于创建文档，跟踪代码中的依赖性，甚至执行基本编译时检查。注解是以‘@注解名’在代码中存在的，根据注解参数的个数，我们可以将注解分为：标记注解、单值注解、完整注解三类。它们都不会直接影响到程序的语义，只是作为注解（标识）存在，我们可以通过反射机制编程实现对这些元数据（用来描述数据的数据）的访问。另外，你可以在编译时选择代码里的注解是否只存在于源代码级，或者它也能在class文件、或者运行时中出现（SOURCE/CLASS/RUNTIME）。
#### @OVERRIDE
它的作用是对覆盖超类中方法的方法进行标记，如果被标记的方法并没有实际覆盖超类中的方法，则编译器会发出错误警告。


	/** * 测试Override注解 * @author Administrator */ 
	public class OverrideDemoTest { 
	//@Override public String tostring() { return "测试注解"; }
	 }

#### @DEPRECATED
它的作用是对不应该再使用的方法添加注解，当编程人员使用这些方法时，将会在编译时显示提示信息，它与javadoc里的@deprecated标记有相同的功能，准确的说，它还不如javadoc @deprecated，因为它不支持参数，使用@Deprecated的示例代码示例如：

	  /** * 测试Deprecated注解 * @author Administrator */ 
	public class DeprecatedDemoTest { 
	    public static void main(String[]args) { 
	        // 使用DeprecatedClass里声明被过时的方法                 
	        DeprecatedClass.DeprecatedMethod();
	 } }
	 class DeprecatedClass { 
	@Deprecated public static void DeprecatedMethod() { }
	 }

####　@SuppressWarnings
其参数有：deprecation，使用了过时的类或方法时的警告 unchecked，执行了未检查的转换时的警告 fallthrough，当 switch 程序块直接通往下一种情况而没有 break 时的警告 path，在类路径、源文件路径等中有不存在的路径时的警告 serial，当在可序列化的类上缺少serialVersionUID 定义时的警告 finally ，任何 finally 子句不能正常完成时的警告 all，关于以上所有情况的警告
## 注解的级别及意义
## 如何自定义注解