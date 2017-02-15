package org.smart4j.framework.proxy;

/**
 * Created by sally on 2017/2/15.
 */
public interface Proxy {
    /*
    * 执行链式代理
    */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
