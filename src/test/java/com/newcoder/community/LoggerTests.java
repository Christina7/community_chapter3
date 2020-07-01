package com.newcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//启用启动类，即配置类CommunityApplication作为正式环境，以它为配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    private static final Logger logger= LoggerFactory.getLogger(LoggerTests.class);

    //logback
    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }
}
