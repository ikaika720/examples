package hoge.exp.jaxrs.webapp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("webapi")
@ApplicationScoped
public class ServerApplication extends Application {
}
