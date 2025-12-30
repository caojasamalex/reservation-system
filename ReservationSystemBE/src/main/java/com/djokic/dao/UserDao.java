package com.djokic.dao;

import com.djokic.data.User;
import com.djokic.enumeration.RoleEnumeration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final UserDao instance = new UserDao();

    private UserDao() {}

    public static UserDao getInstance() {
        return instance;
    }

    public User findUserById(int id, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try{
            ps = con.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                java.time.LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        RoleEnumeration.valueOf(rs.getString("role")),
                        createdAt
                );
            }
        }finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return user;
    }


    public User findByUsername(String username, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try{
            ps = con.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if(rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                java.time.LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        RoleEnumeration.valueOf(rs.getString("role")),
                        createdAt
                );
            }
        }finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return user;
    }

    public int insertUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("INSERT INTO users (username, password, full_name, role, created_at) VALUES (?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            if (user.getCreatedAt() != null) {
                ps.setObject(5, user.getCreatedAt());
            } else {
                ps.setObject(5, java.time.LocalDateTime.now());
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void updateUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("UPDATE users SET username = ?, password = ?, full_name = ?, role = ? WHERE user_id = ?");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void deleteUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try{
            ReservationDao.getInstance().deleteReservationsByUser(user, con);
            ps = con.prepareStatement("DELETE FROM users WHERE user_id = ?");
            ps.setInt(1, user.getUserId());

            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }
}
