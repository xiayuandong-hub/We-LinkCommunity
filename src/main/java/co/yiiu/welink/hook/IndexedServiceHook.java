package co.yiiu.welink.hook;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public class IndexedServiceHook {

    @Pointcut("execution(public * co.yiiu.welink.service.IIndexedService.indexAllTopic(..))")
    public void indexAllTopic() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.IIndexedService.indexTopic(..))")
    public void indexTopic() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.IIndexedService.deleteTopicIndex(..))")
    public void deleteTopicIndex() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.IIndexedService.batchDeleteIndex(..))")
    public void batchDeleteIndex() {
    }

}
