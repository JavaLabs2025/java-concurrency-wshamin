package org.labs.lab1;
import java.util.concurrent.BlockingQueue;

public class Waiter implements Runnable {

    private final int id;
    private final BlockingQueue<Developer> hungryDevs;

    public Waiter(int id, BlockingQueue<Developer> hungryDevs) {
        this.id = id;
        this.hungryDevs = hungryDevs;
    }

    @Override
    public void run() {
        int next = Lunch.REMAINING_FOOD.getAndDecrement();

        while (next > 0) {
            try {
                Developer hungryDev = hungryDevs.take();
                hungryDev.eat();
            } catch (InterruptedException e) {
                System.out.println("Вставка официантом " + id + " блюда #" + next + " была прервана");
                Thread.currentThread().interrupt();
                return;
            }

            next = Lunch.REMAINING_FOOD.getAndDecrement();
        }
    }
}
