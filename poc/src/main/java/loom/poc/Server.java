package loom.poc;

import com.sun.net.httpserver.HttpServer;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 9090;
    private static RedissonClient redisson;
    private static RMap<String, String> data;

    public static void main(String[] args) throws IOException {
        initRedis();

        ThreadFactory factory = Thread.builder().virtual().name("worker", 0).factory();
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
            server.createContext("/", new TestHandler());
            server.createContext("/read", new ReadHandler());
            server.createContext("/write", new WriteHandler());
            server.setExecutor(Executors.newThreadExecutor(factory));
            server.start();
            log.info("Started at port {}", PORT);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception caught:" + e);
        }
    }

    private static void initRedis() throws IOException {
        Config config = Config.fromYAML(Server.class.getResource("/redis-default.yaml"));
        redisson = Redisson.create(config);
        data = redisson.getMap("data");
    }

    public static RMap<String, String> getData() {
        return data;
    }

    public static RedissonClient getRedissonClient() {
        return redisson;
    }
}
