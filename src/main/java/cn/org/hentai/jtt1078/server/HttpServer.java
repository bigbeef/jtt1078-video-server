package cn.org.hentai.jtt1078.server;

import cn.org.hentai.jtt1078.http.GeneralResponseWriter;
import cn.org.hentai.jtt1078.http.NettyHttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * 说明：HttpServer
 *
 * @author winfed
 **/
@Slf4j
public class HttpServer {

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Integer port;

    public HttpServer(Integer port){
        this.port = port;
    }

    public void init() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new GeneralResponseWriter(),
                                new HttpServerCodec(),
                                new HttpObjectAggregator(1024 * 64),
                                new NettyHttpServerHandler()
                        );
                    }
                }).option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture f = serverBootstrap.bind(InetAddress.getByName("0.0.0.0"), port).sync();
            log.info("HTTP Server started at: {}", port);
            f.channel().closeFuture();
        } catch (InterruptedException e) {
            log.error("http server error", e);
        }
    }

    public void destroy() {
        log.info("开始销毁 HttpServer");
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("HttpServer 销毁失败", e);
        }
    }

}
