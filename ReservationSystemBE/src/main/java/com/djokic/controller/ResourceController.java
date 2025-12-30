package com.djokic.controller;

import com.djokic.data.Resource;
import com.djokic.service.ResourceService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/api/resources")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceController {

    private final ResourceService resourceService = ResourceService.getInstance();

    @GET
    public Response getAllResources() {
        try{
            List<Resource> resources = resourceService.getAllResources();
            return Response.status(Response.Status.OK).entity(resources).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/slots")
    public Response getAllSlots(@PathParam("id") int id, @QueryParam("date") String dateStr) {
        try{
            LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : LocalDate.now();

            List<String> slots = resourceService.getAvailableSlots(id, date);

            if(slots.isEmpty()) {
                return Response.status(Response.Status.OK).entity("No vacant slots for the chosen date!").build();
            }

            return Response.status(Response.Status.OK).entity(slots).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
