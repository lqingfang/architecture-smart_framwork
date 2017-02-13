# architecture-smart_framwork

# v3.0 实现bean容器
   ClassHelper 获取指定的类集合(getServiceClassSet,getControllerClassSet)

   ReflectionUtil 封装反射，提供实例化的类

   BeanHelper 返回Map<Class<?>, Object>

   
# v4.0 smart-framework 实现依赖注入

   BeanHelper 获取包下所有的类  Map<Class<?>, Object>

   IocHelper 遍历Map<Class<?>, Object> beanMap

   再遍历每一个 Class<?>, Object下的成员变量

   如果变量有inject修饰，则利用ReflectionUtil修改当前变量的值

   注意：因为 BeanMap中的对象都是事先创建好放入 beanMap的，

             所以 所有的对象都是单例的
             
# v5.0 smart-framework 加载 Controller

   request 封装请求信息，路径，方法名

   handler 封装处理信息，类，方法

   ControllerHelper 中 Map<Request, Handler> 用于存放所有的 request,handler对应关系。

   最后调用getHandler(requestMethod,requestPath)  就可以获取handler
   
# v7.0 v6.0 smart-framework 初始化框架

  第98页以下代码有句话不太明白什么意思。

“实际上，当我们在第一次访问类时，就会加载其static块，这里只是为了让加载更加集中，所以才写了一个HelperLoader类。”

意思是，没有下面这些代码也可以吗？加上下面这些只是为了让加载更加集中吗？

理解：没有此处也可以，当第一次访问类时，就会加载static块，有了下面这些代码就可以集中加载了

public final class HelperLoader {

    public static void init() {
        // 定义需要加载的 Helper 类
        Class<?>[] classList = {
            DatabaseHelper.class,
            EntityHelper.class,
            ActionHelper.class,
            BeanHelper.class,
            AopHelper.class,
            IocHelper.class,
            PluginHelper.class,
        };
        // 按照顺序加载类
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}

 # v8.0 smart-framework 请求转发器完成 （此时mvc简单搭建完成）
                                * 1、初始化helper类
                                * 2、注册 jsp,servlet 静态方法
                                * 3、根据  请求路径，请求方法  获取处理类
                                * 4、从request,输入流  中获取 请求参数
                                * 5、ReflectionUtil  处理获取结果
                                * 6、根据结果   view,data  进行处理


