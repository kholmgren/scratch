package loom.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public class TestClient {

    private static final Logger LOG = LoggerFactory.getLogger(TestClient.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:9090";

    public static final int DEVICE_COUNT = 100;
    public static final int USER_COUNT = 10;

    public static void main(String[] args) throws JsonProcessingException, InterruptedException {
        Random random = new Random();

        List<String> deviceIds = new ArrayList<>();
        for (int i = 0; i < DEVICE_COUNT; i++) {
            deviceIds.add(UUID.randomUUID().toString());
        }

        ThreadFactory factory = Thread.builder().virtual().name("client", 0).factory();
        ExecutorService executor = Executors.newThreadExecutor(factory);

        HttpClient client = HttpClient.newHttpClient();

//        for (int i = 0; i < 1000000; i++) {
        for (int i = 0; i < 10; i++) {
            final int key = i;
            String randomValue = UUID.randomUUID().toString();

            Message m = new Message();
            m.setText(Instant.now().toString());
            m.setUserId(Integer.toString(random.nextInt(USER_COUNT)));
            m.setDeviceId(deviceIds.get(random.nextInt(deviceIds.size())));

            executor.execute(() -> {
                try {
                    writeValue(client, String.valueOf(key), randomValue, m);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

//            executor.execute(() -> readValue(client, String.valueOf(key)));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    private static void writeValue(HttpClient client, String key, String value, Message message) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/write?key=%s&value=%s", BASE_URL, key, value)))
                .method("POST", HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(message)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.info("write value: {}", response.body());
    }

    private static void readValue(HttpClient client, String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/read?key=%s", BASE_URL, key)))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> LOG.info("read value: {}", response))
                .join();
    }
}
