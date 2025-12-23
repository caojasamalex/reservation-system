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
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        RoleEnumeration.valueOf(
                                rs.getString("role")
                        ),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        }finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return user;
    }

    public void insertUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("INSERT INTO users (username, password, full_name, role, created_at) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().toString());
            ps.setObject(5, user.getCreatedAt());
            ps.executeUpdate();
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
            ps.setString(4, user.getRole().toString());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void deleteUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try{
            ReservationRepetitionDao.getInstance().deleteByUser(user, con);
            ps = con.prepareStatement("DELETE FROM users WHERE user_id = ?");
            ps.setInt(1, user.getUserId());

            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }
}
