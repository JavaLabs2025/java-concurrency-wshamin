package org.labs.lab1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.labs.Main;

public class Lunch {

    public static AtomicInteger REMAINING_FOOD;
    public static BlockingQueue<Developer> KITCHEN;

    public static void main(String[] args) {
        Properties prop = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Файл application.properties не найден в ресурсах!");
                return;
            }
            prop.load(input);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Проблемы ввода/вывода");
        }

        int devCount = Integer.parseInt(prop.getProperty("devCount", "7"));
        int waiterCount = Integer.parseInt(prop.getProperty("waiterCount", "2"));
        int totalFood = Integer.parseInt(prop.getProperty("totalFood", "1000"));
        String threadPoolTypeStr = prop.getProperty("threadPoolType", "SINGLE");
        ThreadPoolType threadPoolType = ThreadPoolType.valueOf(threadPoolTypeStr);

        Lunch lunch = new Lunch();
        lunch.startLunch(devCount, waiterCount, totalFood, threadPoolType);
    }

    private void startLunch(int devCount, int waiterCount, int totalFood, ThreadPoolType threadPoolType) {
        REMAINING_FOOD = new AtomicInteger(totalFood);
        KITCHEN = new PriorityBlockingQueue<>(devCount);

        ExecutorService executorService = switch (threadPoolType) {
            case FIXED -> Executors.newFixedThreadPool(devCount + waiterCount);
            case CACHED -> Executors.newCachedThreadPool();
            case WORK_STEALING -> Executors.newWorkStealingPool(devCount + waiterCount);
            case SINGLE -> Executors.newSingleThreadExecutor();
        };

        long start = System.currentTimeMillis();

        Waiter[] waiters = new Waiter[waiterCount];
        for (int i = 0; i < waiterCount; i++) {
            waiters[i] = new Waiter(i, KITCHEN);
            executorService.execute(waiters[i]);
        }

        Developer[] devs = getDevs(devCount);
        for (Developer dev : devs) {
            executorService.execute(dev);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.out.println("Ожидание окончания работы было прервано");
        }

        long workingTime = System.currentTimeMillis() - start;
        System.out.println(threadPoolType + " thread pool отработал за " + workingTime + " мс");
    }

    private static Developer[] getDevs(int devCount) {
        Developer[] devs = new Developer[devCount];

        Spoon[] spoons = new Spoon[devCount];
        for (int i = 0; i < devCount; i++) {
            spoons[i] = new Spoon(i);
        }

        for (int i = 0; i < devCount; i++) {
            Spoon left  = spoons[i];
            Spoon right = spoons[(i + 1) % devCount];
            devs[i] = new Developer(i, left, right, KITCHEN);
        }

        return devs;
    }
}
