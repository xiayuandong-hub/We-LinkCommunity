package co.yiiu.welink.hook;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public class CommentServiceHook {

    @Pointcut("execution(public * co.yiiu.welink.service.ICommentService.selectByTopicId(..))")
    public void selectByTopicId() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ICommentService.insert(..))")
    public void insert() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ICommentService.update(..))")
    public void update() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ICommentService.vote(..))")
    public void vote() {
    }

    @Pointcut("execution(public * co.yiiu.welink.service.ICommentService.delete(..))")
    public void delete() {
    }

}
