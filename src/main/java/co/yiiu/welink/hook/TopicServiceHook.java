package co.yiiu.welink.hook;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public class TopicServiceHook {

    @Pointcut("execution(public * co.yiiu.welink.service.ITopicService.search(..))")
    public void search() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ITopicService.selectById(..))")
    public void selectById() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ITopicService.update(..))")
    public void update() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ITopicService.vote(..))")
    public void vote() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ITopicService.updateViewCount(..))")
    public void updateViewCount() {
    }

}
