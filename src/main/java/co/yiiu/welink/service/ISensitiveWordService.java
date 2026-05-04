package co.yiiu.welink.service;

import co.yiiu.welink.model.SensitiveWord;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface ISensitiveWordService {
    void save(SensitiveWord sensitiveWord);

    void update(SensitiveWord sensitiveWord);

    List<SensitiveWord> selectAll();

    void deleteById(Integer id);

    IPage<SensitiveWord> page(Integer pageNo, String word);

    void updateWordById(Integer id, String word);

    SensitiveWord selectByWord(String word);
}
