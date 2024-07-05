package com.del.passtransfer;

import com.del.passtransfer.utils.SecureManager;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */

    private static final String AES_KEY = "JKHJhkHFufe234^$SW^&d&DV&aw6rU%a";

    @Test
    public void firstTest() {

    }


    public static void main(String[] args) {
        try {
            String message = "ffhhvfhjvfdgcjkh6d4tdhuy7tdg";
            byte[] encryptedMessageBytes;
            String encryptedMessage;

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair keyPair = generator.generateKeyPair();
            PublicKey publicKeyOrig = keyPair.getPublic();
            String pub = new String(Base64.getEncoder().encode(publicKeyOrig.getEncoded()), StandardCharsets.UTF_8);
            System.out.println("PUB: " + pub);

            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
//            KeyFactory factory = KeyFactory.getInstance("RSA");
//            byte[] keyBase64 = Base64.getDecoder().decode(pub);
//            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBase64);
//            PublicKey publicKey = factory.generatePublic(spec);
//            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            encryptedMessageBytes = encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
//            encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
//            System.out.println("Encrypt msg: "  + encryptedMessage);

            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKeyOrig);
            encryptedMessageBytes = encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
            System.out.println("Encrypt orig msg: "  + encryptedMessage);

            Cipher decryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            String msgOrig = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
            System.out.println(msgOrig);





        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
