package com.del.passtransfer.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Created by DodolinEL
 * date: 26.06.2024
 */
public class TrayIconView extends TrayIcon {

    private static final String TITLE = "MyPass";

    private MainFrame owner;
    private MenuItemListener onExitListener = new MenuItemListener();

    public TrayIconView(MainFrame owner) {
        super(findImg(owner), TITLE);
        this.owner = owner;
        init();
    }

    private static Image findImg(MainFrame owner) {
        return Toolkit.getDefaultToolkit().getImage(owner.getClass().getResource("/img/ico_32x32.png"));
    }

    private void init() {
        setImageAutoSize(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    hide();
                }
            }
        });

        PopupMenu pp = new PopupMenu();
        setPopupMenu(pp);
        MenuItem openMI = new MenuItem("Восстановить");
        MenuItem reconnectMI = new MenuItem("Переподключить");
        MenuItem exitMI = new MenuItem("Выход");
        openMI.addActionListener((e) -> hide());
        exitMI.addActionListener(onExitListener);
        reconnectMI.addActionListener(e -> owner.reconnect());

        pp.add(openMI);
        pp.addSeparator();
        pp.add(exitMI);
    }

    public void show() throws AWTException {
        if (SystemTray.isSupported()) {
            owner.setVisible(false);
            SystemTray.getSystemTray().add(this);
            displayMessage(TITLE, "Программа продолжает работу", TrayIcon.MessageType.INFO);
        }
    }

    public void hide() {
        if (SystemTray.isSupported()) {
            owner.setVisible(true);
            owner.setExtendedState(Frame.NORMAL);
            SystemTray.getSystemTray().remove(this);
        }
    }

    public void onExitMenuItemClick(ActionHandler h) {
        onExitListener.setHandler(h);
    }

    private class MenuItemListener implements ActionListener {

        private ActionHandler handler;

        public void setHandler(ActionHandler handler) {
            this.handler = handler;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional.of(handler).ifPresent(ah -> ah.handle(e));
        }

    }

    public interface ActionHandler {

        void handle(Object e);

    }


}
