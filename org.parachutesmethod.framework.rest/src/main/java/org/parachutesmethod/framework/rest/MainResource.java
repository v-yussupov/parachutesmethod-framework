package org.parachutesmethod.framework.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.parachutesmethod.framework.extraction.ParachuteExtractor;
import org.parachutesmethod.framework.extraction.exceptions.LangSupportException;
import org.parachutesmethod.framework.extraction.exceptions.ProjectParsingException;
import org.parachutesmethod.framework.extraction.exceptions.WrongRepositoryException;
import org.parachutesmethod.framework.generation.ParachuteGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;

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

    /**
     * Extracts annotated parachute bundles for a given repository
     *
     * @param url  URL of the repository that contains annotated parachutes
     * @param lang application's programming language
     * @return the path to the folder containing extracted parachutes
     * @throws Exception an exception occurred while extraction
     */
    @POST
    @Path("extract")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response extractParachute(@ApiParam(name = "url", required = true) @FormParam("url") String url,
                                     @ApiParam(name = "lang", required = true) @FormParam("lang") String lang) {
        try {
            ParachuteExtractor p = new ParachuteExtractor<>(new URL(url), lang);

            return Response.ok().entity(p.extract().toString()).build();
        } catch (IOException | GitAPIException | WrongRepositoryException | LangSupportException | ProjectParsingException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Generates parachute deployment bundles for a given cloud service provider
     *
     * @param path     the path to extracted provider-agnostic parachute bundles
     * @param provider the target cloud service provider
     */
    @POST
    @Path("generate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateParachute(@ApiParam(name = "path", required = true) @FormParam("path") String path,
                                      @ApiParam(name = "provider", required = true) @FormParam("provider") String provider) {
        try {
            ParachuteGenerator generator = new ParachuteGenerator(path, provider);
            generator.generate();

            return Response.ok().build();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
