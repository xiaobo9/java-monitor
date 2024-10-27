package me.xiaobo.monitor;

import javax.swing.JFrame;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RtspClient {
    
    public static void main(String[] args) {
        String url =String.format("rtsp://admin:admin@%s:554/h264/ch33/main/av_stream", getIp()) ;
        try (FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(url)){
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);
            grabber.setImageWidth(1280);
            grabber.setImageHeight(720);
            
            grabber.start();

            CanvasFrame canvasFrame  = new CanvasFrame("监控");
            canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvasFrame.setAlwaysOnTop(false);
            while(canvasFrame.isDisplayable()) {
                Frame  frame = grabber.grabImage();
                canvasFrame.showImage(frame);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getIp() {
        String ip = "";
        for (int i = 10; i <= 50; i++) {
            ip = String.format("192.168.1.%d", i);
            try (Socket socket = new Socket();) {
                socket.setSoTimeout(500);
                socket.connect(new InetSocketAddress(ip, 554), 500);
                return ip;
            } catch (IOException e) {
                // skip
            }
        }
        return ip;
    }

}
