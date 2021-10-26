package com.learn.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.learn.consumer.client.RpcClient;
import com.learn.rpc.common.RpcRequest;
import com.learn.rpc.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * <p>
 * RpcClientProxy
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/26
 */
public class RpcClientProxy {
    public static Object createProxy(Class serviceClass){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class[]{serviceClass}, (proxy, method, args) -> {
                    //封装request请求对象
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    request.setMethodName(method.getName());
                    //创建RpcClient
                    RpcClient rpcClient = new RpcClient("127.0.0.1",8899);
                    try {
                        Object responseMsg = rpcClient.send(JSON.toJSONString(request));
                        RpcResponse rpcResponse = JSON.parseObject(responseMsg.toString(), RpcResponse.class);
                        if (rpcResponse.getError()!=null){
                            throw new RuntimeException(rpcResponse.getError());
                        }
                        Object result = rpcResponse.getResult();
                        return JSON.parseObject(result.toString(), method.getReturnType());
                    }catch (Exception e){
                        throw e;
                    }finally {
                        rpcClient.close();
                    }

                });

    }
}
