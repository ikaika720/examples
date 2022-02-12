package hoge.exp.jpa_standalone;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class NamedQueryAndPagination {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-standalonPU");

        try {
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();

            // delete
            {
                transaction.begin();
                em.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
                transaction.commit();
            }

            // create
            {
                int numOfRecords = 95;

                String memberFormat = "member%05d";
                String emailFormat = "member%05d@email.com";

                transaction.begin();

                for (int i = 0; i < numOfRecords; i++) {
                    Member3 member = new Member3(i, String.format(memberFormat, i),
                            String.format(emailFormat, i), LocalDate.now());
                    em.persist(member);
                }

                transaction.commit();
            }

            // read
            {
                int pageSize = 10;
                for (int i = 0;; i++) {
                    transaction.begin();

                    TypedQuery<Member3> query =
                            em.createNamedQuery("Member3.findAll", Member3.class)
                                .setMaxResults(pageSize)
                                .setFirstResult(i * pageSize);
                    List<Member3> result = query.getResultList();

                    if (result.size() == 0) {
                        transaction.commit();
                        break;
                    }

                    System.out.println("Page " + i);

                    for (Member3 m : result) {
                        System.out.println(m);
                    }

                    transaction.commit();
                }
            }

            // delete
            {
                transaction.begin();
                em.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
                transaction.commit();
            }
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }
}
