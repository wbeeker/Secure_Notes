package com.example.secure_notes.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*
 * Utility class for AES encryption and decryption of note content.
 * 
 */

@Component
public class AesEncryptionUtil {
    /*
     * The encryption algorithm identifier.
     */
    private static final String ALGORITHM = "AES";

    /*
     * The AES secret key specification used for encryption and decryption.
     */
    private SecretKeySpec secretKeySpec;

    /*
     * The secret key string injected from application.properties.
     */
    @Value("${aes.secret}")
    private  String SECRET_KEY;

    /*
     * Initializes the AES encryption key after dependency injection.
     * 
     * @throws IllegalArgumentException if the secret key is not 16, 24, or 32 bytes
     * @throws NullPointerException if SECRET_KEY was not injected (missing config)
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 16, 24, or 32 bytes.");
        }
        this.secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /*
     * Ecrypts plaintext content using AES encryption. Returns a Base64-encoded encrypted
     * string.
     * 
     * @param plainText the plaintext string to encrypt (must not be null)
     * @return Base64-encoded encrypted string safe for storage
     * @throws RuntimeException if encryption fails for any reason (wraps underlying exceptions)
     * @throws NullPointerException if plainText is null
     */
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting note", e);
        }
    }

    /*
     * Decrypts AES-encrypted content back to plaintext.
     * 
     * @param encryptedText the Base64-encoded encrypted string to decrypt (must not be null)
     * @return the original plaintext string
     * @throws RuntimeException if decryption fails for any reason (wraps underlying exceptions)
     * @throws NullPointerException if encryptedText is null
     * @throws IllegalArgumentException if encryptedText is not valid Base64
     */
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting note", e);
        }
    }
}
