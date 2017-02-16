package org.smart4j.framework.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.annotation.Transaction;
import org.smart4j.framework.helper.DatabaseHelper;

import java.lang.reflect.Method;

/**
 * 事务代理切面类
 * Created by sally on 2017/2/16.
 */
public class TransactionProxy implements Proxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);
    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;
        //获取当前线程
        Boolean flag = FLAG_HOLDER.get();
        //获取需要代理的方法
        Method method = proxyChain.getTargetMethod();
        //当前线程未被初始化过，即未开启事务，并且带有transaction注解
        if (!flag && method.isAnnotationPresent(Transaction.class)) {
            //当前线程初始化
            FLAG_HOLDER.set(true);
            try {
                //开启事务
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                //执行目标方法
                result = proxyChain.doProxyChain();
                //提交事务
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            } catch (Exception e) {
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            }
        } else {
            result = proxyChain.doProxyChain();
        }
        return result;
    }
}
