package org.labs.lab1;

import java.util.concurrent.ThreadLocalRandom;

public class Developer implements Runnable {

    private final int id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final Waiter waiter;
    private int eaten = 0;

    public Developer(int id, Spoon leftSpoon, Spoon rightSpoon, Waiter waiter) {
        this.id = id;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        while (eaten < Lunch.FOOD_PER_DEV) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(20, 60));
            } catch (InterruptedException e) {
                System.out.println("Программиста " + id + " прервали на обсуждении.");
                return;
            }

            Integer dish = waiter.getDish();
            if (dish == null) {
                if (Lunch.REMAINING_FOOD.get() <= 0 && waiter.isKitchenEmpty()) {
                    return;
                }
                continue;
            }

            Spoon first = leftSpoon;
            Spoon second = rightSpoon;
            if (first.id() > second.id()) {
                Spoon tmp = first;
                first = second;
                second = tmp;
            }

            try {
                synchronized (first) {
                    synchronized (second) {
                        eaten++;
                        System.out.println("Программист " + id + " ест блюдо #" + dish);
                        System.out.println("Всего съел: " + eaten + "/" + Lunch.FOOD_PER_DEV);
                        Thread.sleep(ThreadLocalRandom.current().nextInt(60, 140));
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Программиста " + id + " прервали на поедании блюда #" + dish);
                return;
            }
        }

        System.out.println("Программист " + id + " закончил обед. Съел порций: " + eaten);
    }
}
