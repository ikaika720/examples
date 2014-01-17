package hoge.exp.jpa.webapp;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Stateless
public class MemberService {
    @PersistenceContext(unitName = "jpa-webappPU")
    private EntityManager em;

    public Member getById(long id) {
        return em.find(Member.class, id);
    }

    public Member create(Member member) {
        em.persist(member);
        return member;
    }

    public Member update(Member member) {
        em.find(Member.class, member.getId(), LockModeType.PESSIMISTIC_WRITE);
        Member m = em.merge(member);
        return m;
    }
}
