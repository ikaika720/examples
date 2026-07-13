package hoge.exp.jpa.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@MessageDriven(name = "AccountListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/MyReqQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
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

                if (amount != null) {
                    account.addBalance(amount);
                }

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
