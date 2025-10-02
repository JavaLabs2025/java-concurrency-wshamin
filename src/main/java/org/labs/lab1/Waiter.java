package org.labs.lab1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Waiter implements Runnable {

    private final int id;
    private final BlockingQueue<Integer> dishes;

    public Waiter(int id, BlockingQueue<Integer> dishes) {
        this.id = id;
        this.dishes = dishes;
    }

    @Override
    public void run() {
        int next = Lunch.REMAINING_FOOD.getAndDecrement();

        while (next > 0) {
            try {
                dishes.put(next);
            } catch (InterruptedException e) {
                System.out.println("Вставка официантом " + id + " блюда #" + next + " была прервана");
                Thread.currentThread().interrupt();
                return;
            }

            next = Lunch.REMAINING_FOOD.getAndDecrement();
        }
    }

    public Integer getDish() {
        try {
            Integer dish = dishes.poll(200, TimeUnit.MILLISECONDS);

            if (dish != null) {
                return dish;
            }

            if (Lunch.REMAINING_FOOD.get() <= 0 && dishes.isEmpty()) {
                return null;
            }

            return null;
        } catch (InterruptedException e) {
            System.out.println("Получение официантом " + id + " блюда было прервано");
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public boolean isKitchenEmpty() {
        return dishes.isEmpty();
    }
}
