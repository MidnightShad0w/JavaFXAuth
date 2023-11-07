package com.danila.javafxauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.mindrot.jbcrypt.BCrypt;

public class Utils {
    public static String getUUID() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("wmic", "csproduct", "get", "UUID");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder uuidBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                uuidBuilder.append(line.trim());
            }

            return uuidBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String generateHash(String str) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(str, salt);
    }

    public static boolean checkHash(String str, String hash) {
        return BCrypt.checkpw(str, hash);
    }
}
