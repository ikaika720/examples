package hoge.exp.jpa_standalone;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

public class SimpleCRUD {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-standalonPU");

        try {
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();

            // create
            {
                transaction.begin();
                Member member = new Member(0, "Andrea", "andrea@email.com", LocalDate.now());
                em.persist(member);
                transaction.commit();
            }

            // read
            {
                transaction.begin();
                Member member = em.find(Member.class, 0L);
                System.out.println(member.toString());
                transaction.commit();
            }

            // update
            {
                transaction.begin();
                Member member = em.find(Member.class, 0L, LockModeType.PESSIMISTIC_WRITE);
                member.setEmail("new_" + member.getEmail());
                em.persist(member);
                transaction.commit();
            }

            // read after update
            {
                transaction.begin();
                Member member = em.find(Member.class, 0L);
                System.out.println(member.toString());
                transaction.commit();
            }

            // delete
            {
                transaction.begin();
                Member member = em.find(Member.class, 0L);
                em.remove(member);
                transaction.commit();
            }
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }
}
