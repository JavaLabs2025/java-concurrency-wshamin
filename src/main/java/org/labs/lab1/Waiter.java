package org.labs.lab1;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class Waiter implements Runnable {

    private final int id;
    private final SynchronousQueue<Integer> dishes = new SynchronousQueue<>();

    public Waiter(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            int next = Lunch.REMAINING_FOOD.getAndDecrement();

            if (next <= 0) {
                break;
            }

            try {
                dishes.put(next);
            } catch (InterruptedException e) {
                System.out.println("Вставка официантом " + id + " блюда #" + next + " была прервана");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public Integer getDish() {
        try {
            while (true) {
                Integer dish = dishes.poll(200, TimeUnit.MILLISECONDS);

                if (dish != null) {
                    return dish;
                }

                if (Lunch.REMAINING_FOOD.get() <= 0) {
                    return null;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Получение официантом " + id + " блюда было прервано");
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
