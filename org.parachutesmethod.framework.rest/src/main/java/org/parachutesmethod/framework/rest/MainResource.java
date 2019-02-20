package org.parachutesmethod.framework.rest;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.parachutesmethod.framework.extraction.ParachuteExtractor;

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
    public Response extractRepository(@ApiParam(name = "url", required = true) String url) {
        try {
            ParachuteExtractor p = new ParachuteExtractor<>(new URL(url));
            java.nio.file.Path projectPath = p.getRepository();
            p.parseParachuteProject(projectPath);


        } catch (IOException | GitAPIException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }
}
