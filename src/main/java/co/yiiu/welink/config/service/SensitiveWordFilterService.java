package co.yiiu.welink.config.service;

import co.yiiu.welink.model.SensitiveWord;
import co.yiiu.welink.service.ISensitiveWordService;
import co.yiiu.welink.util.SensitiveWordUtil;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Component
@DependsOn("mybatisPlusConfig")
public class SensitiveWordFilterService {

    @Resource
    private ISensitiveWordService sensitiveWordService;

    // 初始化过滤器
    @PostConstruct
    public void init() {
        List<SensitiveWord> sensitiveWords = sensitiveWordService.selectAll();
        Set<String> sensitiveWordSet = new HashSet<>();
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            sensitiveWordSet.add(sensitiveWord.getWord());
        }
        SensitiveWordUtil.init(sensitiveWordSet);
    }
}
