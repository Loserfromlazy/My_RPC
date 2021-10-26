package com.learn.provider.server;

import com.learn.provider.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * RpcServer
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/23
 */
@Service
public class RpcServer implements DisposableBean {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    @Autowired
    RpcServerHandler rpcServerHandler;

    public void startServer(String ip, Integer port) {
        try {
            bossGroup =new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加解码器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            //业务处理类
                            pipeline.addLast(rpcServerHandler);
                        }
                    });
            ChannelFuture sync = serverBootstrap.bind(ip, port).sync();
            System.out.println("服务端启动成功");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (bossGroup!=null){
                bossGroup.shutdownGracefully();
            }
            if (bossGroup!=null){
                bossGroup.shutdownGracefully();
            }
        }
    }

    /**
     * @Author Yuhaoran
     * @Description 此方法   可以在容器销毁时执行
     * @Date 2021/10/23 15:42
     **/
    @Override
    public void destroy() throws Exception {
        if (bossGroup!=null){
            bossGroup.shutdownGracefully();
        }
        if (bossGroup!=null){
            bossGroup.shutdownGracefully();
        }
    }
}
