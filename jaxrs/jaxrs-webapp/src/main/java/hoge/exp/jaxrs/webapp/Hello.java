package hoge.exp.jaxrs.webapp;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("hello")
@RequestScoped
public class Hello {
    @GET
    @Produces("text/html")
    public String getHtml() {
        return "<html><head><title>hello</title><body><h1>hello</h1></body></html>";
    }

}
