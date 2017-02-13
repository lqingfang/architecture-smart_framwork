package org.smart4j.framework.bean;

/**
 * Created by sally on 2017/2/13.
 * 封装返回的数据
 */
public class Data {
    private Object model;

    public Data(Object model) {
        this.model = model;
    }
    public Object getModel() {
        return model;
    }
}
