package com.danila.javafxauth.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties properties = new Properties();
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
                properties.load(fileInputStream);

                String url = properties.getProperty("url");
                String username = properties.getProperty("username");
                String password = properties.getProperty("password");
                connection = DriverManager.getConnection(url,  username, password);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
