# architecture-smart_framwork

#  获取配置文件的常量
   ConfigConstant中定义的是和配置文件相对应的常量     
   ConfigHelper定义一些静态方法，借用ProsUtil工具类获取配置文件中定义的字符串 
    
#  开发一个类加载器  
   ClassUtil    
   1、获取类加载器：getClassLoader() ====> Thread.currentThread().getContextClassLoader();  
   2、加载类：loadClass ====>  Class.forName(className, isInitialized, getClassLoader());    
   3、获取指定包下的所有类：getClassSet ====> 首先将包名转化为文件路径，获取class文件或jar包，获取指定的类名去加载类    
   
#  定义注解    
   1、定义类的注解：  
      @Target(ElementType.Type)  
      @Retention(RetentionPolicy.RUNTIME)  
   2、定义方法的注解：  
      @Target(ElementType.METHOD)  
      @Retention(RetentionPolicy.RUNTIME)  
   3、定义依赖注入的注解：  
      @Target(ElementType.FIELD)  
      @Retention(RetentionPolicy.RUNTIME)    
#  ClassHelper 获取各种想要类的类集合（封装了ClassUtil）      
              遍历ClassUtil获取到的类集合，根据cls.isAnnotationPresent(Service.class)获取指定的类集合
  
# v3.0 实现bean容器  
   1、ReflectionUtil 封装反射    
               创建实例：newInstance(Class<?> cls) cls.newInstance();   
               方法实例化：invokeMethod(obj,method,..) method.setAccessible(true);  method.invoke(obj, args);   
               成员变量实例化：setField(obj,field,..) field.setAccessible(true);  field.set(obj,value);               
                                 
   2、 BeanHelper 返回Map<Class<?>, Object>,存放了Bean类与Bean实例的映射关系          
      ClassHelper 获取所有的类集合，遍历，调用ReflectionUtil进行实例化，获取了Bean类与Bean实例的映射关系

# v4.0 IocHelper实现依赖注入

   1、从BeanHelper中获取包下所有的类  Map<Class<?>, Object> beanMap，里面存放了Bean类与Bean实例的映射关系   
   2、遍历bean,获取类中的成员变量  
   3、变量成员变量，如果有@Inject注解，则去beanMap中获取所需的bean实例，通过ReflectionUtil修改当前成员变量的值  
   
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

       
