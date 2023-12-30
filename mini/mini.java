import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(0);
        AtomicBoolean sharedVar = new AtomicBoolean(false);
        Lock lock = new ReentrantLock();

        Thread process1 = new Thread(() -> {
               // Initialize red to 0
            int count = 0;  // Initialize count to 0 initially
            int red = 0;
            int green=30;
            while (true) {
                lock.lock();
                try {
                    if (!sharedVar.get()) {
                        if (count == 0) {
                            count = new Random().nextInt(10) + 1; // Generate initial count
                             green = 30; // Initialize green to 30
                            
                        }

                        System.out.println("East---> Red: " + red + ", Green: " + green + ", Count: " + count);

                        // Decrement green and count simultaneously
                        green--;
                        count--;

                        if (count == 0) {
                            System.out.println("Count is zero. Switching to west");
                            sharedVar.set(true);
                            semaphore.release();
                            semaphore.acquire();
                        }

                        Thread.sleep(1000);
                    } else {
                        System.out.println("Switching to west");
                        sharedVar.set(false);
                        semaphore.release();
                        semaphore.acquire();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread process2 = new Thread(() -> {
              // Initialize red to 0
            int count = 0;  // Initialize count to 0 initially
            int red = 0;
            int green=30;
            while (true) {
                lock.lock();
                try {
                    if (sharedVar.get()) {
                        if (count == 0) {
                            count = new Random().nextInt(10) + 1; // Generate initial count
                              green = 30; // Initialize green to 30
                              
                        }

                        System.out.println("west--> Red: " + red + ", Green: " + green + ", Count: " + count);

                        // Decrement green and count simultaneously
                        green--;
                        count--;

                        if (count == 0) {
                            System.out.println("Count is zero. Switching to west");
                            sharedVar.set(false);
                            semaphore.release();
                            semaphore.acquire();
                        }

                        Thread.sleep(1000);
                    } else {
                        System.out.println("Switching to East");
                        sharedVar.set(true);
                        semaphore.release();
                        semaphore.acquire();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
        });

        process1.start();
        process2.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            process1.interrupt();
            process2.interrupt();
        }));
    }
}
