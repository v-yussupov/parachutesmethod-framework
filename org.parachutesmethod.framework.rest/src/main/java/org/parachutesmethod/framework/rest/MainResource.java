package org.parachutesmethod.framework.rest;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.parachutesmethod.framework.extraction.ParachuteExtractor;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Api(value = "mainresource", description = "Sample description")
public class MainResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get items list", notes = "Returns items list.")
    public String index() {
        return "api";
    }

    @POST
    @Path("extract")
    @Produces(MediaType.TEXT_PLAIN)
    public Response extractRepository(String url) {
        try {
            ParachuteExtractor p = new ParachuteExtractor();
            p.downloadGitHubRepository(url);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

}
