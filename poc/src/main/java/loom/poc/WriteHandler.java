package loom.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.dto.Message;
import org.redisson.api.RTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WriteHandler extends AbstractHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WriteHandler.class);
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        assertMethod("POST", exchange);

        Message message = null;
        try {
            message = mapper.readValue(exchange.getRequestBody().readAllBytes(), Message.class);
            RTopic topic = Server.getRedissonClient().getTopic(makeTopic(message));
            topic.publish(message);
            LOG.debug(message.toString());

            final Charset charset = StandardCharsets.UTF_8;
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + charset.name());
            exchange.sendResponseHeaders(200, 0);

            try (OutputStreamWriter writer = new OutputStreamWriter(exchange.getResponseBody(), charset)) {
                writer.write(String.format("Publishing Message(deviceId=%s, userId=%s, text=%s)",
                        message.getDeviceId(), message.getUserId(), message.getText()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            respondWithString(500, "internal error", exchange);
        }
    }

    private String makeTopic(Message message) {
        return message.getUserId() + "." + message.getDeviceId();
    }

}
