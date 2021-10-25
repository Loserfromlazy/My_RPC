package com.learn.provider.handler;

import com.alibaba.fastjson.JSON;
import com.learn.provider.anno.RpcService;
import com.learn.rpc.common.RpcRequest;
import com.learn.rpc.common.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * RpcServerHandler 服务端业务处理类
 * 1.将标有@RpcService注解的bean缓存
 * 2.接收客户端请求，根据传递的beanName从缓存中查找到对应的bean
 * 3.解析请求中的方法名称，参数类型，参数信息
 * 4.反射调用bean中的方法
 * 5.响应客户端
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/23
 */
@Component
public class RpcServerHandler extends SimpleChannelInboundHandler implements ApplicationContextAware {

    private static final Map SERVICE_INSTANCE_MAP = new ConcurrentHashMap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将msg转换成RpcRequest
        ByteBuf buf = (ByteBuf) msg;
        RpcRequest rpcRequest = JSON.parseObject(buf.toString(), RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            //业务处理
            rpcResponse.setResult(handler(rpcRequest));
        }catch (Exception e){
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        ctx.writeAndFlush(JSON.toJSONString(rpcResponse));
    }
    /**
     * @Author Yuhaoran
     * @Description 业务处理逻辑
     * @param rpcRequest
     * @return java.lang.Object
     * @Date 2021/10/23 16:15
     **/
    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {
        //从缓存中获取bean
        String className = rpcRequest.getClassName();
        Object serviceBean = SERVICE_INSTANCE_MAP.get(className);
        if (serviceBean == null){
            throw new RuntimeException("根据BeanName找不到服务,BeanName="+className);
        }
        Class<?> clazz = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();
        //反射调用方法 CGLIB
        FastClass fastClass = FastClass.create(clazz);
        FastMethod method = fastClass.getMethod(methodName,parameterTypes);
        return method.invoke(serviceBean,parameters);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //找到标有RpcService注解的全部bean
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceMap != null && serviceMap.size() > 0) {
            Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
                entries.forEach(item -> {
                    Object serviceBean = item.getValue();
                    if (serviceBean.getClass().getInterfaces().length ==0){
                        throw new RuntimeException("服务必须实现接口");
                    }
                    //默认 约定 取第一个接口作为缓存Bean的名称
                    String name = serviceBean.getClass().getInterfaces()[0].getName();
                    SERVICE_INSTANCE_MAP.put(name,serviceBean);
                });
        }
    }
}
