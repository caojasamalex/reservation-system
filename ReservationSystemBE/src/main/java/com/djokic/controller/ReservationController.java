package com.djokic.controller;

import com.djokic.data.Reservation;
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
    @Path("/{id}")
    public Response getReservation(@PathParam("id") int id) {
        try {
            Reservation reservation = reservationService.getReservation(id);
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
    public Response getReservationsByUser(@PathParam("userId") int userId) {
        try {
            List<Reservation> reservations = reservationService.getAllReservationsByUserId(userId);
            return Response.ok(reservations).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response addReservation(Reservation reservation) {
        try {
            int id = reservationService.addReservation(reservation);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") int id, Reservation reservation) {
        try {
            reservationService.updateReservation(id, reservation);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/status")
    public Response changeStatus(@PathParam("id") int id, @QueryParam("status") String status) {
        try {
            ReservationStatusEnum newStatus = ReservationStatusEnum.valueOf(status);
            reservationService.changeReservationStatus(id, newStatus);
            return Response.noContent().build();
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status value").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") int id) {
        try {
            reservationService.deleteReservationById(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/user/{userId}")
    public Response deleteReservationsByUser(@PathParam("userId") int userId) {
        try {
            reservationService.deleteReservationsByUserId(userId);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
