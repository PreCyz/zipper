package pawgit.zipper;

import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Path("/download")
public class HelloResource {

    @POST
    @Path(value = "/generate/{limit}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generate(@PathParam("limit") int limit) {
        try (CompanyRepository companyRepository = new CompanyRepository()) {
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
        return os -> {
            try (CompanyRepository companyRepository = new CompanyRepository();
                 Zipper zipper = new Zipper(os).createEntry()) {

                int offset = 0;
                int limit = 20000;

                Query query = companyRepository.getCompanySortedByStartDate(offset, limit);
                List<Company> resultList = query.getResultList();

                do {
                    System.out.printf("Processing offset: %d%n", offset);
                    String output = resultList.stream()
                            .map(Company::toString)
                            .collect(Collectors.joining());

                    output += "\n";
                    byte[] encode = Base64.getEncoder().encode(output.getBytes(StandardCharsets.UTF_8));
                    zipper.writeAndFlush(encode);
                    System.out.printf("After write Entity manager is opened: %b%n", CompanyRepository.isEntityManagerOpen());

                    offset += limit;
                    query = companyRepository.getCompanySortedByStartDate(offset, limit);
                    resultList = query.getResultList();

                } while (!resultList.isEmpty());

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        };
    }
}