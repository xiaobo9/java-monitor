package me.xiaobo.monitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {
    private static Logger logger = LoggerFactory.getLogger(Tools.class);

    public static String getIp() {
        String ip = "";
        for (int i = 10; i <= 50; i++) {
            ip = String.format("192.168.1.%d", i);
            try (Socket socket = new Socket();) {
                socket.setSoTimeout(500);
                socket.connect(new InetSocketAddress(ip, 554), 500);
                logger.info("ip: {} 可联通", ip);
                return ip;
            } catch (IOException e) {
                logger.debug("ip: {}", ip, e);
            }
        }
        return ip;
    }

    public static Image image(String path) throws IOException {
        return ImageIO.read(Tools.class.getResource(path));
    }

    public static Image image2(String path) {
        return new ImageIcon(Tools.class.getResource(path)).getImage();
    }
}
