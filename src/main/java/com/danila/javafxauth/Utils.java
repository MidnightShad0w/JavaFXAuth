package com.danila.javafxauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    // Метод для получения UUID
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
}
