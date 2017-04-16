# architecture-smart_framwork

# v3.0 实现bean容器
   ClassHelper 获取指定的类集合(getServiceClassSet,getControllerClassSet)

   ReflectionUtil 封装反射，提供实例化的类

   BeanHelper 返回Map<Class<?>, Object>

   
# v4.0 smart-framework 实现依赖注入

   BeanHelper 获取包下所有的类  Map<Class<?>, Object>  里面存放的是类与对象的映射关系

   IocHelper 遍历Map<Class<?>, Object> beanMap

   再遍历每一个 Class<?>, Object下的成员变量

   如果变量有inject修饰，则利用ReflectionUtil修改当前变量的值

   注意：因为 BeanMap中的对象都是事先创建好放入 beanMap的，

             所以 所有的对象都是单例的
             
# v5.0 smart-framework 加载 Controller

   request 封装请求信息，路径，方法名

   handler 封装处理信息，类，方法

   ControllerHelper 中 Map<Request, Handler> 用于存放所有的 request,handler对应关系。
   
   （思路是从Controller类中取，Controller类的注解就有request路径的信息。）

   最后调用getHandler(requestMethod,requestPath)  就可以获取handler
   
# v7.0 v6.0 smart-framework 初始化框架

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

       DispatcherServlet（extends HttpServlet,有init,service方法，就一个正常的处理类，根据请求路径找action进行处理）:

        * 1、初始化helper类

        * 2、注册 jsp,servlet 静态方法

        * 3、根据  请求路径，请求方法  获取处理类

        * 4、从request,输入流  中获取 请求参数

        * 5、ReflectionUtil  处理获取结果

        * 6、根据结果   view,data  进行处理

# v11.0 smart-framework add aop

Proxy  接口  ( doProxy(proxyChain) )

AspectProxy   实现了proxy ( doProxy(proxyChain) ) （环绕方法在这个里面）

ProxyManager   创建所有的代理对象  (CGLibProxy实现就在此处，Enhancer.create(targetClass,new MethodInterceptor(方法拦截器){}))

ProxyChain  链式代理，
       当还有时，就去取出相应的proxy代理，调用doProxy()
       
       否则，调用invokeSuper，执行目标对象的业务逻辑
       
      public Object doProxyChain() throws Throwable {  
      
             Object methodResult;  
             
             if (proxyIndex<proxyList.size()) {   
               
                //Proxy.doProxy()中有相应的横切逻辑，doProxy是调用代理类AspectProxy里面的方法  
                  
               methodResult = proxyList.get(proxyIndex++).doProxy(this);  
                
              } else {  
              
                 methodResult = methodProxy.invokeSuper(targetObject, methodParams);  
                 
              }  
              
             return methodResult;  
             
          }

                  

 注意：操作对象都是对于链式代理，也就是ProxyChain  
 

# v13.0 smart-framework 加载aop框架
              
    在AopHelper中获取所有的目标类及其被拦截的切面类实例，并通过ProxyManager#createProxy方法来创建代理对象，最后将其放入BeanMap中。  
            
           

# v15.0 事务的aop实现(此列可作为增加代理类的一个例子)：

 1、先定义一个注解类  transaction

 2、修改databaseHelper，增加开启事务，关闭事务，回滚事务的操作

 3、编写切面代理类 TransactionProxy   implements Proxy

 4、在框架中添加事务代理机制

       
