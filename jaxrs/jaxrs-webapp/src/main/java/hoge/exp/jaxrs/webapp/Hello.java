package hoge.exp.jaxrs.webapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("hello")
public class Hello {
    @GET
    @Produces("text/html")
    public String getHtml() {
        return "<html><head><title>hello</title><body><h1>hello</h1></body></html>";
    }

}
