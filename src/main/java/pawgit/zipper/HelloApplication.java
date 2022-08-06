package pawgit.zipper;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.slf4j.LoggerFactory;
import pawgit.zipper.company.CompanyRepository;

import java.util.Collections;

@ApplicationPath("/api")
public class HelloApplication extends Application {

    private static final EntityManagerFactory emFactory;
    public static CompanyRepository COMPANY_REPOSITORY;

    static {
        emFactory = Persistence.createEntityManagerFactory("zipper", Collections.emptyMap());
        LoggerFactory.getLogger(HelloApplication.class).debug("{} initialized", emFactory.getClass().getSimpleName());
        System.out.printf("%s initialized%n", emFactory.getClass().getSimpleName());
        COMPANY_REPOSITORY = CompanyRepository.getInstance(emFactory.createEntityManager());
        LoggerFactory.getLogger(HelloApplication.class).debug("{} initialized", COMPANY_REPOSITORY.getClass().getSimpleName());
        System.out.printf("%s initialized%n", COMPANY_REPOSITORY.getClass().getSimpleName());
    }

}