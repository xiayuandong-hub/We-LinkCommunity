package co.yiiu.welink.service;

import co.yiiu.welink.model.Role;

import java.util.List;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface IRoleService {
    Role selectById(Integer roleId);

    List<Role> selectAll();

    void insert(String name, Integer[] permissionIds);

    void update(Integer id, String name, Integer[] permissionIds);

    void delete(Integer id);
}
