// WAP in Java to show Implementation of Threads
// Demonstrates:
//   • Extending Thread class
//   • Implementing Runnable interface
//   • Thread lifecycle (new, runnable, running, waiting, dead)
//   • Thread priority, sleep, join, interrupt
//   • Synchronization (synchronized method & block)
//   • Producer–Consumer pattern with wait/notify

public class ThreadsDemo {

    // ══════════════════════════════════════════════════════════════════════════
    // 1. Thread by extending Thread class
    // ══════════════════════════════════════════════════════════════════════════
    static class PrintThread extends Thread {
        private final String message;
        private final int    count;

        PrintThread(String name, String message, int count) {
            super(name);           // set thread name
            this.message = message;
            this.count   = count;
        }

        @Override
        public void run() {
            for (int i = 1; i <= count; i++) {
                System.out.printf("  [%s] %s (#%d)%n", getName(), message, i);
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    System.out.println("  [" + getName() + "] Interrupted!");
                    return;
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2. Thread via Runnable interface
    // ══════════════════════════════════════════════════════════════════════════
    static class CountdownRunnable implements Runnable {
        private final String label;
        private final int    from;

        CountdownRunnable(String label, int from) {
            this.label = label;
            this.from  = from;
        }

        @Override
        public void run() {
            for (int i = from; i >= 0; i--) {
                System.out.printf("  [%s] %d%n", label, i);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
            System.out.println("  [" + label + "] Countdown complete!");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3. Synchronization — shared counter
    // ══════════════════════════════════════════════════════════════════════════
    static class SharedCounter {
        private int count = 0;

        // synchronized method prevents race condition
        public synchronized void increment() {
            count++;
        }

        public int getCount() { return count; }
    }

    static class IncrementThread extends Thread {
        private final SharedCounter counter;
        private final int           times;

        IncrementThread(String name, SharedCounter counter, int times) {
            super(name);
            this.counter = counter;
            this.times   = times;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                counter.increment();
            }
            System.out.println("  [" + getName() + "] done incrementing.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4. Producer–Consumer with wait / notify
    // ══════════════════════════════════════════════════════════════════════════
    static class DataBuffer {
        private int     data;
        private boolean hasData = false;

        public synchronized void produce(int value) throws InterruptedException {
            while (hasData) wait();          // wait until consumer reads
            data    = value;
            hasData = true;
            System.out.println("  [Producer] Produced: " + value);
            notify();                        // wake up consumer
        }

        public synchronized int consume() throws InterruptedException {
            while (!hasData) wait();         // wait until producer writes
            hasData = false;
            System.out.println("  [Consumer] Consumed: " + data);
            notify();                        // wake up producer
            return data;
        }
    }

    static class Producer extends Thread {
        private final DataBuffer buffer;
        Producer(DataBuffer b) { super("Producer"); this.buffer = b; }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                try {
                    buffer.produce(i * 10);
                    Thread.sleep(100);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    static class Consumer extends Thread {
        private final DataBuffer buffer;
        Consumer(DataBuffer b) { super("Consumer"); this.buffer = b; }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                try {
                    buffer.consume();
                    Thread.sleep(150);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Threads Demo                   ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── 1. Extending Thread ──
        System.out.println("── 1. Extending Thread class ─────────────");
        PrintThread t1 = new PrintThread("Thread-A", "Hello from A", 3);
        PrintThread t2 = new PrintThread("Thread-B", "Hello from B", 3);

        t1.setPriority(Thread.MAX_PRIORITY);
        t2.setPriority(Thread.MIN_PRIORITY);

        t1.start();
        t2.start();
        t1.join();  // wait for t1 to finish
        t2.join();

        // ── 2. Implementing Runnable ──
        System.out.println("\n── 2. Implementing Runnable ───────────────");
        Thread r1 = new Thread(new CountdownRunnable("Rocket", 5), "RocketThread");
        Thread r2 = new Thread(new CountdownRunnable("Bomb",   3), "BombThread");
        r1.start();
        r2.start();
        r1.join();
        r2.join();

        // ── 3. Lambda Runnable (Java 8+) ──
        System.out.println("\n── 3. Lambda Runnable ─────────────────────");
        Thread lambdaThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [Lambda] iteration " + i);
            }
        });
        lambdaThread.start();
        lambdaThread.join();

        // ── 4. Synchronization ──
        System.out.println("\n── 4. Synchronization ─────────────────────");
        SharedCounter counter  = new SharedCounter();
        IncrementThread inc1   = new IncrementThread("Inc-1", counter, 1000);
        IncrementThread inc2   = new IncrementThread("Inc-2", counter, 1000);
        inc1.start();
        inc2.start();
        inc1.join();
        inc2.join();
        System.out.println("  Expected count : 2000");
        System.out.println("  Actual count   : " + counter.getCount()
                + (counter.getCount() == 2000 ? "  ✔ No race condition!" : "  ✘ Race condition!"));

        // ── 5. Producer–Consumer ──
        System.out.println("\n── 5. Producer–Consumer (wait/notify) ────");
        DataBuffer buffer   = new DataBuffer();
        Producer   producer = new Producer(buffer);
        Consumer   consumer = new Consumer(buffer);
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // ── Thread info ──
        System.out.println("\n── 6. Thread Info ─────────────────────────");
        Thread cur = Thread.currentThread();
        System.out.println("  Current thread : " + cur.getName());
        System.out.println("  Thread ID      : " + cur.getId());
        System.out.println("  Priority       : " + cur.getPriority());
        System.out.println("  Is alive       : " + cur.isAlive());
        System.out.println("  Is daemon      : " + cur.isDaemon());

        System.out.println("\n[Done]");
    }
}