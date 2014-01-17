package hoge.exp.jpa.webapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("member")
public class MemberREST {
    private static final String DATE_OF_BIRTH_FORMAT = "yyyy-MM-dd";

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

    @GET
    @Path("new")
    @Produces("text/html")
    public Response create(@QueryParam("id") long id,
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("dateOfBirth") String dateOfBirth) throws ParseException {
        Member member = ms.create(new Member(id, name, email, parseDateOfBirth(dateOfBirth)));
        return Response.ok(member.toString()).build();
    }

    @GET
    @Path("update")
    @Produces("text/html")
    public Response update(@QueryParam("id") long id,
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("dateOfBirth") String dateOfBirth) throws ParseException {
        Member member = ms.update(new Member(id, name, email, parseDateOfBirth(dateOfBirth)));
        return Response.ok(member.toString()).build();
    }

    private Date parseDateOfBirth(String str) throws ParseException {
        return new SimpleDateFormat(DATE_OF_BIRTH_FORMAT).parse(str);
    }
}
