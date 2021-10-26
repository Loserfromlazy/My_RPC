package com.learn.consumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

/**
 * <p>
 * RpcClientHandler
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/26
 */
public class RpcClientHandler extends SimpleChannelInboundHandler implements Callable {

    ChannelHandlerContext ctx;

    String requestMsg;

    String responseMsg;



    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        responseMsg = (String) msg;
        //唤醒等待的线程
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }

    /**
     * 发送消息到服务端
     *
     * @author Yuhaoran
     * @date 2021/10/26 18:44
     */
    @Override
    public synchronized Object call() throws Exception {
        ctx.writeAndFlush(requestMsg);
        wait();
        return responseMsg;
    }
}
