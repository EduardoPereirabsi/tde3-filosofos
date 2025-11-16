package parte3;

public class DeadlockCorrigido {

    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("T1: Pegando LOCK_A");
            synchronized (LOCK_A) {
                System.out.println("T1: LOCK_A OK");
                sleep(50);

                System.out.println("T1: Tentando LOCK_B");
                synchronized (LOCK_B) {
                    System.out.println("T1: LOCK_B OK - terminou");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println("T2: Pegando LOCK_A");
            synchronized (LOCK_A) {
                System.out.println("T2: LOCK_A OK");
                sleep(50);

                System.out.println("T2: Tentando LOCK_B");
                synchronized (LOCK_B) {
                    System.out.println("T2: LOCK_B OK - terminou");
                }
            }
        });

        long inicio = System.currentTimeMillis();
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        long duracao = System.currentTimeMillis() - inicio;
        System.out.println("Threads terminaram sem deadlock (" + duracao + " ms)");
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
