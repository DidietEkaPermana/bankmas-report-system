package com.bankmas.report.webapi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class StringUtil {
    public static String generateChecksum(String input) throws NoSuchAlgorithmException{
        // Use SHA-256 algorithm
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Pass input string to digest()
        byte[] hash = md.digest(input.getBytes());

        // Convert to hex format 
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
        sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }

    public static String generateFilename() {
        // Generate random string
        String randomString = UUID.randomUUID().toString().replace("-", "");

        // Get current timestamp in milliseconds 
        long timeMilli = System.currentTimeMillis();

        // Concatenate random string and timestamp
        String fileName = randomString + "-" + timeMilli;

        // Example filename: 
        // 4b9c2b4e690a11ed817b0242ac130002_1677445455123.txt

        return fileName;
    }

    public static boolean isNull(String id) {
        if(id == null || id.isEmpty())
            return true;
        return false;
    }
}
