package io.kettil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Callable;


//~/.jdks/openjdk-15.0.1/bin/java -jar untitled-1.0-SNAPSHOT.jar
@Command(name = "generator", mixinStandardHelpOptions = true, version = "checksum 1.0-SNAPSHOT",
        description = "generates stuff to STDOUT")
class Generator implements Callable<Integer> {
    public static void main(String... args) {
        int exitCode = new CommandLine(new Generator()).execute(args);
        System.exit(exitCode);
    }

    @Option(names = {"-c", "--count"}, description = "Number of sensors to simulate")
    private int count = 1000;

    @Option(names = {"--minRate"}, description = "Shortest delay between readings in seconds")
    private int minRate = 1;

    @Option(names = {"--maxRate"}, description = "Longest delay between readings in seconds")
    private int maxRate = 5;

    @Option(names = {"--minLimit"}, description = "Shortest delay between readings in seconds")
    private int minLimit = 0;

    @Option(names = {"--maxLimit"}, description = "Longest delay between readings in seconds")
    private int maxLimit = 100;

    private Random random = new Random();
    private ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private final PriorityQueue<ItemState> pendingUpdates = new PriorityQueue<>(
            Comparator.comparing(ItemState::getNextUpdate));

    private LocalDateTime next(LocalDateTime now) {
        return now
                .plusSeconds(random.nextInt(maxRate - minRate) + minRate)
                .plusNanos((long) (random.nextDouble() * 1e9));
    }

    private int makeNextValue(int current) {
        var upOrDownOrNot = random.nextInt(3) - 1;
        var v = current + upOrDownOrNot;

        v = Math.max(minLimit, v);
        v = Math.min(maxLimit, v);

        return v;
    }

    private void initPendingUpdates() {
        var now = LocalDateTime.now();
        for (int i = 0; i < count; i++) {
            int value = random.nextInt(maxLimit - minLimit) + minLimit;
            ItemState s = new ItemState(
                    "item:" + i,
                    now,
                    value,
                    next(now));

            pendingUpdates.add(s);
        }
    }

    @Override
    public Integer call() throws Exception {
        initPendingUpdates();

        while (true) {
            LocalDateTime now = LocalDateTime.now();
            ItemState s = pendingUpdates.poll();

            if (s.getNextUpdate().isAfter(now)) {
                pendingUpdates.add(s);
                Thread.sleep(100);
                continue;
            }

            ItemState n = new ItemState(
                    s.getId(),
                    now,
                    makeNextValue(s.getValue()),
                    next(now));

            System.out.println(mapper.writeValueAsString(n));

            pendingUpdates.add(n);
        }
    }
}