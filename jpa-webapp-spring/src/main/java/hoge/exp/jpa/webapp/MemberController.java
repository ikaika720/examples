package hoge.exp.jpa.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {
    @Autowired
    MemberService ms;

    @RequestMapping("/member/{id}")
    @ResponseBody
    public String helloWorld(@PathVariable long id) {
        Member member = ms.getById(id);
        if (member == null) {
            throw new NotFoundException();
        }
        return member.toString();
    }
}
