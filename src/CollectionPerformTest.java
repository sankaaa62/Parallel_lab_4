import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CollectionPerformTest {

    public final static int THREAD_POOL_SIZE = 5;

    public static Map<String, Integer> hashTableObject = null;
    public static Map<String, Integer> synchronizedMapObject = null;
    public static Map<String, Integer> concurrentHashMapObject = null;

    public static void main(String[] args) throws InterruptedException {

        hashTableObject = new Hashtable<String, Integer>();
        performTest(hashTableObject);

        synchronizedMapObject = Collections.synchronizedMap(new HashMap<String, Integer>());
        performTest(synchronizedMapObject);

        concurrentHashMapObject = new ConcurrentHashMap<String, Integer>();
        performTest(concurrentHashMapObject);

    }

    public static void performTest(final Map<String, Integer> threads) throws InterruptedException {

        System.out.println("Тестирование для: " + threads.getClass());
        long averageTime = 0;
        for (int i = 0; i < 5; i++) {

            long startTime = System.nanoTime();
            ExecutorService exServer = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            for (int j = 0; j < THREAD_POOL_SIZE; j++) {
                exServer.execute(new Runnable() {
                    @SuppressWarnings("unused")
                    @Override
                    public void run() {

                        for (int i = 0; i < 500000; i++) {
                            Integer randomNumber = (int) Math.ceil(Math.random() * 550000);
                            // Wreate
                            threads.put(String.valueOf(randomNumber), randomNumber);
                            // Reade
                            Integer value = threads.get(String.valueOf(randomNumber));
                        }
                    }
                });
            }

            exServer.shutdown();
            exServer.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            long entTime = System.nanoTime();
            long totalTime = (entTime - startTime) / 1000000L;
            averageTime += totalTime;
            System.out.println("Произведено 500к операций чтения/записи " + totalTime + " ms");
        }
        System.out.println("Для " + threads.getClass() + " среднее время составило " + averageTime / 5 + " ms\n");
    }
}