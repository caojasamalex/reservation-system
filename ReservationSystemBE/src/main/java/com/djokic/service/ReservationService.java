package com.djokic.service;

import com.djokic.dao.ReservationDao;
import com.djokic.dao.ResourcesManager;
import com.djokic.dao.UserDao;
import com.djokic.data.Reservation;
import com.djokic.data.User;
import com.djokic.enumeration.ReservationStatusEnum;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private static final ReservationService instance = new ReservationService();

    private ReservationService() {}

    public static ReservationService getInstance() {
        return instance;
    }

    public Reservation getReservation(int id) throws Exception {
        Connection con = null;
        try{
            con = ResourcesManager.getConnection();
            return ReservationDao.getInstance().getReservationById(id, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ResourcesManager.closeConnection(con);
        }
    }

    public List<Reservation> getAllReservationsByUserId(int id) throws Exception {
        Connection con = null;
        try{
            con = ResourcesManager.getConnection();
            return ReservationDao.getInstance().getReservationsByUserId(id, con);
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }
    
    public int addReservation(Reservation reservation) throws Exception {
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            return ReservationDao.getInstance().insertReservation(reservation, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void updateReservation(int reservationId, Reservation reservation) throws Exception {
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            ReservationDao.getInstance().updateReservation(reservationId, reservation, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void changeReservationStatus(int reservationId, ReservationStatusEnum status) throws Exception {
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            ReservationDao.getInstance().changeReservationStatus(reservationId, status, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteReservationById(int reservationId) throws Exception {
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            ReservationDao.getInstance().deleteReservationById(reservationId, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteReservationsByUserId(int userId) throws Exception {
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            User user = UserDao.getInstance().findUserById(userId, con);
            if (user != null) {
                ReservationDao.getInstance().deleteReservationsByUser(user, con);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }
}
