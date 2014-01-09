package hoge.exp.jap_webapp;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MemberService {
    @PersistenceContext(unitName = "jpa-webappPU")
    private EntityManager em;

    public Member getById(long id) {
        return em.find(Member.class, id);
    }
}
