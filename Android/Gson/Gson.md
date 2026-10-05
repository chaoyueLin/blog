# Gson 解析原理

```mermaid
mindmap
  root((Gson 解析原理))
    反射解析雏形
      构造器创建实例
      反射为属性赋值
    对象创建 ConstructorConstructor
      InstanceCreator 优先
      默认构造器
      接口默认实现
      Unsafe 兜底
    序列化与反序列化
      ReflectiveTypeAdapterFactory
      read 反序列化
      write 序列化
      BoundField 字段绑定
    整体流程
      JsonElement
      JsonObject
    泛型处理 TypeToken
      类型擦除问题
      匿名子类 + 反射
      getSuperclassTypeParameter
    小结
```

---

## 一、反射解析雏形

Gson 对 JSON 解析的简单原理雏形：**用反射拿到构造器创建对象，再反射遍历属性逐个赋值**。

```java
public class GsonReflect {

    public static class Person {
        public String name;
        public String sex;

        public Person() {
        }

        public Person(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", sex='" + sex + '\'' +
                    '}';
        }
    }


    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //1.反射获取构造器，根据构造器创建Person对象
        Constructor<?> c = Person.class.getDeclaredConstructor();
        Person person = (Person) c.newInstance();
        System.out.println("person=" + person);

        //2.获取Person中的所有属性，为属性赋值
        Field[] fields = Person.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //获取属性名称
            String fieldName = field.getName();
            if(fieldName.equals("name")) {
                field.set(person, "孙悟空");
            } else if(fieldName.equals("sex")) {
                field.set(person, "男");
            }
        }
        System.out.println("person=" + person);
    }

}
```

真实实现主要在 `ConstructorConstructor.java` 和 `ReflectiveTypeAdapterFactory.java` 两个类中。

---

## 二、对象的创建：ConstructorConstructor

### 1. get() 的四级兜底策略

`get(TypeToken<T>)` 按照优先级从高到低依次尝试四种创建方式：

1. **type 维度的 InstanceCreator**：先通过 `type` 获取 `InstanceCreator`，获取到就用它创建对象；
2. **rawType 维度的 InstanceCreator**：再通过 `rawType` 匹配一次；
3. **普通类**：用 `newDefaultConstructor(rawType)` 创建默认构造器对象；
4. **接口类**：用 `newDefaultImplementationConstructor(type, rawType)` 创建默认实现；
5. **最终兜底**：如果自定义 Java 类中没有默认构造器，最终调用 `newUnsafeAllocator` 来创建对象。

```java
public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class<? super T> rawType = typeToken.getRawType();

        //先通过type去获取InstanceCreator，如果获取到InstanceCreator，则通过它来创建构造器对象
        // first try an instance creator

        @SuppressWarnings("unchecked") // types must agree
        final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreators.get(type);
        if (typeCreator != null) {
            return new ObjectConstructor<T>() {
                @Override public T construct() {
                    return typeCreator.createInstance(type);
                }
            };
        }

        //再通过rawType去获取InstanceCreator，如果获取到InstanceCreator，则通过它来创建构造器对象
        // Next try raw type match for instance creators
        @SuppressWarnings("unchecked") // types must agree
        final InstanceCreator<T> rawTypeCreator =
                (InstanceCreator<T>) instanceCreators.get(rawType);
        if (rawTypeCreator != null) {
            return new ObjectConstructor<T>() {
                @Override public T construct() {
                    return rawTypeCreator.createInstance(type);
                }
            };
        }

        //如果是普通类,将使用newDefaultConstructor方法创建一个默认的构造器对象
        ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }

        //如果是接口类，将通过newDefaultImplementationConstructor方法创建一个默认的构造器对象
        ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementation != null) {
            return defaultImplementation;
        }

        //如果自定义的java类中没有默认构造器，那么最终会调用newUnsafeAllocator方法来为你创建对应的Java对象
        // finally try unsafe
        return newUnsafeAllocator(type, rawType);
    }
```

### 2. newDefaultConstructor

`rawType.getDeclaredConstructor()` 不传参数，即获取默认的无参构造器；拿到后通过 `constructor.newInstance(args)` 创建 Java 对象并返回。拿不到默认构造器时返回 null（交给后续兜底策略）。

```java
  private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
    try {
     //此方法返回具有指定参数列表的构造函数对象,在这里没有传参数，即获取默认的构造器
      final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
      if (!constructor.isAccessible()) {
        accessor.makeAccessible(constructor);
      }
      return new ObjectConstructor<T>() {
        @SuppressWarnings("unchecked") // T is the same raw type as is requested
        @Override public T construct() {
          try {
            Object[] args = null;
            return (T) constructor.newInstance(args);  //创建Java对象，并返回
          } catch (InstantiationException e) {
            // TODO: JsonParseException ?
            throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
          } catch (InvocationTargetException e) {
            // TODO: don't wrap if cause is unchecked!
            // TODO: JsonParseException ?
            throw new RuntimeException("Failed to invoke " + constructor + " with no args",
                e.getTargetException());
          } catch (IllegalAccessException e) {
            throw new AssertionError(e);
          }
        }
      };
    } catch (NoSuchMethodException e) {
      return null;
    }
}
```

---

## 三、序列化与反序列化：ReflectiveTypeAdapterFactory

### 1. read 反序列化流程

`Adapter.read()` 的步骤，与代码注释一一对应：

