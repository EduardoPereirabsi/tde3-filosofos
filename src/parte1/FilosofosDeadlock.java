package parte1;

public class FilosofosDeadlock {

    enum Estado {
        PENSANDO, COM_FOME, COMENDO
    }
    
    static class Filosofo extends Thread {
        private final int id;
        private final Object garfoEsquerdo;
        private final Object garfoDireito;
        private Estado estado;
        private int vezesQueComeu;
        
        public Filosofo(int id, Object garfoEsquerdo, Object garfoDireito) {
            this.id = id;
            this.garfoEsquerdo = garfoEsquerdo;
            this.garfoDireito = garfoDireito;
            this.estado = Estado.PENSANDO;
            this.vezesQueComeu = 0;
        }
        
        @Override
        public void run() {
            try {
                while (vezesQueComeu < 3) {
                    pensar();
                    comer();
                }
                System.out.println("Filosofo " + id + " terminou (" + vezesQueComeu + " refeicoes)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void pensar() throws InterruptedException {
            estado = Estado.PENSANDO;
            Thread.sleep((long) (Math.random() * 1000));
        }
        
        private void comer() throws InterruptedException {
            estado = Estado.COM_FOME;
            
            synchronized (garfoEsquerdo) {
                System.out.println("Filosofo " + id + " pegou garfo esquerdo");
                Thread.sleep(100);
                
                synchronized (garfoDireito) {
                    estado = Estado.COMENDO;
                    System.out.println("Filosofo " + id + " comendo");
                    Thread.sleep((long) (Math.random() * 1000));
                    vezesQueComeu++;
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== JANTAR DOS FILOSOFOS - VERSAO COM DEADLOCK ===\n");
        
        final int NUM_FILOSOFOS = 5;
        Object[] garfos = new Object[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Object();
        }
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Object garfoEsquerdo = garfos[i];
            Object garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito);
        }
        
        System.out.println("Iniciando filosofos...\n");
        for (Filosofo filosofo : filosofos) {
            filosofo.start();
        }
        
        // Timeout para detectar deadlock
        long timeout = 10000;
        long inicio = System.currentTimeMillis();
        
        for (Filosofo filosofo : filosofos) {
            long tempoRestante = timeout - (System.currentTimeMillis() - inicio);
            if (tempoRestante > 0) {
                filosofo.join(tempoRestante);
            }
        }
        
        boolean algumVivo = false;
        for (Filosofo filosofo : filosofos) {
            if (filosofo.isAlive()) {
                algumVivo = true;
                break;
            }
        }
        
        System.out.println("\n=== RESULTADO ===");
        if (algumVivo) {
            System.out.println("DEADLOCK DETECTADO - Filosofos travados");
            System.out.println("Condicoes de Coffman presentes:");
            System.out.println("  1. Exclusao Mutua");
            System.out.println("  2. Manter-e-Esperar");
            System.out.println("  3. Nao Preempcao");
            System.out.println("  4. Espera Circular");
            
            for (Filosofo filosofo : filosofos) {
                filosofo.interrupt();
            }
        } else {
            System.out.println("Todos completaram (nao houve deadlock)");
        }
    }
}
