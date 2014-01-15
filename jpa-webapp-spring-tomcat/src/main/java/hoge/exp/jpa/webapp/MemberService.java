package hoge.exp.jpa.webapp;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    @PersistenceContext(unitName = "jpa-webappPU")
    private EntityManager em;

    @Transactional(readOnly = true)
    public Member getById(long id) {
        return em.find(Member.class, id);
    }
}
