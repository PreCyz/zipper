package pawgit.zipper;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CompanyRepository implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyRepository.class);
    private static final int BATCH_SIZE = 1000;

    private final EntityManager entityManager;
    private static final EntityManagerFactory emFactory;
    static {
        emFactory = Persistence.createEntityManagerFactory("zipper", Collections.emptyMap());
    }

    private static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public CompanyRepository() {
        this.entityManager = getEntityManager();
    }

    public void generate(int limit) {
        List<Company> companies = Stream.generate(() -> {
                    Company f = new Company();
                    f.setName(UUID.randomUUID().toString());
                    f.setBoardMembers(new Random().nextInt(10));
                    f.setStartDateTime(LocalDateTime.now().plusDays(new Random().nextInt(100)));
                    return f;
                }).limit(limit)
                .collect(toList());

        entityManager.getTransaction().begin();
        if (limit > BATCH_SIZE) {
            int counter = 0;
            for (Company c : companies) {
                if (counter > 0 && counter % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    LOGGER.debug("Entity manager flushed and cleared.");
                }
                counter++;
                entityManager.persist(c);
            }

        } else {
            companies.forEach(entityManager::persist);
        }
    }

    public Query getCompanySortedByStartDate(int offset, int limit) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> criteria = criteriaBuilder.createQuery(Company.class);
        Root<Company> r = criteria.from(Company.class);
        criteria.orderBy(criteriaBuilder.desc(r.get("startDateTime")));

        CriteriaQuery<Company> select = criteria.select(r);

        return entityManager.createQuery(select).setFirstResult(offset).setMaxResults(limit);
    }

    @Override
    public void close() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
        entityManager.clear();
    }

    public static boolean isEntityManagerOpen() {
        return getEntityManager() != null && getEntityManager().isOpen();
    }
}
