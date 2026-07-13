package hoge.exp.jpa.mdb;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@ExtendWith(MockitoExtension.class)
public class AccountListenerTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private Queue queue;

    @Mock
    private EntityManager em;

    @Mock
    private TextMessage textMessage;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private TextMessage responseMessage;

    @InjectMocks
    private AccountListener accountListener;

    @Test
    void testOnMessage_QueryAccount() throws Exception {
        // Arrange
        when(textMessage.getText()).thenReturn("100");
        when(textMessage.getJMSMessageID()).thenReturn("MSG-123");

        Account mockAccount = new Account(100L, new BigDecimal("5000.00"));
        when(em.find(eq(Account.class), eq(100L), eq(LockModeType.NONE))).thenReturn(mockAccount);

        when(jmsContext.createTextMessage(anyString())).thenReturn(responseMessage);
        when(jmsContext.createProducer()).thenReturn(jmsProducer);

        // Act
        accountListener.onMessage(textMessage);

        // Assert
        verify(em).find(eq(Account.class), eq(100L), eq(LockModeType.NONE));
        verify(jmsContext).createTextMessage(mockAccount.toString());
        verify(responseMessage).setJMSCorrelationID("MSG-123");
        verify(jmsProducer).send(queue, responseMessage);
    }

    @Test
    void testOnMessage_UpdateAccount() throws Exception {
        // Arrange
        when(textMessage.getText()).thenReturn("100 2000.00");
        when(textMessage.getJMSMessageID()).thenReturn("MSG-124");

        Account mockAccount = new Account(100L, new BigDecimal("5000.00"));
        when(em.find(eq(Account.class), eq(100L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(mockAccount);

        when(jmsContext.createTextMessage(anyString())).thenReturn(responseMessage);
        when(jmsContext.createProducer()).thenReturn(jmsProducer);

        // Act
        accountListener.onMessage(textMessage);

        // Assert
        verify(em).find(eq(Account.class), eq(100L), eq(LockModeType.PESSIMISTIC_WRITE));
        verify(em).persist(mockAccount); // Because balance was updated
        verify(jmsContext).createTextMessage(mockAccount.toString());
        verify(responseMessage).setJMSCorrelationID("MSG-124");
        verify(jmsProducer).send(queue, responseMessage);
    }
}
