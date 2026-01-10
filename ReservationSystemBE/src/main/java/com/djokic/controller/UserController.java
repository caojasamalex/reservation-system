package com.djokic.controller;

import com.djokic.data.EditUserRequest;
import com.djokic.data.User;
import com.djokic.service.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService = UserService.getInstance();

    @POST
    @Path("/auth")
    public Response login(User credentials) {
        try{
            String token = userService.login(credentials);

            if(token == null){
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid username or password").build();
            }

            Map<String, String> map = Map.of("token", token);
            return Response.ok(map).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/users")
    public Response register(User user) {
        try{
            String token = userService.addUser(user);

            if(token == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid username or password").build();
            }

            Map<String, String> map = Map.of("token", token);
            return Response.status(Response.Status.CREATED).entity(map).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/users/{id}")
    public Response editUser(@PathParam("id") int id, EditUserRequest editUserRequest, @HeaderParam("Authorization") String authHeader) {
        try{
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            User user = userService.editUser(id, editUserRequest, authHeader);
            return Response.status(Response.Status.OK).entity(user).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/users/{id}")
    public Response getUser(@PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        try{
            if(authHeader == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            User user = userService.getUserById(id, authHeader);
            if(user == null){ return Response.status(Response.Status.NOT_FOUND).build(); }
            return Response.ok(user).build();
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
