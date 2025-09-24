package org.labs.lab1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Lunch {

    public static AtomicInteger REMAINING_FOOD = new AtomicInteger(1000);

    public static void main(String[] args) {
        Lunch lab = new Lunch();
        lab.startLunch();
    }

    private void startLunch() {
        int devCount = 7;

        ExecutorService executorService = Executors.newCachedThreadPool();

        Waiter waiter1 = new Waiter(1);
        Waiter waiter2 = new Waiter(2);

        executorService.execute(waiter1);
        executorService.execute(waiter2);

        Developer[] devs = getDevs(devCount, waiter1, waiter2);

        for (Developer dev : devs) {
            executorService.execute(dev);
        }

        executorService.shutdown();
    }

    private static Developer[] getDevs(int devCount, Waiter waiter1, Waiter waiter2) {
        Developer[] devs = new Developer[devCount];

        Spoon leftSpoon = new Spoon(0);

        for (int i = 0; i < devCount; i++) {
            Spoon rightSpoon = new Spoon(i + 1);
            Waiter waiter = (i % 2 == 0) ? waiter1 : waiter2;
            devs[i] = new Developer(i, leftSpoon, rightSpoon, waiter);
            leftSpoon = rightSpoon;
        }

        return devs;
    }
}

