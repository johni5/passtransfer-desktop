package com.del.passtransfer.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by DodolinEL
 * date: 03.07.2024
 */
public class SecureManager {

    private KeyPair keyPair;

    public void generateNew() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        keyPair = generator.generateKeyPair();
    }

    public String getPublicKey() {
        PublicKey publicKey = keyPair.getPublic();
        return new String(Base64.getEncoder().encode(publicKey.getEncoded()), StandardCharsets.UTF_8);

    }

    public String encodeMsg(String msg) throws Exception {
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(msg));
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }
}

