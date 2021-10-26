package com.learn.provider;

import com.learn.provider.server.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * ServerBootStrapApplication
 * </p>
 *
 * @author Yuhaoran
 * @since 2021/10/23
 */
@SpringBootApplication
public class ServerBootStrapApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ServerBootStrapApplication.class);
    }

    @Autowired
    private RpcServer rpcServer;
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rpcServer.startServer("127.0.0.1",8899);
            }
        }).start();
    }
}
