package hoge.exp.jpa_standalone;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class ManyToManyAndLazyLoading {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-standalonPU");

        try {
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();

            transaction.begin();
            em.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE member_member").executeUpdate();
            transaction.commit();

            // create
            {
                transaction.begin();
                Member2 member1 = new Member2(0, "member01", "member01@email.com", LocalDate.now());
                Member2 member2 = new Member2(1, "member02", "member02@email.com", LocalDate.now());
                Member2 member3 = new Member2(2, "member03", "member03@email.com", LocalDate.now());

                em.persist(member1);
                em.persist(member2);
                em.persist(member3);

                member1.getFriends().add(member2);
                member1.getFriends().add(member3);

                member2.getFriends().add(member1);

                member3.getFriends().add(member1);

                em.persist(member1);
                em.persist(member2);
                em.persist(member3);

                transaction.commit();
            }

            // read
            {
                em.clear();
                transaction.begin();
                Member2 member = em.find(Member2.class, 0L);
                System.out.println(member.toString());
                for (Member2 m : member.getFriends()) {
                    System.out.println(m.toString());
                }
                transaction.commit();
            }

            transaction.begin();
            em.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE member_member").executeUpdate();
            transaction.commit();
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }
}
