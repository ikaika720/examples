package hoge.exp.jaxrs.client;

import hoge.exp.jaxrs.common.Temperature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class TemperatureConversionClient {

    public static void main(String[] args) {
        String unit = "c";	// c | f | k
        double value = 20.0d;

        Client client = ClientBuilder.newClient();
        Temperature temp = client.target("http://localhost:8080/jaxrs/webapi/temperature")
                .queryParam("unit", unit)
                .queryParam("temp", value)
                .request("application/json")
                .get(Temperature.class);
        System.out.println(temp.getC() + " °C, "
                + temp.getF() + " °F, " + temp.getK() + " K");
    }

}
