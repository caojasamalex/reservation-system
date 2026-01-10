package com.djokic.service;

import com.djokic.dao.ReservationDao;
import com.djokic.dao.ResourceDao;
import com.djokic.dao.ResourcesManager;
import com.djokic.data.Reservation;
import com.djokic.data.Resource;
import com.djokic.enumeration.RoleEnumeration;
import com.djokic.util.TokenData;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.djokic.util.TokenUtil.authorize;

public class ResourceService {
    private static final ResourceService instance = new ResourceService();

    private ResourceService() {}

    public static ResourceService getInstance() {
        return instance;
    }

    public List<Resource> getAllResources() throws SQLException {
        try(Connection con = ResourcesManager.getConnection()){
            return ResourceDao.getInstance().getAllResources(con);
        }
    }

    public Resource getResourceById(int id) throws SQLException {
        try (Connection con = ResourcesManager.getConnection()) {
            Resource resource = ResourceDao.getInstance().getResource(id, con);

            if (resource == null) {
                throw new IllegalArgumentException("Resource not found!");
            }

            return resource;
        }
    }

    public List<String> getAvailableSlots(int id, LocalDate date) throws SQLException {
        List<String> slots = new ArrayList<>();

        try(Connection con = ResourcesManager.getConnection()){
            Resource resource = ResourceDao.getInstance().getResource(id, con);
            if(resource == null){ return slots; }

            List<Reservation> dailyReservations = ReservationDao.getInstance().getReservationsByResourceAndDate(id, date, con);

            LocalTime currentTime = resource.getTimeFrom();
            LocalTime endTime = resource.getTimeTo();

            int slotSize = 30;

            while(!currentTime.plusMinutes(slotSize).isAfter(endTime)){
                LocalTime slotEnd = currentTime.plusMinutes(slotSize);

                long takenCount = countOverlappingInMemory(currentTime, slotEnd, dailyReservations);

                if(takenCount < resource.getQuantity()){
                    long remaining = resource.getQuantity() - takenCount;
                    slots.add(String.format("%s - %s (Vacant: %d)", currentTime, slotEnd, remaining));
                }

                currentTime = slotEnd;
            }
        }

        return slots;
    }

    private long countOverlappingInMemory(LocalTime s1, LocalTime e1, List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> s1.isBefore(r.getEndTime()) && e1.isAfter(r.getStartTime()))
                .count();
    }

    public int addResource(Resource resource, String authHeader) throws Exception {
        if(resource == null){
            throw new IllegalArgumentException("Resource cannot be null!");
        }

        TokenData auth = authorize(authHeader);

        if(!auth.getRole().name().equals("ADMIN")) {
            throw new Exception("Forbidden!");
        }

        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            int generatedId = ResourceDao.getInstance().insertResource(resource, con);

            con.commit();
            return generatedId;
        } catch (SQLException ex) {
            if(con != null) con.rollback();
            throw new RuntimeException(ex);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public void deleteResource(int resourceId, String authHeader) throws Exception {
        if(resourceId <= 0){
            throw new IllegalArgumentException("Invalid resourceId!");
        }

        TokenData auth = authorize(authHeader);

        if(!auth.getRole().equals(RoleEnumeration.ADMIN)) {
            throw new Exception("Forbidden!");
        }

        Connection con = null;
        try {
            con = ResourcesManager.getConnection();
            ReservationDao.getInstance().deleteReservationsByResource(resourceId, con);
            ResourceDao.getInstance().deleteResource(resourceId, con);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }
}
