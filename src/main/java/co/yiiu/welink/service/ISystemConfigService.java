package co.yiiu.welink.service;

import co.yiiu.welink.model.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface ISystemConfigService {
    Map<String, String> selectAllConfig();

    // 刷新缓存（E2E 测试用，确保 schema 加载后缓存正确）
    void refreshCache();

    // 根据键取值
    SystemConfig selectByKey(String key);

    Map<String, Object> selectAll();

    // 在更新系统设置后，清一下selectAllConfig()的缓存
    void update(List<Map<String, String>> list);

    // 根据key更新数据
    void updateByKey(String key, SystemConfig systemConfig);

    Map<String, String> selectAllConfigWithoutPassword();
}
