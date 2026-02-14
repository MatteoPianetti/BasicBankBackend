package com.example.demo.model.card;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.security.SecureRandom;

import jakarta.persistence.AttributeConverter;

@Component
public class CardNumberEncryptor implements AttributeConverter<String, String>{
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    
    private final String secretKeyBase64;
    
    public CardNumberEncryptor(
            @Value("${app.encryption.key:a7mbcb5dxApttFUjZOkUOnPlwPP0/ltSDqxb+CSbIoQ=}") 
            String secretKeyBase64) {
        
        if (secretKeyBase64 == null || secretKeyBase64.trim().isEmpty()) {
            throw new IllegalStateException("Encryption key is not configured");
        }
        this.secretKeyBase64 = secretKeyBase64;
    }
    
    private SecretKey getSecretKey() {
        try{
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
            return new SecretKeySpec(decodedKey, "AES");
        }catch(IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 secret key format");
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting card number", e);
        }
    }
    
    private String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), parameterSpec);
        
        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Combina IV + cipherText per storage
        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
        
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    
    private String decrypt(String encryptedData) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        
        // Estrai IV (primi 12 byte)
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);
        
        // Estrai cipherText (restanti byte)
        byte[] cipherText = new byte[decodedData.length - IV_LENGTH_BYTE];
        System.arraycopy(decodedData, IV_LENGTH_BYTE, cipherText, 0, cipherText.length);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), parameterSpec);
        
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, "UTF-8");
    }
}