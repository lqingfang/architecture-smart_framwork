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
   1、定义类的注解:  
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
  
# v3.0 BeanHelper 实现bean容器  
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
   
# v5.0 ControllerHelper加载 Controller    
	1、request 封装请求信息，路径，方法名  
	2、handler 封装处理信息，类，方法  
	3、ControllerHelper 中 Map<Request, Handler> 用于存放所有的 request,handler对应关系。  
           （思路是从Controller类中取，Controller类的注解就有request路径的信息。）  
  
# v7.0 v6.0 HelperLoader初始化框架  
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


# v8.0 DispatcherServlet请求转发器    
	1、Param请求参数对象  
	2、View视图对象：路径、模型数据  
	3、Data数据对象：模型数据  
	4、StreamUtil流操作工具类  
	5、CodecUtil用于编码与解码  
	6、JsonUtil用于Json与POJO之间的转换  
	7、DispatcherServlet  extends HttpServlet,有init,service方法，一个正常的处理类。  
     
       1>、初始化helperLoader,加载所有的类  
       2>、注册 jsp,servlet 静态方法  
       3>、根据  请求路径，请求方法  从Controller中获取相应的处理类  
       4>、从request,输入流  中获取 请求参数  
       5>、ReflectionUtil  处理获取结果  
       6>、根据结果   view,data  进行处理  
#  总结：  
            至此，一个简单的MVC框架搭建完成。  
            定义了一系列的注解；通过一系列的Helper类来初始化MVC框架；  
            通过DispatcherServlet来处理所有的请求；  
            根据请求方法和请求路径来来调用具体的Action方法，判断Action方法的返回值，若为View类型，则调转到JSP页面，若为Data类型，则返回json数据。


# v11.0 add aop（其实就是加了一个动态代理）
	1、定义切面注解@Aspect  
	2、Proxy  接口  ( doProxy(proxyChain) )  
	3、ProxyChain实体       
   
              成员变量：target(目标类)  
             targetObject(目标对象)  
             targetMethod(目标方法)  
             methodProxy(方法代理)  是cglib提供的一个方法代理对象，在doProxyChain中被使用
             methodParams(方法参数)  
             proxyList(代理列表)  
             proxyIndex(代理索引)  代理对象的计数器  
             方法：doProxyChain()  
        if (proxyIndex<proxyList.size()) {   
              methodResult = proxyList.get(proxyIndex++).doProxy(this);  
           } else {  
              methodResult = methodProxy.invokeSuper(targetObject, methodParams);  
            }  
            return methodResult;  
         }  
   4、ProxyManager  创建所有的代理对象 (cglib代理就在这里)   
   
      public static <T> T createProxy(......) {  
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {//创建代理对象  
           public Object intercept(.......){   
                return new ProxyChain(......).doProxyChain();  
           }  
         });  
     }  
   5、abstract AspectProxy implements proxy ( doProxy(proxyChain) ) （环绕方法在这个里面）  
   
       doProxy(ProxyChain){   
          try {  
              if (intercept(......)) {   
                  before(......);
                  result = proxyChain.doProxyChain();
                  after(......);
              } else {  
                proxyChain.doProxyChain();
              }
          } catch (Exception e) {
              error(cls, method, params, e);
              throw e;
          } finally {
                 end();
          }
         return result;
        }
      public void end() {}
      public void error(......) {}  
      public void after(......) {}  
      public void before(......) {}
      public void begin() {}
      public boolean intercept(......){return true;}  
                  注意：操作对象都是对于链式代理，也就是ProxyChain
   6、AspectProxy中doProxy()方法，从proxyChain中获取目标类、方法、参数，通过try,catch,finally实现调用框架，从框架中抽象出一系列“钩子方法”，这些方法可在AspectProxy的子类有选择性的进行实现，如：    
   
	@Aspect(Controller.class)  
	public class ControllerAspect extends AspectProxy {  
     @Override  
     public void before(...) {.....}
	}
 

# v13.0 AopHelper加载aop框架
    1、ClassHelper.getClassSetBySuper(superClass)  获取指定类的子类及实现类    
    2、ClassHelper.getClassSetByAnnotation(annotationClass) 获取应用包下带某注解的所有类  
    3、AopHelper.createTargetClassSet(aspect)  获取带有指定aspect注解的所有类
    4、AopHelper.createProxyMap() 获取Map<代理类/切面类,目标类集合> 的映射关系    
                     获取所有实现AspectProxy的所有类ClassHelper.getClassSetBySuper(AspectProxy.class)；
       	遍历，获取所有的带有@Aspect注解的类proxyClass.isAnnotationPresent(Aspect.class)
       	遍历，取出所代理的是哪一类，proxyClass.getAnnotation(Aspect.class)  
                      获取带有该注解的类集合 createTargetClassSet(aspect);
                      放入map，返回  
    5、AopHelper.createTargetMap() 获取  Map<目标类，代理实例化集合类>   
                     遍历  Map<代理类/切面类,目标类集合>；
                     遍历目标类集合，实例化切面类，如果map中有目标类，之间将切面的实例化类添加到目标类对应的map中，没有创建添加即可  
    6、AopHelper中静态块来初始化整个AOP框架    
        createProxyMap() 获取Map<代理类/切面类,目标类集合> 的映射关系  
        createTargetMap() 获取  Map<目标类，代理实例化集合类> 
                       遍历   Map<目标类，代理实例化集合类> 获取代理对象ProxyManager.createProxy(目标类，代理实例化集合类)---->proxy
        BeanHelper.setBean(targetClass, proxy)  代理类，代理对象的映射关系
    在AopHelper中获取所有的目标类及其被拦截的切面类实例，并通过ProxyManager#createProxy方法来创建代理对象，最后将其放入BeanMap中。  
            
# v15.0 事务的aop实现(此列可作为增加代理类的一个例子)：  
	1、定义事务注解@Transaction
		@Target(ElementType.METHOD)
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Transaction {}
	2、DatabaseHelper里面封装对数据库的操作
		开启事务、提交事务、回滚事务
		注意：开启事务时，需要将自动提交设置为false,将Connection放入本地线程变量中；
			    事务提交或回滚后，需要移除本地线程变量中的Connection对象  
	3、TransactionProxy implements Proxy  事务代理切面类		
	           有一个标志，保证同一线程中事务控制相关逻辑只会执行一次。  
	4、在AopHelper中添加事务代理，两个私有方法，一个是对普通切面的代理，一个是对事务的代理（为什么呢？）  
# 优化Action参数  
     1、Param中增加isEmpty()方法  
     2、DispatcherServlet中拿到参数判断下Param是否为空  
     
 

       
