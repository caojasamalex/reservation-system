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
    @Path("/{id}")
    public Response getResourceById(@PathParam("id") int id) {
        try{
            Resource resource = resourceService.getResourceById(id);

            return Response.status(Response.Status.OK).entity(resource).build();
        } catch (Exception e){
            if(e.getMessage().contains("Unauthorized")) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else if(e.getMessage().contains("Forbidden")) {
                return Response.status(Response.Status.FORBIDDEN).build();
            } else {
                return Response.serverError().entity(e.getMessage()).build();
            }
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

    @POST
    public Response createResource(Resource resource, @HeaderParam("Authorization") String authHeader) {
        try{
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            int id = resourceService.addResource(resource, authHeader);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (Exception e){
            if(e.getMessage().contains("Unauthorized")) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else if(e.getMessage().contains("Forbidden")) {
                return Response.status(Response.Status.FORBIDDEN).build();
            } else {
                return Response.serverError().entity(e.getMessage()).build();
            }
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteResource(@PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        try{
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            resourceService.deleteResource(id, authHeader);

            return Response.status(Response.Status.OK).build();
        } catch (Exception e){
            if(e.getMessage().contains("Unauthorized")) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else if(e.getMessage().contains("Forbidden")) {
                return Response.status(Response.Status.FORBIDDEN).build();
            } else {
                return Response.serverError().entity(e.getMessage()).build();
            }
        }
    }
}
