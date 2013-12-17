package hoge.exp.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class App {
    private static final Log log = LogFactory.getLog(App.class);

    public static void main(String[] args) {
        log.info("helo");
        try {
            Integer.parseInt("xxx");
        } catch (NumberFormatException e) {
            log.error("Error!", e);
        }

        new MyClass01().execute();
    }
}
