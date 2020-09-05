package cn.org.hentai.jtt1078;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 说明：模拟推流
 *
 * @author winfed
 **/
@Service
public class MediaMockService {
    @Value("${server.live.mock:false}")
    private Boolean isMock;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {
        if (isMock) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleWithFixedDelay(this::mock, 0, 100L, TimeUnit.MILLISECONDS);
        }
    }

    public void mock() {
        // http://127.0.0.1:3333/test/multimedia#013800138999-2
        try {
            Socket conn = new Socket("localhost", 1078);
            OutputStream os = conn.getOutputStream();

            InputStream fis = MediaMockService.class.getResourceAsStream("/tcpdump.bin");
            int len = -1;
            byte[] block = new byte[512];
            while ((len = fis.read(block)) > -1) {
                os.write(block, 0, len);
                os.flush();
                Thread.sleep(20);
            }
            os.close();
            fis.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
