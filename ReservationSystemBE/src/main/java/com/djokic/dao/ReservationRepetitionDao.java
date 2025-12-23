package com.djokic.dao;

import com.djokic.data.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservationRepetitionDao {
    private static final ReservationRepetitionDao instance = new ReservationRepetitionDao();

    private ReservationRepetitionDao() {}

    public static ReservationRepetitionDao getInstance() {
        return instance;
    }

    public void deleteByUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(
                "DELETE FROM reservation_repetition " +
                "WHERE reservation_id IN (SELECT reservation_id FROM reservations WHERE user_id = ?)"
            );
            ps.setInt(1, user.getUserId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }
}
