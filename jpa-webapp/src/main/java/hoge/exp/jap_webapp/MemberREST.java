package hoge.exp.jap_webapp;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("member")
public class MemberREST {
    @EJB
    MemberService ms;

    @GET
    @Path("{id}")
    @Produces("text/html")
    public Response getById(@PathParam("id") long id) {
        Member member = ms.getById(id);
        if (member == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(member.toString()).build();
    }

}
