package parte3;

public class DeadlockDemo {
    
    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        
        Thread t1 = new Thread(() -> {

            System.out.println("T1: Pegando LOCK_A");
            synchronized (LOCK_A) {
                System.out.println("T1: LOCK_A OK");
                sleep(100);
                
                System.out.println("T1: Tentando LOCK_B");
                synchronized (LOCK_B) {
                }
            }
        });
        
        Thread t2 = new Thread(() -> {
            
            System.out.println("T2: Pegando LOCK_B");
            synchronized (LOCK_B) {
                System.out.println("T2: LOCK_B OK");
                sleep(100);
                
                System.out.println("T2: Tentando LOCK_A");
                synchronized (LOCK_A) {
                }
            }
        });

        long inicio = System.currentTimeMillis();
        t1.start();
        t2.start();

        long timeout = 5000;
        t1.join(timeout);
        t2.join(timeout);
        
        long duracao = System.currentTimeMillis() - inicio;
        

        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("DEADLOCK DETECTADO " + duracao + " ms");
            
            t1.interrupt();
            t2.interrupt();
        } else {
            System.out.println("Threads terminaram sem deadlock");
        }
    }
    
    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
