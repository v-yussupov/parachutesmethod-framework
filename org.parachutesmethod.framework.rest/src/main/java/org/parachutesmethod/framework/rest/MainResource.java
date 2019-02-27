package org.parachutesmethod.framework.rest;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedLanguageException;
import org.parachutesmethod.framework.extraction.exceptions.NotSupportedRepositoryTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Api(value = "mainresource", description = "Sample description")
public class MainResource {
    private static Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get items list", notes = "Returns items list.")
    public String index() {
        return "api";
    }

    @POST
    @Path("extract")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response extractParachute(@ApiParam(name = "url", required = true) @FormParam("url") String url,
                                     @ApiParam(name = "lang", required = true) @FormParam("lang") String lang) {
        try {
            ParachuteExtractor p = new ParachuteExtractor<>(new URL(url), lang);
            p.extractParachutes();
        } catch (IOException | GitAPIException | NotSupportedRepositoryTypeException | NotSupportedLanguageException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("generate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateParachute() {
        return Response.ok().build();
    }
}
