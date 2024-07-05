package com.del.passtransfer.actions;


import com.del.passtransfer.utils.SystemEnv;
import com.del.passtransfer.utils.Utils;
import com.del.passtransfer.utils.WSUtils;
import com.del.passtransfer.view.MainFrame;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrameActions implements WindowListener {

    final static private Logger logger = Logger.getLogger(MainFrameActions.class);

    private MainFrame owner;

    public MainFrameActions(MainFrame owner) {
        this.owner = owner;

    }

    @Override
    public void windowOpened(WindowEvent e) {
        logger.info("================================= WINDOW OPENED =================================");
        logger.info("Version: " + Utils.getInfo().getString("version.info"));
        logger.info("Loading system variables...");
        for (SystemEnv value : SystemEnv.values()) {
            logger.info("\t\t" + value.getName() + "=" + value.read());
        }
        logger.info(String.format("WS HOST: %s", WSUtils.getHost()));
        logger.info(WSUtils.isSecure() ? "Secure connection" : "No secure connection");

        logger.info("... success.");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (SystemTray.isSupported()) {
            try {
                owner.moveToTray();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                owner.exit();
            }
        } else {
            logger.warn("System tray not available. Close window");
            owner.exit();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
