package com.danila.javafxauth;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.ZipFile;
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
    public static void archiveFileAndDeleteSource(String filePath, String entryName, String newContent, String newPassword, Path pathToDelete) throws IOException {
        try {
            Files.delete(pathToDelete);
            if (entryName.endsWith(".zip")) {
                entryName = entryName.substring(0, entryName.length() - 4);
            }
            File tempFile = new File(entryName);
            try (PrintWriter writer = new PrintWriter(tempFile)) {
                writer.write(newContent);
            }
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            String zipName = filePath + ".zip";
            ZipFile zipFile = new ZipFile(zipName, newPassword.toCharArray());
            zipFile.addFile(tempFile, zipParameters);
            zipFile.close();
            if (!tempFile.delete()) {
                throw new IOException("Не удалось удалить файл" + tempFile);
            }
        } catch (ZipException e) {
            throw new ZipException("Не удалось архивировать файл" + e);
        }
    }
    public static void extractArchiveAndDeleteSource(String archivePath, String password) {
        try {
            extractFromArchive(archivePath, password);

            File archiveFile = new File(archivePath);
            if (archiveFile.exists()) {
                if (archiveFile.delete()) {
                    System.out.println("Исходный архив успешно удален.");
                } else {
                    System.out.println("Не удалось удалить исходный архив.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractFromArchive(String archivePath, String password) throws ZipException {
        try {
            ZipFile zipFile = new ZipFile(archivePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }

            String archiveDirectory = new File(archivePath).getParent();

            zipFile.extractAll(archiveDirectory);
            zipFile.close();
        } catch (ZipException e) {
            throw new ZipException("Не удалось извлечь файл из архива: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
