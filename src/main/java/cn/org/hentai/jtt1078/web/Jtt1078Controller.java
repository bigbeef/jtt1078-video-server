package cn.org.hentai.jtt1078.web;

import cn.hutool.core.collection.ListUtil;
import cn.org.hentai.jtt1078.publisher.Channel;
import cn.org.hentai.jtt1078.publisher.PublishManager;
import cn.org.hentai.jtt1078.subscriber.Subscriber;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 说明：控制台接口
 *
 * @author winfed
 **/
@RestController
@RequestMapping("/jtt1078")
public class Jtt1078Controller {

    /**
     * 查询当前所有通道推流状态
     *
     * @return
     */
    @RequestMapping("/stat")
    public List<Map<String, Object>> stat() {
        List<Map<String, Object>> list = ListUtil.toList();
        ConcurrentHashMap<String, Channel> channels = PublishManager.getInstance().getChannels();
        channels.forEach((tag, channel) -> {
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("tag", tag);
            resultMap.put("publishing", channel.isPublishing());
            if (channel.getPusherContext() != null) {
                resultMap.put("pusher", channel.getPusherContext().channel().remoteAddress());
            }
            int subscriberCount = 0;
            List<Map<String, Object>> subscriberList = ListUtil.toList();
            for (Subscriber subscriber : channel.getSubscribers()) {
                subscriberCount++;
                HashMap<String, Object> subscriberMap = new HashMap<>();
                ChannelHandlerContext context = subscriber.getContext();
                if (context != null && context.channel() != null) {
                    subscriberMap.put("remoteAddress", context.channel().remoteAddress());
                    subscriberMap.put("localAddress", context.channel().localAddress());
                    subscriberList.add(subscriberMap);
                }
            }
            resultMap.put("subscriberCount", subscriberCount);
            resultMap.put("subscribers", subscriberList);

            list.add(resultMap);
        });
        return list;
    }
}
