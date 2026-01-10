package com.djokic.dao;

import com.djokic.data.*;
import com.djokic.enumeration.RepetitionTypeEnum;
import com.djokic.enumeration.ReservationStatusEnum;
import com.djokic.enumeration.ResourceTypeEnum;
import com.djokic.enumeration.RoleEnumeration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepetitionDao {
    private static final ReservationRepetitionDao instance = new ReservationRepetitionDao();

    private ReservationRepetitionDao() {}

    public static ReservationRepetitionDao getInstance() {
        return instance;
    }

    public ReservationRepetition getReservationRepetitionByReservationId(int id, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservationRepetition reservationRepetition = null;

        try{
            ps = con.prepareStatement("SELECT * FROM reservation_repetition WHERE reservation_id = ?");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if(rs.next()){

                int reservationId = rs.getInt("reservation_id");

                Reservation reservation = ReservationDao.getInstance().getReservationById(reservationId, con);

                reservationRepetition = new ReservationRepetition(
                        reservation,
                        RepetitionTypeEnum.valueOf(rs.getString("repetition_type")),
                        rs.getDate("repetition_end_date").toLocalDate()
                );
            } else {
                return null;
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return reservationRepetition;
    }

    public int insertReservationRepetition(ReservationRepetition reservationRepetition, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try{
            ps = con.prepareStatement("INSERT INTO reservation_repetition (reservation_id, repetition_type, repetition_end_date) VALUES (?, ?, ?)");
            ps.setInt(1, reservationRepetition.getReservation().getId());
            ps.setString(2, reservationRepetition.getRepetitionType().name());
            ps.setDate(3, java.sql.Date.valueOf(reservationRepetition.getRepetitionEndDate()));
            ps.executeUpdate();
            return reservationRepetition.getReservation().getId();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void updateReservationRepetition(ReservationRepetition reservationRepetition, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try{
            ps = con.prepareStatement("UPDATE reservation_repetition SET repetition_type = ?, repetition_end_date = ? WHERE reservation_id = ?");
            ps.setString(1, reservationRepetition.getRepetitionType().name());
            ps.setDate(2, java.sql.Date.valueOf(reservationRepetition.getRepetitionEndDate()));
            ps.setInt(3, reservationRepetition.getReservation().getId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void deleteReservationRepetitionByReservationId(int id, Connection con) throws SQLException {
        PreparedStatement ps = null;

        try{
            ps = con.prepareStatement("DELETE FROM reservation_repetition WHERE reservation_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
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

    public void deleteByResource(int resourceId, Connection con) throws SQLException {
        PreparedStatement  ps = null;

        try{
            ps = con.prepareStatement("DELETE FROM reservation_repetition " +
                    "WHERE reservation_id IN (SELECT reservation_id FROM reservations WHERE resource_id = ?)");

            ps.setInt(1, resourceId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void deleteByResourceAndUser(int resourceId, int userId, Connection con) throws SQLException {
        PreparedStatement  ps = null;

        try{
            ps = con.prepareStatement("DELETE FROM reservation_repetition " +
                    "WHERE reservation_id IN (SELECT reservation_id FROM reservations WHERE resource_id = ? AND user_id = ?)");

            ps.setInt(1, resourceId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }
}
