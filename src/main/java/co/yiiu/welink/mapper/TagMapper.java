package co.yiiu.welink.mapper;

import co.yiiu.welink.model.Tag;
import co.yiiu.welink.util.MyPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface TagMapper extends BaseMapper<Tag> {

    MyPage<Map<String, Object>> selectTopicByTagId(MyPage<Map<String, Object>> iPage, @Param("tagId") Integer tagId);

    int countToday();
}
