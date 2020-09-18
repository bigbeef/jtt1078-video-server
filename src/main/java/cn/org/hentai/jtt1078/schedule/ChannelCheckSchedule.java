package cn.org.hentai.jtt1078.schedule;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.org.hentai.jtt1078.publisher.Channel;
import cn.org.hentai.jtt1078.publisher.PublishManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 说明：节流检测
 *
 * @author winfed
 **/
@Slf4j
@Component
public class ChannelCheckSchedule {

    // 过期防止残余堆积
    private final static ExpiringMap<String, Date> channelCheckMap = ExpiringMap.builder()//
            .expirationPolicy(ExpirationPolicy.ACCESSED)//
            .expiration(120, TimeUnit.SECONDS)//
            .build();

    @PostConstruct
    public void doChannelCheck() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            ConcurrentHashMap<String, Channel> channels = PublishManager.getInstance().getChannels();
            channels.forEach((tag, channel) -> {
                try {
                    int subscriberSize = channel.getSubscribers().size();
                    if (subscriberSize == 0) {
                        Date lastDate = channelCheckMap.get(tag);
                        if (lastDate == null) {
                            channelCheckMap.put(tag, DateUtil.date());
                        } else {
                            // 超过30秒无人订阅，断开推流
                            if (DateUtil.between(lastDate, DateUtil.date(), DateUnit.SECOND) > 30L) {
                                ChannelHandlerContext pusherContext = channel.getPusherContext();
                                if (pusherContext != null && pusherContext.channel().isOpen()) {
                                    pusherContext.close();
                                    log.info("节流停止推流:" + tag);
                                }
                                channels.remove(tag);
                            }
                        }
                    } else {
                        channelCheckMap.remove(tag);
                    }
                } catch (Exception e) {
                    log.error("节流检测异常:" + tag, e);
                }
            });
        }, 0L, 30, TimeUnit.SECONDS);
    }


}
