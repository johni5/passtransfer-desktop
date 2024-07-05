package com.del.passtransfer.view;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by DodolinEL
 * date: 03.07.2024
 */
public class QRViewPanel extends JPanel {

    private JLabel imgLabel;
    private JLabel nameLabel;

    public QRViewPanel() {
        super();
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setPreferredSize(new Dimension(250, 250));
        imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(210, 210));
        imgLabel.setBackground(Color.YELLOW);
        imgLabel.setOpaque(true);
        nameLabel = new JLabel();
        nameLabel.setPreferredSize(new Dimension(200, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(nameLabel);
        add(imgLabel);
        disconnect();
    }

    public void connectServer(String cid, String pubKey) throws WriterException {
        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<String, String> data = Maps.newHashMap();
        data.put("cid", cid);
        data.put("pub", pubKey);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        BitMatrix byteMatrix = qrCodeWriter.encode(json, BarcodeFormat.QR_CODE, 200, 200, hintMap);
        int w = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(w, w, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, w, w);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < w; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIcon ii = new ImageIcon();
        ii.setImage(image);
        imgLabel.setIcon(ii);
        imgLabel.setBackground(Color.ORANGE);
        nameLabel.setText("Сканируйте QR код");
    }


    public void connectApp(String name) {
        imgLabel.setBackground(Color.GREEN);
        nameLabel.setText(name);
    }

    public void disconnect() {
        imgLabel.setBackground(Color.YELLOW);
        nameLabel.setText("Не подключено");
    }


}
