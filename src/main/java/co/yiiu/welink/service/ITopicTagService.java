package co.yiiu.welink.service;

import co.yiiu.welink.model.Tag;
import co.yiiu.welink.model.TopicTag;

import java.util.List;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface ITopicTagService {
    List<TopicTag> selectByTopicId(Integer topicId);

    List<TopicTag> selectByTagId(Integer tagId);

    void insertTopicTag(Integer topicId, List<Tag> tagList);

    // 删除话题所有关联的标签记录
    void deleteByTopicId(Integer id);
}
