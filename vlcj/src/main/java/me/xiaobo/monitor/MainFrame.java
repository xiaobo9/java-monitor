package me.xiaobo.monitor;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

    private JFrame frame;
    private PopupMenu popupMenu;
    private SystemTray sysTray;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public void prepare() throws IOException {
        frame = new JFrame("监控");
        frame.setIconImage(Tools.image("/icon/1.png"));
        frame.setBounds(100, 100, 600, 400);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                if (ConfigUtils.isAutoOpen()) {
                    openMonitor();
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    public void start() throws IOException {
        initPopMenu();
        initSysTray();
        initMenu();
        startFrame();
    }

    public void initPopMenu() {
        popupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("退出");
        popupMenu.add(exitItem);
        // popupMenu.addSeparator();
        exitItem.addActionListener(e -> exit());

    }

    public void initSysTray() throws IOException {
        sysTray = SystemTray.getSystemTray();
        TrayIcon taryIcon = new TrayIcon(Tools.image("/icon/16.png"), "监控", popupMenu);
        taryIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    frame.setExtendedState(JFrame.NORMAL);
                    frame.setVisible(true);
                }

            }
        });
        try {
            sysTray.add(taryIcon);
        } catch (AWTException e) {
            logger.warn("", e);
        }
    }

    public void initMenu() {
        if (ConfigUtils.isAutoOpen()) {
            return;
        }
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu mediaMenu = new JMenu("视频");
        menubar.add(mediaMenu);

        mediaMenu.add(new AbstractAction("监控") {

            @Override
            public void actionPerformed(ActionEvent e) {
                openMonitor();
            }
        });
    }

    public void startFrame() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
        frame.setContentPane(contentPane);

        EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                logger.info("playing");
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(
                                frame,
                                "播放视频失败",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });

        frame.setVisible(true);
    }

    private void openMonitor() {
        logger.info("播放视频呀");
        // JOptionPane.showMessageDialog(frame, "准备播放视频", "提示信息",
        // JOptionPane.INFORMATION_MESSAGE);
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        String ip = Tools.getIp();
        String rtspUrl = String.format("rtsp://admin:admin@%s:554/h264/ch33/main/av_stream", ip);
        frame.setTitle(ip);
        // mediaPlayer.media().play(rtspUrl);
        // mediaPlayer.media().play(rtspUrl, new String[] { "--live-caching
        // 0","--network-caching 300", "--no-audio" });
        mediaPlayer.media().play(rtspUrl, new String[] { "rtsp-tcp", "--network-caching 300", "--no-audio" });
    }

    /**
     * 关闭窗口，清理资源
     */
    private void exit() {
        logger.info("关闭窗口");
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.release();
        }
        System.exit(0);
    }

}