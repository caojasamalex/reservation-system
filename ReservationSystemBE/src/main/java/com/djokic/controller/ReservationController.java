package com.djokic.controller;

import com.djokic.data.Reservation;
import com.djokic.data.ReservationCreateRequest;
import com.djokic.data.ReservationDTO;
import com.djokic.data.ReservationEditRequest;
import com.djokic.enumeration.ReservationStatusEnum;
import com.djokic.service.ReservationService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {

    private final ReservationService reservationService = ReservationService.getInstance();

    @GET
    @Path("/my-reservations")
    public Response getReservationForCurrentUser(@HeaderParam("Authorization") String authHeader) {
        try {
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            List<ReservationDTO> reservations = reservationService.getReservationForCurrentUser(authHeader);
            return Response.ok(reservations).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getReservation(@PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        try {
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            ReservationDTO reservation = reservationService.getReservation(id, authHeader);
            if (reservation == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(reservation).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getReservationsByUser(@PathParam("userId") int userId, @HeaderParam("Authorization") String authHeader) throws Exception {
        try {
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            List<ReservationDTO> reservations = reservationService.getAllReservationsByUserId(userId, authHeader);
            return Response.ok(reservations).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response addReservation(ReservationCreateRequest reservation, @HeaderParam("Authorization") String authHeader) {
        try {
            int id = reservationService.addReservation(reservation,  authHeader);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") int id, ReservationEditRequest reservation, @HeaderParam("Authorization") String authHeader) {
        try {
            reservationService.updateReservation(id, reservation, authHeader);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    public Response changeStatus(@PathParam("id") int id, @QueryParam("status") String status, @HeaderParam("Authorization") String authHeader) {
        try {
            ReservationStatusEnum newStatus = ReservationStatusEnum.valueOf(status);
            reservationService.changeReservationStatus(id, newStatus, authHeader);
            return Response.noContent().build();
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status value").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        try {
            reservationService.deleteReservationById(id,  authHeader);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/user/{userId}")
    public Response deleteReservationsByUser(@PathParam("userId") int userId, @HeaderParam("Authorization") String authHeader) {
        try {
            reservationService.deleteReservationsByUserId(userId, authHeader);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/resource/{resourceId}")
    public Response deleteReservationsByResource(@PathParam("resourceId") int resourceId, @HeaderParam("Authorization") String authHeader) {
        try {
            reservationService.deleteReservationsByResourceId(resourceId, authHeader);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
