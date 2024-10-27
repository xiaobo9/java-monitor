package me.xiaobo.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * vlcj 连接监控摄像头
 *
 */
public class VlcjRtsp {
    private static Logger logger = LoggerFactory.getLogger(VlcjRtsp.class);

    public static void main(String[] args) throws IOException {

        ConfigUtils.init(args);

        MainFrame frame = new MainFrame();
        logger.info("启动");
        frame.prepare();
        frame.start();

    }

}