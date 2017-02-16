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

                    DispatcherServlet:

                                * 1、初始化helper类

                                * 2、注册 jsp,servlet 静态方法

                                * 3、根据  请求路径，请求方法  获取处理类

                                * 4、从request,输入流  中获取 请求参数

                                * 5、ReflectionUtil  处理获取结果

                                * 6、根据结果   view,data  进行处理

# v11.0 smart-framework add aop

Proxy  接口  ( doProxy(proxyChain) )

AspectProxy   实现了proxy

ProxyManager   创建所有的代理对象  (CGLibProxy实现就在此处)

ProxyChain  链式代理，

                   当还有时，就去取出相应的proxy代理，调用doProxy()

                   否则，调用invokeSuper，执行目标对象的业务逻辑

 注意：操作对象都是对于链式代理，也就是ProxyChain

 # v13.0 smart-framework
            加载aop框架（这里理解的不是很透彻，等完了翻过来再看吧）
           //获取 Map<代理类,目标类集合> 的映射关系
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            //获取  Map<目标类，代理类实体列表>
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntity : targetMap.entrySet()) {
                //获取目标类
                Class<?> targetClass = targetEntity.getKey();
                //获取代理类实体列表
                List<Proxy> proxyList = targetEntity.getValue();
                //创建代理类对象
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                //将代理类对象放入Bean_Map中
                BeanHelper.setBean(targetClass, proxy);

