package io.base.model;

import com.alibaba.fastjson.JSON;

import rx.functions.Func1;

/**
 * Created by gaochao on 2016/6/12.
 */
public class GetResultObject implements Func1<ResultBean, Object> {
    private Class<?> cls;

    public GetResultObject(Class<?> cls){ this.cls = cls; }

    @Override
    public Object call(ResultBean resultBean) {
        return (resultBean != null && resultBean.isOk()) ? JSON.parseObject(resultBean.getData(), cls) : resultBean;
    }
}
