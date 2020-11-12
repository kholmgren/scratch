package loom.poc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractHandler implements HttpHandler {

    protected void assertMethod(String expectedMethod, HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase(expectedMethod)){
            String message = String.format("Incorrect method [Expected: %s]", expectedMethod);
            exchange.sendResponseHeaders(405, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();

            throw new IllegalArgumentException();
        }
    }

    protected static Map<String, String> splitQuery(String query) {
        if (query == null) return new LinkedHashMap<>();
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return query_pairs;
    }
    protected void respondWithString(int code, String message, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }
}
