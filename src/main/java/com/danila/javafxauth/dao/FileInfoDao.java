package com.danila.javafxauth.dao;

import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.FileInfo;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;

public class FileInfoDao {
    public static int setUserFile(FileInfo newFileInfo) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();

        String checkQuery = "SELECT COUNT(*) FROM file WHERE filepath = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setString(1, newFileInfo.getFilePath());
        ResultSet checkResult = checkStatement.executeQuery();
        checkResult.next();
        int rowCount = checkResult.getInt(1);

        if (rowCount > 0) {
            String updateQuery = "UPDATE file SET filehash = ?, savetime = ? WHERE filepath = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, newFileInfo.getFileHash());
            updateStatement.setObject(2, newFileInfo.getSaveTime());
            updateStatement.setString(3, newFileInfo.getFilePath());
            int rowsUpdated = updateStatement.executeUpdate();
            updateStatement.close();
            return rowsUpdated;
        } else {
            String insertQuery = "INSERT INTO file (filehash, filepath, savetime, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, newFileInfo.getFileHash());
            insertStatement.setString(2, newFileInfo.getFilePath());
            insertStatement.setObject(3, newFileInfo.getSaveTime());
            insertStatement.setObject(4, newFileInfo.getUserId());
            int rowsInserted = insertStatement.executeUpdate();
            insertStatement.close();
            return rowsInserted;
        }
    }


    public static FileInfo getUserFileInfo(String userEmail, File file) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM \"user\" u JOIN file f ON u.user_id = f.user_id\n WHERE u.email = ? AND f.filepath = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userEmail);
        preparedStatement.setString(2, file.getAbsolutePath());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String fileHash = resultSet.getString("filehash");
            LocalDateTime saveTime = resultSet.getTimestamp("savetime").toLocalDateTime();
            String filePath = resultSet.getString("filepath");
            int userId = resultSet.getInt("user_id");
            FileInfo retrievedFileInfo = new FileInfo(fileHash, saveTime, filePath, userId);
            preparedStatement.close();
            return retrievedFileInfo;
        } else {
            preparedStatement.close();
            return null;
        }
    }
}
