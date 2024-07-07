package com.del.passtransfer.view;


import com.del.passtransfer.ConnectionListener;
import com.del.passtransfer.actions.MainFrameActions;
import com.del.passtransfer.utils.ConnectionManager;
import com.del.passtransfer.utils.SecureManager;
import com.del.passtransfer.utils.Utils;
import com.del.passtransfer.utils.WSUtils;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.apache.log4j.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by DodolinEL
 * date: 02.07.2019
 */
public class MainFrame extends JFrame implements ConnectionListener {

    final static private Logger logger = Logger.getLogger(MainFrameActions.class);

    private TrayIconView trayIcon;
    private SecureManager sm = new SecureManager();
    private QRViewPanel qrViewPanel;
    private ConnectionManager connectionManager;
    private JMenuItem menuItemConnect;

    public MainFrame() {
        initWindow();
        initMenu();
        pack();
        reconnect();
    }

    public void exit() {
        logger.info("================================= WINDOW CLOSING =================================");
        System.exit(0);
    }

    public void moveToTray() throws AWTException {
        trayIcon.show();
    }

    public void reconnect() {
        menuItemConnect.setEnabled(false);
        connectionManager.connect();
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menuFile = new JMenu("Файл");
        menuBar.add(menuFile);
        JMenuItem menuItemSettings = new JMenuItem("Параметры");
        menuItemConnect = new JMenuItem("Подключение");
        JMenuItem menuItemExit = new JMenuItem("Выход");
        menuItemExit.addActionListener(a -> exit());
        menuItemConnect.addActionListener(a -> reconnect());
        menuItemSettings.addActionListener(ae -> {
        });
        menuFile.add(menuItemConnect);
        menuFile.add(menuItemSettings);
        menuFile.add(new JSeparator());
        menuFile.add(menuItemExit);
    }

    private void initWindow() {
        setLocale(new Locale("RU"));
        setTitle(String.format("Версия %s", Utils.getInfo().getString("version.info")));
        addWindowListener(new MainFrameActions(this));
        setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/img/ico_64x64.png")));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        trayIcon = new TrayIconView(this);
        trayIcon.onExitMenuItemClick(e -> exit());

        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(300, 300));
        qrViewPanel = new QRViewPanel();
        centerPanel.add(qrViewPanel);
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        connectionManager = new ConnectionManager(this);

    }

    private ImageIcon getImage(String path, int w, int h) {
        Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(path));
        return new ImageIcon(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
    }


    private Socket socket;

    private void initSocket(String _cid) {
        try {
            if (socket == null) {
                String url = WSUtils.getHttpUrl();
                logger.info("WS connection to " + url);
                IO.Options options = IO.Options.builder().build();
                socket = IO.socket(url, options).open();
                socket.on(Socket.EVENT_CONNECT, o -> {
                    logger.info("WS connected ");
                    menuItemConnect.setEnabled(true);
                    try {
                        sm.generateNew();
                        String publicKey = sm.getPublicKey();
                        logger.info("CID: " + _cid);
                        logger.info("KEY: " + publicKey);
                        qrViewPanel.connectServer(_cid, publicKey);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }).on(Socket.EVENT_CONNECT_ERROR, o -> {
                    logger.warn("WS connection error");
                    disconnected();
                }).on(Socket.EVENT_DISCONNECT, o -> {
                    logger.info("WS disconnected ");
                    disconnected();

                }).on("notify", o -> {
                    logger.info("WS notify");
                    if (o != null && o.length > 0) {
                        logger.info(o[0]);
                        notyfyApp(o[0].toString());
                    }
                }).on("pass transfer", o -> {
                    logger.info("WS pass");
                    if (o != null && o.length > 0) {
                        String msg = o[0].toString().replace("\n", "");
                        logger.info(msg);
                        message(msg);
                    }
                }).emit("register", _cid);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void connected(String cid) {
        logger.info("Registered on server " + cid);
        initSocket(cid);
    }

    @Override
    public void notyfyApp(String name) {
        qrViewPanel.connectApp(name);
        playSound();
    }

    @Override
    public void message(String msg) {
        if (msg != null) {
            try {
                String v = sm.encodeMsg(msg);
                StringSelection stringSelection = new StringSelection(v);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                playSound();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void disconnected() {
        menuItemConnect.setEnabled(true);
        qrViewPanel.disconnect();
        socket = null;
        reconnect();
    }

    @Override
    public void error(String msg) {
        menuItemConnect.setEnabled(true);
        qrViewPanel.disconnect();
        logger.error("Connection error: " + msg);
        socket = null;
    }

    public void playSound() {
        try {
            URL f = getClass().getResource("/mp3/pass.wav");
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
