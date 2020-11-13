//package io.kettil;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//
//import static org.assertj.core.api.Assertions.assertThatCode;
//import static org.mockito.ArgumentMatchers.endsWith;
//import static org.mockito.ArgumentMatchers.eq;
//
//public class MessageSenderTest {
//    private MessageSender subject;
//    private RabbitTemplate rabbitTemplateMock;
//
//    @BeforeEach
//    public void setUp() {
//        this.rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
//        this.subject = new MessageSender(this.rabbitTemplateMock);
//    }
//
//    @Test
//    public void testBroadcast() {
//        assertThatCode(() -> this.subject.broadcast("Test")).doesNotThrowAnyException();
//        Mockito.verify(this.rabbitTemplateMock)
//                .convertAndSend(eq(MessagingConfig.FANOUT_EXCHANGE_NAME), eq(""), eq("Test"));
//    }
//
//    @Test
//    public void testSendError() {
//        assertThatCode(() -> this.subject.sendError("Test Error")).doesNotThrowAnyException();
//        Mockito.verify(this.rabbitTemplateMock)
//                .convertAndSend(eq(MessagingConfig.TOPIC_EXCHANGE_NAME), endsWith("error"),
//                        eq("Test Error"));
//    }
//}