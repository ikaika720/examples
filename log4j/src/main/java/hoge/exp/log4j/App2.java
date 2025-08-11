package hoge.exp.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App2 {
    private static final Logger logger = LogManager.getLogger(App2.class);

    public static void main(String[] args) {
        logger.info("hello");
        logger.debug("debug!");
        logger.debug(() -> "debug!!");

        logger.debug(() -> {
            System.out.println("XXXXXX");
            return "debug!!!";
        });

        try {
            Integer.parseInt("xxx");
        } catch (NumberFormatException e) {
            logger.error("Error!", e);
        }

        new MyClass01().execute();
    }
}