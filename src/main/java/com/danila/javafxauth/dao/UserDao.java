package com.danila.javafxauth.dao;

import com.danila.javafxauth.database.DatabaseConnection;
import com.danila.javafxauth.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public static User getUser(User user) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getPassword());
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Извлекаем данные из ResultSet
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            Object uuidObject = resultSet.getObject("uuid");
            String uuid = uuidObject.toString();
//            String currentUUID = Utils.getUUID();
//            if (user.getUuid().equals(currentUUID)) {
//                User retrievedUser = new User(email, password, uuid);
//                return retrievedUser;
//            } else {
//                return null;
//            }
            User retrievedUser = new User(email, password, uuid);
            return retrievedUser;
        } else {
            return null; // Если пользователь не найден
        }
    }
    public static int createUser(User user) throws SQLException {
        // Создаем подключение к бд
        Connection connection = DatabaseConnection.getConnection();
        // SQL-запрос для вставки данных в таблицу
        String query = "INSERT INTO \"user\" (name, phone, email, password, uuid) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPhone());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, user.getUuid());

        // Выполнение запроса на вставку
        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();
        return rowsInserted;
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
