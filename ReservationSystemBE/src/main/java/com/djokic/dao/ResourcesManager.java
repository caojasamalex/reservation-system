package com.djokic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourcesManager {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/reservation_system_db" +
                        "?useSSL=false" +
                        "&allowPublicKeyRetrieval=true" +
                        "&serverTimezone=UTC",
                "root",
                "supersecret"
        );
    }


    public static void closeResources(ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

    public static void closeConnection(Connection con) throws Exception {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                throw new Exception("Failed to close database connection.", ex);
            }
        }
    }

    public static void rollbackTransactions(Connection con) throws Exception {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new Exception("Failed to rollback database transactions.", ex);
            }
        }
    }
}
