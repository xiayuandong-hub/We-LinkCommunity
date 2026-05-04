package co.yiiu.welink.mapper;

import co.yiiu.welink.model.Comment;
import co.yiiu.welink.model.vo.CommentsByTopic;
import co.yiiu.welink.util.MyPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentsByTopic> selectByTopicId(@Param("topicId") Integer topicId);

    MyPage<Map<String, Object>> selectByUserId(MyPage<Map<String, Object>> iPage, @Param("userId") Integer userId);

    MyPage<Map<String, Object>> selectAllForAdmin(MyPage<Map<String, Object>> iPage, @Param("startDate") String
            startDate, @Param("endDate") String endDate, @Param("username") String username);

    int countToday();
}
