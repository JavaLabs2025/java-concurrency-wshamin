package org.labs.lab1;

import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class Developer implements Comparable<Developer>, Runnable {

    private final int id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final Queue<Developer> hungryDevs;
    private int eaten = 0;

    public Developer(int id, Spoon leftSpoon, Spoon rightSpoon, Queue<Developer> hungryDevs) {
        this.id = id;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.hungryDevs = hungryDevs;
    }

    @Override
    public void run() {
        while (Lunch.REMAINING_FOOD.get() > 0) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(20, 60));
            } catch (InterruptedException e) {
                System.out.println("Программиста " + id + " прервали на обсуждении.");
                return;
            }

            hungryDevs.add(this);
        }
    }

    public void eat() {
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
                    System.out.println("Программист " + id  + ". Всего съел: " + eaten);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(60, 140));
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Программиста " + id + " прервали на поедании блюда #" + eaten);
            return;
        }

        System.out.println("Программист " + id + " закончил обед. Съел порций: " + eaten);
    }

    @Override
    public int compareTo(Developer o) {
        if (this.eaten > o.eaten) {
            return 1;
        } else if (this.eaten < o.eaten) {
            return -1;
        } else {
            return 0;
        }
    }
}
