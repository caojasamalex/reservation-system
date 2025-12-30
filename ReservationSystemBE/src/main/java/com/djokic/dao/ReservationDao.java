package com.djokic.dao;

import com.djokic.data.Reservation;
import com.djokic.data.Resource;
import com.djokic.data.User;
import com.djokic.enumeration.ReservationStatusEnum;
import com.djokic.enumeration.ResourceTypeEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReservationDao {
    private static final ReservationDao instance = new ReservationDao();

    private ReservationDao() {}

    public static ReservationDao getInstance() {
        return instance;
    }

    public Reservation getReservationById(int id, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Reservation reservation = null;

        String sql = "SELECT r.*, res.resource_name, res.resource_type, res.time_from, res.time_to, res.quantity as res_qty " +
                        "FROM reservations r " +
                        "JOIN resources res ON r.resource_id = res.resource_id "
                + "WHERE r.reservation_id = ? ";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                User user = UserDao.getInstance().findUserById(rs.getInt("user_id"), con);

                Resource resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        ResourceTypeEnum.valueOf(rs.getString("resource_type")),
                        rs.getTime("time_from").toLocalTime(),
                        rs.getTime("time_to").toLocalTime(),
                        rs.getInt("res_qty")
                );

                reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        user,
                        resource,
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        ReservationStatusEnum.valueOf(rs.getString("status"))
                );
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
        return reservation;
    }

    public List<Reservation> getReservationsByUserId(int userId, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT r.*, res.resource_name, res.resource_type, res.time_from, res.time_to, res.quantity as res_qty " +
                "FROM reservations r " +
                "JOIN resources res ON r.resource_id = res.resource_id " +
                "WHERE r.user_id = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            User user = UserDao.getInstance().findUserById(userId, con);

            while (rs.next()) {
                Resource resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        ResourceTypeEnum.valueOf(rs.getString("resource_type")),
                        rs.getTime("time_from").toLocalTime(),
                        rs.getTime("time_to").toLocalTime(),
                        rs.getInt("res_qty")
                );

                reservations.add(new Reservation(
                        rs.getInt("reservation_id"),
                        user,
                        resource,
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        ReservationStatusEnum.valueOf(rs.getString("status"))
                ));
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return reservations;
    }

    public int insertReservation(Reservation reservation, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(
                    "INSERT INTO reservations (user_id, resource_id, date, start_time, end_time, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, reservation.getUser().getUserId());
            ps.setInt(2, reservation.getResource().getId());
            ps.setDate(3, java.sql.Date.valueOf(reservation.getDate()));
            ps.setTime(4, java.sql.Time.valueOf(reservation.getStartTime()));
            ps.setTime(5, java.sql.Time.valueOf(reservation.getEndTime()));
            ps.setString(6, reservation.getStatus().name());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(reservation.getCreatedAt()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating reservation failed, no ID obtained.");
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void updateReservation(int reservationId, Reservation reservation, Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(
                    "UPDATE reservations SET user_id = ?, resource_id = ?, date = ?, start_time = ?, end_time = ?, status = ? WHERE reservation_id = ?"
            );
            ps.setInt(1, reservation.getUser().getUserId());
            ps.setInt(2, reservation.getResource().getId());
            ps.setDate(3, java.sql.Date.valueOf(reservation.getDate()));
            ps.setTime(4, java.sql.Time.valueOf(reservation.getStartTime()));
            ps.setTime(5, java.sql.Time.valueOf(reservation.getEndTime()));
            ps.setString(6, reservation.getStatus().name());
            ps.setInt(7, reservationId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void deleteReservationById(int reservationId, Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ReservationRepetitionDao.getInstance().deleteReservationRepetitionByReservationId(reservationId, con);

            ps = con.prepareStatement("DELETE FROM reservations WHERE reservation_id = ?");
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void deleteReservationsByUser(User user, Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ReservationRepetitionDao.getInstance().deleteByUser(user, con);

            ps = con.prepareStatement("DELETE FROM reservations WHERE user_id = ?");
            ps.setInt(1, user.getUserId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public void changeReservationStatus(int reservationId, ReservationStatusEnum status, Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE reservations SET status = ? WHERE reservation_id = ?");
            ps.setString(1, status.name());
            ps.setInt(2, reservationId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(null, ps);
        }
    }

    public int countOverlappingReservations(
            LocalDate date,
            LocalTime start,
            LocalTime end,
            int resourceId,
            int excludeReservationId,
            Connection con
    ) throws SQLException {

        String sql = """
        SELECT COUNT(*)
        FROM reservations
        WHERE status = 'ACTIVE'
          AND resource_id = ?
          AND reservation_id != ?
          AND date = ?
          AND NOT (end_time <= ? OR start_time >= ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, resourceId);
            ps.setInt(2, excludeReservationId);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setTime(4, java.sql.Time.valueOf(start));
            ps.setTime(5, java.sql.Time.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