1. 遇到 `JsonToken.NULL` 直接返回 null；
2. `constructor.construct()` **创建一个 Java 对象**；
3. `in.beginObject()` 开始读取一个 JsonObject；
4. 循环 `in.nextName()` 取字段名，根据 name 从 `boundFields` 取对应的 `BoundField`；
5. 字段为 null 或未标记 `deserialized` → `in.skipValue()` 跳过；否则调用 `field.read(in, instance)` 读取 value；
6. `in.endObject()` 结束并返回实例。

### 2. write 序列化流程

value 为 null 时直接 `out.nullValue()`；否则 `beginObject` 后遍历 `boundFields`，`boundField.writeField(value)` 判断该字段是否写出，写出时 `out.name()` + `boundField.write()`，最后 `endObject`。

```java
    public static final class Adapter<T> extends TypeAdapter<T> {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;

        Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            T instance = constructor.construct();  //创建一个java对象

            try {
                in.beginObject(); //开始读取一个JsonObject
                while (in.hasNext()) {
                    String name = in.nextName();//获取name
                    //根据name获取对应的BoundField
                    BoundField field = boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    } else {
                    	//调用BoundField的read方法读取value
                        field.read(in, instance);
                    }
                }
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            in.endObject();
            return instance;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            try {
                for (BoundField boundField : boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            out.endObject();
        }
    }
```

---

## 四、整体流程与 JsonElement

![](./gson.png)

对 JSON 字段、对象的封装：`JsonElement`、`JsonObject` 等，就是预期的对象、数组、基本类型等。

![](./jsonelement.png)

![](./jsonobject.png)

---

## 五、泛型处理：TypeToken

### 1. 为什么需要 TypeToken

Java 的泛型在运行时会有**类型擦除**，Gson 解析将得不到预期的类型，`TypeToken` 就是解决这个问题的。解决方案就是：**匿名类 + 反射**。

### 2. 实现原理

```java
  /**
   * Constructs a new type literal. Derives represented class from type
   * parameter.
   *
   * <p>Clients create an empty anonymous subclass. Doing so embeds the type
   * parameter in the anonymous class's type hierarchy so we can reconstitute it
   * at runtime despite erasure.
   */
  @SuppressWarnings("unchecked")
  protected TypeToken() {
    this.type = getSuperclassTypeParameter(getClass());
    this.rawType = (Class<? super T>) $Gson$Types.getRawType(type);
    this.hashCode = type.hashCode();
  }

  /**
   * Returns the type from super class's type parameter in {@link $Gson$Types#canonicalize
   * canonical form}.
   */
  static Type getSuperclassTypeParameter(Class<?> subclass) {
    Type superclass = subclass.getGenericSuperclass();
    if (superclass instanceof Class) {
      throw new RuntimeException("Missing type parameter.");
    }
    ParameterizedType parameterized = (ParameterizedType) superclass;
    return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
  }
```

原理是：**用一个继承 TypeToken 的匿名类，获取该匿名类的泛型超类，再将泛型超类强制转换为 `ParameterizedType`**。`ParameterizedType` 提供了获取实际类型参数的 `getActualTypeArguments()` 和获取原始类型的 `getRawType()`。

### 3. 使用示例

```java
public class TypeTokenTest {

    //待解析的json字符串： {"data":"data from server"}
    public class Response<T>{
        public T data;//简化数据, 省略了其他字段

        @Override
        public String toString() {
            return "Response{" +
                    "data=" + data +
                    '}';
        }
    }

    private Response<String> data;

    public static void main(String[] args) {
        String json = "{\"data\":\"data from server\"}";

        //fromJson(String json, Class<T> classOfT)的第二个参数classOfT期望获取Response<String>这种类型
        //Class responseClass = Response<String>.class; /但是这么写不能通过编译，因为Response<String>不是一个Class类型
        Class responseClass = Response.class; //只能这样获取类型, 但无法知道Response里面数据的类型

        Response<String> result = (Response<String>) new Gson().fromJson(json, responseClass);
        System.out.println("result=" + result);

        //Gson的解决方案
        Type type = new TypeToken<Response<String>>(){}.getType();
        System.out.println("type=" + type);//TypeTokenTest$Response<java.lang.String>
        result = new Gson().fromJson(json, type);
        System.out.println("result=" + result);

    }
}
```

### 4. 实例化过程分解

`new TypeToken<Response<String>>(){}` 的实例化过程可以分解如下：

```java
class TypeToken$0 extends TypeToken<Response<String>>{
}

TypeToken typeToken = new TypeToken$0();
```

- `TypeToken$0` 是 `TypeToken<Response<String>>` 的**匿名子类**。所以 `typeToken` 是 `TypeToken$0` 类型的，父类型是 `TypeToken<Response<String>>`，而不是 `TypeToken<T>`。
- 所以 `getSuperclassTypeParameter()` 方法中：

```java
Type superclass = subclass.getGenericSuperclass();
```

得到的 `superclass` 是 `TypeToken<Response<String>>`，而不是 `TypeToken<T>`；继续调用 `parameterized.getActualTypeArguments()[0]` 得到的就是 `Response<String>`——这正是我们预期的类型。

---

## 六、小结

Gson 解析的核心链路是：**反射创建对象 → 逐字段绑定（BoundField）→ 读写（TypeAdapter）**。对象创建走 `ConstructorConstructor` 的四级兜底（InstanceCreator → 默认构造器 → 接口默认实现 → Unsafe）；反射解析时字段名的匹配靠 `ReflectiveTypeAdapterFactory` 里的 `boundFields`；泛型信息则在类型擦除的限制下，靠 `TypeToken` 的匿名子类在编译期"藏"进类层次结构、运行期再用反射取回。
