package hoge.exp.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("temperature")
public class TemperatureConversion {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Temperature getTemperature(@QueryParam("unit") String unit,
            @QueryParam("temp") double temp) {
        Temperature result = new Temperature();
        switch(unit) {
        case "c":
            result.setC(temp);
            result.setF(temp * (9d / 5d) + 32);
            result.setK(temp + 273.15d);
            break;
        case "f":
            result.setC((temp - 32) * (5d / 9d));
            result.setF(temp);
            result.setK((temp + 459.67d) * (5d / 9d));
            break;
        case "k":
            result.setC(temp - 273.15d);
            result.setF(temp * (9d / 5d) - 459.67d);
            result.setK(temp);
            break;
        default:
            throw new IllegalArgumentException();
        }

        return result;
    }

}
