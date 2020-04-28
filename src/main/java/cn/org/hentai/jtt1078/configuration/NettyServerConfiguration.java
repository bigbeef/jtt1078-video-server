package cn.org.hentai.jtt1078.configuration;


import cn.org.hentai.jtt1078.server.HttpServer;
import cn.org.hentai.jtt1078.server.VideoServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 说明：netty server配置类
 *
 * @author winfed
 **/
@Configuration
public class NettyServerConfiguration {


    @Value("${server.http.port}")
    private Integer httpPort;

    @Value("${server.live.port}")
    private Integer livePort;

    @Value("${server.playback.port}")
    private Integer playback;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public HttpServer httpServer() {
        return new HttpServer(httpPort);
    }

    @Bean(name = "liveVideoServer", initMethod = "init", destroyMethod = "destroy")
    public VideoServer liveVideoServer() {
        return new VideoServer(livePort, "");
    }

    @Bean(name = "playbackVideoServer", initMethod = "init", destroyMethod = "destroy")
    public VideoServer playbackVideoServer() {
        return new VideoServer(playback, "-playback");
    }
}
