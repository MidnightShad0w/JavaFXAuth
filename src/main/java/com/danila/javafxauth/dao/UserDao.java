package com.danila.javafxauth.dao;

import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDao {

    public static User getUser(User user) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getPassword());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            Object uuidObject = resultSet.getObject("uuid");
            String uuid = uuidObject.toString();
            User retrievedUser = new User(email, password, uuid);
            preparedStatement.close();
            return retrievedUser;
        } else {
            preparedStatement.close();
            return null;
        }
    }
    public static User getUserByEmail(String checkingEmail) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM \"user\" WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, checkingEmail);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String phone = resultSet.getString("phone");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            Object uuidObject = resultSet.getObject("uuid");
            String uuid = uuidObject.toString();
            String message = resultSet.getString("message");
            Timestamp blockingTimeTimestamp = resultSet.getTimestamp("blockingTime");
            String credentials = resultSet.getString("credentials");
            LocalDateTime blockingTime = blockingTimeTimestamp != null ? blockingTimeTimestamp.toLocalDateTime() : null;
            User retrievedUser = new User(name,phone, email, password, uuid, message, blockingTime, credentials);
            preparedStatement.close();
            return retrievedUser;
        } else {
            preparedStatement.close();
            return null;
        }
    }
    public static int createUser(User user) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "INSERT INTO \"user\" (name, phone, email, password, uuid, credentials) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPhone());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, user.getUuid());
        preparedStatement.setString(6, user.getCredentials());

        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();
        return rowsInserted;
    }

    public static void updateUserMessage(User user, String message) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "UPDATE \"user\" SET message = ? WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, message);
        preparedStatement.setString(2, user.getEmail());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static void updateUserBlockingTime(User user, LocalDateTime currentTime) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "UPDATE \"user\" SET blockingtime = ? WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, currentTime);
        preparedStatement.setString(2, user.getEmail());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static boolean checkUserEmail(String email) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT COUNT(*) FROM \"user\" WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        preparedStatement.close();
        return count == 0;
    }
}
