package hoge.exp.jpa.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@MessageDriven(name = "AccountListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/MyReqQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@ApplicationScoped
public class AccountListener implements MessageListener {
    private static Logger LOGGER = Logger.getLogger(AccountListener.class.toString());

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/MyResQueue")
    private Queue queue;

    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;

    public void onMessage(Message message) {
        try {
            String text = ((TextMessage) message).getText();
            LOGGER.info(text);

            // message text: accountId [amount]
            String[] splitted = text.split(" ");
            long accountId = Long.parseLong(splitted[0]);
            String amount = splitted.length > 1 ? splitted[1] : null;

            Account account = em.find(Account.class, accountId,
                    (amount == null ? LockModeType.NONE : LockModeType.PESSIMISTIC_WRITE));
            String resTxt = "";
            if (account != null) {
                LOGGER.info(account.toString());

                if (amount != null) em.persist(account.addBalance(amount));

                resTxt = account.toString();
            }

            TextMessage res = context.createTextMessage(resTxt);
            res.setJMSCorrelationID(message.getJMSMessageID());
            context.createProducer().send(queue, res);
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
