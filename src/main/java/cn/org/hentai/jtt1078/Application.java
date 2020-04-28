package cn.org.hentai.jtt1078;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 说明：启动类
 *
 * @author winfed
 **/
@Slf4j
@SpringBootApplication(scanBasePackages = {"cn.org.hentai"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
