package org.smart4j.framework;

import org.smart4j.framework.bean.Data;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sally on 2017/2/13.
 * 1、初始化helper类
 * 2、注册 jsp,servlet 静态方法
 * 3、根据  请求路径，请求方法  获取处理类
 * 4、从request,输入流  中获取 请求参数
 * 5、ReflectionUtil  处理获取结果
 * 6、根据结果   view,data  进行处理
 */
public class DispatcherServlet extends HttpServlet{
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化相关helper类
        HelperLoader.init();
        //获取servletContext
        ServletContext servletContext = servletConfig.getServletContext();
        //注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+ "*");
        //注册处理静态资源的servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+ "*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //获取请求方法
        String requestMethod = req.getMethod().toLowerCase();
        //获取请求路径
        String requestPath = req.getPathInfo();
        //根据 请求方法，请求路径  获取处理类
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if(handler != null) {
            //获取  处理类
            Class<?> controllerClass = handler.getControllerClass();
            //获取  处理类的  实体
            Object controllerBean = BeanHelper.getBean(controllerClass);
            //创建 请求参数对象
            Map<String, Object> paramMap = new HashMap<String, Object>();
                //获取所有的请求参数名
                Enumeration<String> parameterNames = req.getParameterNames();
                //遍历请求参数
                while(parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    String paramValue = req.getParameter(paramName);
                    paramMap.put(paramName, paramValue);
            }
            //获取输入流中的请求参数
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if (StringUtil.isNotEmpty(body)) {
                String[] params = StringUtil.splitString(body, "&");
                if (ArrayUtil.isNotEmpty(params)) {
                    for(String param:params) {
                        String[] array = StringUtil.splitString(param, "=");
                        if (ArrayUtil.isNotEmpty(array) && array.length ==2) {
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName,paramValue);
                        }
                    }
                }
            }
            //请求参数从此以后 就是 param 了
            Param param = new Param(paramMap);
            //获取 处理类的方法
            Method actionMethod = handler.getActionMethod();
            //处理，获得结果
            Object result;
            if(param.isEmpty()) {
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
            } else {
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            }

            if (result instanceof View) {
                View view = (View) result;
                String path = view.getPath();
                if(StringUtil.isNotEmpty(path)) {
                    if (path.startsWith("/")) {
                        resp.sendRedirect(req.getContextPath()+path);
                    } else {
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry:model.entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        req.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(req,resp);
                    }
                }
            } else if(result instanceof Data) {
                Data data = (Data) result;
                Object model = data.getModel();
                if(model != null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter writer = resp.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}
