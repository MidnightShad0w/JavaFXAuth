package com.danila.javafxauth.dao;

import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.FileInfo;
import com.danila.javafxauth.model.User;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;

public class FileInfoDao {
    public static int setUserFile(FileInfo newFileInfo, User user) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();

        String checkQuery = "SELECT COUNT(*) FROM file WHERE filepath = ? AND user_id = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setString(1, newFileInfo.getFilePath());
        checkStatement.setInt(2, user.getId());
        ResultSet checkResult = checkStatement.executeQuery();
        checkResult.next();
        int rowCount = checkResult.getInt(1);

        if (rowCount > 0) {
            String updateQuery = "UPDATE file SET filehash = ?, savetime = ? WHERE filepath = ? AND user_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, newFileInfo.getFileHash());
            updateStatement.setObject(2, newFileInfo.getSaveTime());
            updateStatement.setString(3, newFileInfo.getFilePath());
            updateStatement.setInt(4, user.getId());
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


    public static FileInfo getUserFileInfo(File file) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT f.* FROM \"user\" u JOIN file f ON u.user_id = f.user_id\n WHERE f.filepath = ? ORDER BY f.savetime DESC LIMIT 1";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, file.getAbsolutePath());
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
