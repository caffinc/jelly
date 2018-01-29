package com.caffinc.jelly.resources;

import com.caffinc.jelly.Jelly;
import com.caffinc.jelly.RemoteCall;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/jelly")
public class JellyResource {
    @POST @Produces("application/json")
    public Response call(RemoteCall call) {
        return Response.ok(Jelly.getInstance().remoteCall(call)).build();
    }

    @GET
    public Response call() {
        return Response.ok("Yo!").build();
    }
}