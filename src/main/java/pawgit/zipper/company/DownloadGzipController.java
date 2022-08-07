package pawgit.zipper.company;

import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawgit.zipper.HelloApplication;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

@Path("/gzip")
public class DownloadGzipController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadGzipController.class);

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
                 OutputStream b64os = new Base64OutputStream(outputStream);
                 GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {

                LOGGER.info("GZIPOutputStream is used in the streaming.");

                int offset = 0;
                int limit = 20000;

                Query query = companyRepository.getCompanySortedByStartDate(offset, limit);
                List<Company> resultList = query.getResultList();

                do {
                    LOGGER.info("Processing offset: {}", offset);
                    String output = resultList.stream()
                            .map(Company::toString)
                            .collect(Collectors.joining());

                    gzip.write(output.getBytes(StandardCharsets.UTF_8));
                    gzip.flush();
                    LOGGER.info("After write Entity manager is opened: {}", companyRepository.isEntityManagerOpen());

                    offset += limit;
                    query = companyRepository.getCompanySortedByStartDate(offset, limit);
                    resultList = query.getResultList();

                } while (!resultList.isEmpty());

            } catch (Exception ex) {
                LOGGER.error("Error when streaming", ex);
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        };
    }


}