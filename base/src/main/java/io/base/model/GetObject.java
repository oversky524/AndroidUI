package io.base.model;

import com.alibaba.fastjson.JSON;

import rx.functions.Func1;

/**
 * Created by gaochao on 2016/6/3.
 */
public class GetObject implements Func1<ResultBean, Object> {
    private String key;
    private Class<?> cls;

    public GetObject(Class<?> cls, String key) {
        this.cls = cls;
        this.key = key;
    }

    @Override
    public Object call(ResultBean resultBean) {
        if(resultBean != null && resultBean.isOk()){
            return JSON.parseObject(JSON.parseObject(resultBean.getData()).getString(key), cls);
        }
        return resultBean;
    }
}
