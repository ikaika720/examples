package hoge.exp.jpa.mdb.client;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Client {
    private static Logger LOGGER = Logger.getLogger(Client.class.toString());

    private static final String DEFAULT_ACCOUNT_ID = "1";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_REQUEST_QUEUE = "MyReqQueue";
    private static final String DEFAULT_RESPONSE_QUEUE = "MyResQueue";
    private static final String DEFAULT_USERNAME = "user1";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";

    public static void main(String[] args) {
        String accountId = args.length > 0 ? args[0] : DEFAULT_ACCOUNT_ID;
        String amount = args.length > 1 ? args[1] : null;

        // message text: accountId [amount]
        // if accountId only, query account
        // if account and amount, update account
        StringBuffer sb = new StringBuffer();
        sb.append(accountId);
        if (amount != null) {
            sb.append(" ").append(amount);
        }
        String msgTxt = sb.toString();

        String userName = System.getProperty("username", DEFAULT_USERNAME);
        String password = System.getProperty("password", DEFAULT_PASSWORD);

        Context namingCtx = null;

        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            namingCtx = new InitialContext(env);

            String cfString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            ConnectionFactory cf = (ConnectionFactory) namingCtx.lookup(cfString);

            try (JMSContext ctx = cf.createContext(userName, password)){
                // send
                Queue reqQ = ctx.createQueue(System.getProperty("reqQueue", DEFAULT_REQUEST_QUEUE));
                TextMessage req = ctx.createTextMessage(msgTxt);
                ctx.createProducer().send(reqQ, req);

                // receive
                Queue resQ = ctx.createQueue(System.getProperty("resQueue", DEFAULT_RESPONSE_QUEUE));
                Message received = ctx.createConsumer(resQ,
                        String.format("JMSCorrelationID = '%s'", req.getJMSMessageID())).receive(1000L);

                System.out.println(((TextMessage) received).getText());
            } catch (JMSException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (namingCtx != null) {
                try {
                    namingCtx.close();
                } catch (NamingException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

}
