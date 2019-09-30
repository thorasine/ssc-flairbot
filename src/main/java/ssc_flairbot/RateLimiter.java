package ssc_flairbot;

import java.util.LinkedList;

public class RateLimiter {

    private final LinkedList<Long> list = new LinkedList<>();

    private final int amount;
    private final int timespan;

    public RateLimiter(int amount, int timespan) {
        this.amount = amount;
        this.timespan = timespan;
    }

    public void acquire() {
        remove();
        while (list.size() >= amount) {
            try {
                Thread.sleep(20);
            } catch (final InterruptedException e) {
            }
            remove();
        }
    }

    public void enter() {
        list.addFirst(System.currentTimeMillis() + timespan);
    }

    private void remove() {
        boolean searching = true;
        while (searching && list.size() > 0) {
            final long element = list.getLast();
            if (element < System.currentTimeMillis()) {
                list.removeLast();
            } else {
                searching = false;
            }
        }
    }

}
