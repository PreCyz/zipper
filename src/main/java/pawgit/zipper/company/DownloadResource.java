package pawgit.zipper.company;

import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawgit.zipper.HelloApplication;
import pawgit.zipper.services.Zipper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Path("/download")
public class DownloadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadResource.class);

    @POST
    @Path(value = "/generate/{limit}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generate(@PathParam("limit") int limit) {
        try (CompanyRepository companyRepository = HelloApplication.COMPANY_REPOSITORY) {
            companyRepository.generate(limit);
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
        return Response.ok().entity("GENERATED").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download() {
        try {
            StreamingOutput streamingOutput = streamingOutput();

            return Response.ok(streamingOutput)
                    .type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=companies.zip")
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private StreamingOutput streamingOutput() {
        return outputStream -> {
            try (CompanyRepository companyRepository = HelloApplication.COMPANY_REPOSITORY;
                 Zipper zipper = new Zipper(outputStream).withEntry())
            {
                int offset = 0;
                int limit = 20000;

                Query query = companyRepository.getCompanySortedByStartDate(offset, limit);
                List<Company> resultList = query.getResultList();

                do {
                    companyRepository.clearSession();
                    LOGGER.info("Processing offset: {}", offset);
                    String output = resultList.stream()
                            .map(Company::toString)
                            .collect(Collectors.joining());

                    output += "\n";

                    zipper.writeAndFlush(output.getBytes(StandardCharsets.UTF_8));
                    LOGGER.info("After write Entity manager is opened: {}", companyRepository.isEntityManagerOpen());

                    offset += limit;
                    query = companyRepository.getCompanySortedByStartDate(offset, limit);
                    resultList = query.getResultList();

                } while (!resultList.isEmpty());

            } catch (Exception ex) {
                LOGGER.error("Error when streaming", ex);
            }
        };
    }


}