package org.smart4j.framework.proxy;

import java.lang.reflect.Method;

/**
 * Created by sally on 2017/2/15.
 */
public abstract class AspectProxy implements Proxy{

    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;
        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();
        begin();
        try {
            if (intercept(cls, method, params)) {
                before(cls, method, params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
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
    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable{
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable{
    }

    public void begin() {

    }
    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

}
