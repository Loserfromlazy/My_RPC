package com.learn.rpc.api;

import com.learn.rpc.pojo.User;

/**
 * 用户服务
 **/
public interface IUserService {
    User getById(Integer id);
}
