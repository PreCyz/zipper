package pawgit.zipper;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawgit.zipper.company.CompanyRepository;

import java.util.Collections;

@ApplicationPath("/api")
public class HelloApplication extends Application {

    private static final EntityManagerFactory emFactory;
    public static CompanyRepository COMPANY_REPOSITORY;

    static {
        Logger logger = LoggerFactory.getLogger(HelloApplication.class);
        emFactory = Persistence.createEntityManagerFactory("zipper", Collections.emptyMap());
        logger.info("{} initialized", emFactory.getClass().getSimpleName());
        COMPANY_REPOSITORY = CompanyRepository.getInstance(emFactory.createEntityManager());
        logger.info("{} initialized", COMPANY_REPOSITORY.getClass().getSimpleName());
    }

}