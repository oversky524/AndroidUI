package io.base.model;

import com.alibaba.fastjson.JSON;

import rx.functions.Func1;

/**
 * Created by gaochao on 2016/6/3.
 */
public class GetList implements Func1<ResultBean, Object> {
    private String key;
    private Class<?> cls;

    public GetList(Class<?> cls, String key) {
        this.cls = cls;
        this.key = key;
    }

    public GetList(Class<?> cls) {
        this.cls = cls;
    }

    @Override
    public Object call(ResultBean resultBean) {
        if (resultBean != null && resultBean.isOk()) {
            if(key != null && key.length() > 0)
            return JSON.parseArray(JSON.parseObject(resultBean.getData()).getString(key), cls);
            else
                return JSON.parseArray(resultBean.getData(), cls);
        }
        return resultBean;
    }
}
