package com.learn.consumer;

import com.learn.consumer.proxy.RpcClientProxy;
import com.learn.rpc.api.IUserService;
import com.learn.rpc.pojo.User;

/**
 * <p>
 * ClientBootStrap
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/26
 */
public class ClientBootStrap {
    public static void main(String[] args) {
        IUserService proxy = (IUserService) RpcClientProxy.createProxy(IUserService.class);
        User byId = proxy.getById(1);
        System.out.println("查询结果"+byId);

    }
}
