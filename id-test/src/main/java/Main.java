import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main
 *
 * @author Jun.An3
 * @date 2022/08/24
 */
@Slf4j
public class Main {

    public static AtomicInteger num = new AtomicInteger(0);

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            1,
            3,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args) {
        log.info(String.valueOf(3 | 3));
        //00000001
        //00000001
        //00000001
        log.info(String.valueOf(1 | 4));
        //00000001
        //00000100
        //00000101
        log.info(String.valueOf(3 | 6));
        //00000011
        //00000110
        //00000111
        log.info(String.valueOf((1 & 0) == 0));
        //00000001
        //00000000
        //00000000
        print("1", "2", "3");

        testThreadPoolPolicy();
    }

    public static void print(String... args) {
        for (String arg : args) {
            log.info(arg);
        }
    }

    public static void test() throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 1000000000; i++) {
                num.getAndAdd(1);
            }
            System.out.println("===");
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        Thread.sleep(1000);
        System.out.println("num = " + num);
    }

    public static void testThreadPoolPolicy() {

        AtomicInteger auto = new AtomicInteger(0);
        for (int i = 0; i < 50; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(auto.incrementAndGet() + "，" + Thread.currentThread().getName() + "，" + System.nanoTime());
                    try {
                        if (!"main".equalsIgnoreCase(Thread.currentThread().getName())) {
                            Thread.sleep(3);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
