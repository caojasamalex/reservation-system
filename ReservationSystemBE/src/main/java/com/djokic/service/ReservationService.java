package com.djokic.service;

import com.djokic.dao.*;
import com.djokic.data.*;
import com.djokic.enumeration.ReservationStatusEnum;
import com.djokic.enumeration.RoleEnumeration;
import com.djokic.util.TokenData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.djokic.util.TokenUtil.authorize;

public class ReservationService {
    private static final ReservationService instance = new ReservationService();

    private ReservationService() {}

    public static ReservationService getInstance() {
        return instance;
    }

    public List<ReservationDTO> getReservationForCurrentUser(String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        try(Connection con = ResourcesManager.getConnection()){
            return ReservationDao.getInstance().getReservationDTOsByUserId(auth.getUserId(), con);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public ReservationDTO getReservation(int id, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        Connection con = null;
        try{
            con = ResourcesManager.getConnection();
            Reservation reservation = ReservationDao.getInstance().getReservationById(id, con);
            if(reservation == null){
                throw new Exception("Reservation not found!");
            }

            boolean isOwner = reservation.getUser().getUserId() == auth.getUserId();
            boolean isAdmin = auth.getRole().name().equals("ADMIN");

            if(!isOwner && !isAdmin) {
                throw new Exception("Forbidden!");
            }

            ReservationRepetition rep = ReservationRepetitionDao.getInstance().getReservationRepetitionByReservationId(reservation.getId(), con);

            ReservationDTO dto;

            if(rep != null){
                dto = new ReservationDTO(reservation, rep.getRepetitionType(), rep.getRepetitionEndDate());
            } else {
                dto = new ReservationDTO(reservation, null, null);
            }

            return dto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ResourcesManager.closeConnection(con);
        }
    }

    public List<ReservationDTO> getAllReservationsByUserId(int id, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        boolean isOwner = auth.getUserId() == id;
        boolean isAdmin = auth.getRole().name().equals("ADMIN");

        if(!isOwner && !isAdmin) {
            throw new Exception("Forbidden!");
        }

        Connection con = null;
        try{
            con = ResourcesManager.getConnection();
            return ReservationDao.getInstance().getReservationDTOsByUserId(id, con);
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public int addReservation(ReservationCreateRequest reservation, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        validateCreateRequest(reservation);

        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            User user = UserDao.getInstance().findUserById(auth.getUserId(), con);
            if (user == null) {
                throw new Exception("User not found!");
            }

            Resource resource = ResourceDao.getInstance().getResource(reservation.getResourceId(), con);
            if (resource == null) {
                throw new Exception("Invalid resource!");
            }


            int overlapping = ReservationDao
                    .getInstance()
                    .countOverlappingReservations(
                            reservation.getDate(),
                            reservation.getStartTime(),
                            reservation.getEndTime(),
                            resource.getId(),
                            -1,
                            con
                    );

            if(overlapping >= resource.getQuantity()) {
                throw new Exception("No vacancies for the specified resource!");
            }

            Reservation newReservation = new Reservation();
            newReservation.setUser(user);
            newReservation.setResource(resource);
            newReservation.setStartTime(reservation.getStartTime());
            newReservation.setEndTime(reservation.getEndTime());
            newReservation.setDate(reservation.getDate());
            newReservation.setStatus(ReservationStatusEnum.ACTIVE);

            int id = ReservationDao.getInstance().insertReservation(newReservation, con);

            newReservation.setId(id);

            if(reservation.getRepetitionType() != null && reservation.getRepetitionEndDate() != null){
                ReservationRepetition rep = new ReservationRepetition(newReservation, reservation.getRepetitionType(), reservation.getRepetitionEndDate());

                ReservationRepetitionDao.getInstance().insertReservationRepetition(rep, con);
            }

            con.commit();
            return id;

        } catch (Exception e) {
            if(con != null) {con.rollback();}
            throw e;
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    private void validateCreateRequest(ReservationCreateRequest req) throws Exception {

        if (req.getDate() == null) {
            throw new Exception("Date is required");
        }

        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new Exception("Start and end time are required");
        }

        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new Exception("Start time must be before end time");
        }

        if (req.getRepetitionType() != null) {
            if (req.getRepetitionEndDate() == null) {
                throw new Exception("Repetition end date is required");
            }

            if (req.getRepetitionEndDate().isBefore(req.getDate())) {
                throw new Exception("Repetition end date must be after start date");
            }
        }
    }


    public void updateReservation(int reservationId, ReservationEditRequest reservation, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);
        Connection con = null;

        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            Reservation oldReservation = ReservationDao.getInstance().getReservationById(reservationId, con);
            if(oldReservation == null) {
                throw new Exception("Reservation not found!");
            }

            boolean isOwner = oldReservation.getUser().getUserId() == auth.getUserId();
            boolean isAdmin = auth.getRole().name().equals("ADMIN");
            if(!isOwner && !isAdmin) {
                throw new Exception("Forbidden!");
            }

            Resource resource = ResourceDao.getInstance().getResource(reservation.getResourceId(), con);
            if (resource == null) {
                throw new Exception("Invalid resource!");
            }

            int overlapping = ReservationDao
                    .getInstance()
                    .countOverlappingReservations(
                            reservation.getDate(),
                            reservation.getStartTime(),
                            reservation.getEndTime(),
                            resource.getId(),
                            reservationId,
                            con
                    );

            if(overlapping >= resource.getQuantity()){
                throw new Exception("No vacancies for the specified resource!");
            }

            oldReservation.setResource(resource);
            oldReservation.setDate(reservation.getDate());
            oldReservation.setStartTime(reservation.getStartTime());
            oldReservation.setEndTime(reservation.getEndTime());
            oldReservation.setStatus(reservation.getStatus());

            ReservationDao.getInstance()
                    .updateReservation(oldReservation, con);

            handleRepetitionUpdate(oldReservation, reservation, con);
            con.commit();
        } catch (SQLException e) {
            if(con != null) {con.rollback();}
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    private void handleRepetitionUpdate(
            Reservation reservation,
            ReservationEditRequest req,
            Connection con
    ) throws SQLException {

        ReservationRepetitionDao dao =
                ReservationRepetitionDao.getInstance();

        ReservationRepetition existing =
                dao.getReservationRepetitionByReservationId(
                        reservation.getId(), con
                );

        if (req.getRepetitionType() == null ||
                req.getRepetitionEndDate() == null) {

            if (existing != null) {
                dao.deleteReservationRepetitionByReservationId(
                        reservation.getId(), con
                );
            }
            return;
        }

        if (existing == null) {
            dao.insertReservationRepetition(
                    new ReservationRepetition(
                            reservation,
                            req.getRepetitionType(),
                            req.getRepetitionEndDate()
                    ),
                    con
            );
        } else {
            existing.setRepetitionType(req.getRepetitionType());
            existing.setRepetitionEndDate(req.getRepetitionEndDate());
            dao.updateReservationRepetition(existing, con);
        }
    }

    public void changeReservationStatus(int reservationId, ReservationStatusEnum status, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);
        
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            Reservation reservation = ReservationDao.getInstance().getReservationById(reservationId, con);
            if (reservation == null) {
                throw new Exception("Reservation not found");
            }

            boolean isOwner = reservation.getUser().getUserId() == auth.getUserId();
            boolean isAdmin = auth.getRole().name().equals("ADMIN");

            if(!isOwner && !isAdmin) {
                throw new Exception("Forbidden!");
            }

            if(reservation.getStatus().equals(status)) { return; }
            else if(reservation.getStatus().equals(ReservationStatusEnum.CANCELED)) {
                int overlapping = ReservationDao.getInstance().countOverlappingReservations(
                        reservation.getDate(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getResource().getId(),
                        reservation.getId(),
                        con
                );

                if (overlapping >= reservation.getResource().getQuantity()) {
                    throw new Exception("No vacancies for the specified resource!");
                }
            }

            ReservationDao.getInstance().changeReservationStatus(reservationId, status, con);

            con.commit();
        } catch (SQLException e) {
            if(con != null) {con.rollback();}
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteReservationById(int reservationId, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);
        
        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            Reservation reservation = ReservationDao.getInstance().getReservationById(reservationId, con);

            if(reservation == null) {
                throw new Exception("Reservation not found!");
            }

            boolean isOwner = reservation.getUser().getUserId() == auth.getUserId();
            boolean isAdmin = auth.getRole().name().equals("ADMIN");
            if(!isOwner && !isAdmin) {
                throw new Exception("Forbidden!");
            }
            ReservationDao.getInstance().deleteReservationById(reservationId, con);
            con.commit();
        } catch (SQLException e) {
            if(con != null) {con.rollback();}
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteReservationsByUserId(int userId, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        boolean isOwner  = auth.getUserId() == userId;
        boolean isAdmin = auth.getRole().name().equals("ADMIN");
        if(!isOwner && !isAdmin) {
            throw new Exception("Forbidden!");
        }

        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            User user = UserDao.getInstance().findUserById(userId, con);
            if (user != null) {
                ReservationDao.getInstance().deleteReservationsByUser(user, con);
            }

            con.commit();
        } catch (SQLException e) {
            if(con != null) {con.rollback();}
            throw new RuntimeException(e);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteReservationsByResourceId(int resourceId, String authHeader) throws Exception {
        TokenData tokenData = authorize(authHeader);

        try(Connection con = ResourcesManager.getConnection();) {
            if (tokenData.getRole() == RoleEnumeration.ADMIN) {
                ReservationDao.getInstance().deleteReservationsByResource(resourceId, con);
            } else {
                ReservationDao.getInstance().deleteReservationsByResourceAndUser(resourceId, tokenData.getUserId(), con);
            }
        }
    }
}
