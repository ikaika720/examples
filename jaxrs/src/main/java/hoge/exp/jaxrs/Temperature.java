package hoge.exp.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Temperature {
    private double c;
    private double f;
    private double k;
    public double getC() {
        return c;
    }
    public void setC(double c) {
        this.c = c;
    }
    public double getF() {
        return f;
    }
    public void setF(double f) {
        this.f = f;
    }
    public double getK() {
        return k;
    }
    public void setK(double k) {
        this.k = k;
    }

}
