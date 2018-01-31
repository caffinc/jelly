package com.caffinc.jelly.resources;

import com.caffinc.jelly.core.Jelly;
import com.caffinc.jelly.RemoteCall;
import com.caffinc.jelly.core.SliceInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/jelly")
public class JellyResource {
    @POST @Consumes("application/json") @Produces("application/json")
    public Response call(RemoteCall call) {
        return Response.ok(Jelly.call(call)).build();
    }

    @Path("/addslice") @POST @Consumes("application/json") @Produces("application/json")
    public Response addSlice(SliceInfo sliceInfo) {
        Jelly.addSlice(sliceInfo);
        return Response.ok().build();
    }
}
