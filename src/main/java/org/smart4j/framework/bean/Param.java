package org.smart4j.framework.bean;

import org.smart4j.framework.util.CastUtil;
import org.smart4j.framework.util.CollectionUtil;

import java.util.Map;

/**
 * Created by sally on 2017/2/13.
 * 封装的  请求参数对象
 */
public class Param {
    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
    public long getLong(String name) {
        return CastUtil.castLong(paramMap.get(name));
    }
    public Map<String, Object> getMap() {
        return paramMap;
    }
    public Boolean isEmpty(){
        return CollectionUtil.isEmpty(paramMap);
    }
}
