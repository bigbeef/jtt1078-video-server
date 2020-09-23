package cn.org.hentai.jtt1078.web;

import cn.hutool.core.collection.ListUtil;
import cn.org.hentai.jtt1078.publisher.Channel;
import cn.org.hentai.jtt1078.publisher.PublishManager;
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
            resultMap.put("subscriberCount", channel.getSubscribers().size());
            list.add(resultMap);
        });
        return list;
    }
}
