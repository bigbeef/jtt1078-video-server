package cn.org.hentai.jtt1078.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * 说明：VideoServer
 *
 * @author winfed
 **/
@Slf4j
public class VideoServer {

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Integer port;
    private String tagSuffix;

    public VideoServer(Integer port, String tagSuffix) {
        this.port = port;
        this.tagSuffix = tagSuffix;
    }

    public void init() throws Exception {
        serverBootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        workerGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new Jtt1078MessageDecoder());
                        p.addLast(new Jtt1078Handler(tagSuffix));
                    }
                });

        Channel ch = serverBootstrap.bind(InetAddress.getByName("0.0.0.0"), port).sync().channel();
        log.info("Video Server started at: {}", port);
        // ch.closeFuture().sync();
        ch.closeFuture();
    }

    public void destroy() {
        log.info("开始销毁 VideoServer");
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("VideoServer 销毁失败", e);
        }
    }
}
