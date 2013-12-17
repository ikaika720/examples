package hoge.exp.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyClass01 {
    private static final Log log = LogFactory.getLog(MyClass01.class);

    public void execute() {
        log.info("This is MyClass01.");

        if (log.isDebugEnabled()) {
            log.debug("a" + "b" + "c");
        }
    }
}
