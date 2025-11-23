package hoge.exp.jaxrs.webapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("hello")
public class Hello {
    @GET
    @Produces("text/html")
    public String getHtml() {
        return "<html><head><title>hello</title><body><h1>hello</h1></body></html>";
    }

}
