package loom.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.dto.Message;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.PatternMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReadHandler extends AbstractHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ReadHandler.class);
    ObjectMapper mapper = new ObjectMapper();
    boolean wait = true;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        assertMethod("POST", exchange);
        Message message = mapper.readValue(exchange.getRequestBody().readAllBytes(), Message.class);

        RedissonClient redissonClient = Server.getRedissonClient();
        RPatternTopic patternTopic = redissonClient.getPatternTopic(message.getUserId() + ".*");
        exchange.sendResponseHeaders(200, 0);

        patternTopic.addListener(Message.class, new PatternMessageListener<Message>() {
            @Override
            public void onMessage(CharSequence charSequence, CharSequence charSequence1, Message s) {
                try {
                    if (!message.getDeviceId().equals(s.getDeviceId())) {
                        exchange.getResponseBody().write(mapper.writeValueAsString(s).getBytes());
                    }
                } catch (IOException e) {
                    log.warn("stream closed for {} : {}  ", message.getDeviceId(), message.getUserId());
                    patternTopic.removeAllListeners();
                    exchange.close();
                }

            }
        });
        while (wait) {
            try {
                Thread.sleep(1000);
                exchange.getResponseBody().flush();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.warn("closing handler for {} : {} ", message.getDeviceId(), message.getUserId());
    }
}
