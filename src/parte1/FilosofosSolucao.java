package parte1;

public class FilosofosSolucao {
    
    enum Estado {
        PENSANDO, COM_FOME, COMENDO
    }
    
    static class Filosofo extends Thread {
        private final int id;
        private final Garfo garfoMenor;
        private final Garfo garfoMaior;
        private Estado estado;
        private int vezesQueComeu;
        
        public Filosofo(int id, Garfo garfo1, Garfo garfo2) {
            this.id = id;
            if (garfo1.id < garfo2.id) {
                this.garfoMenor = garfo1;
                this.garfoMaior = garfo2;
            } else {
                this.garfoMenor = garfo2;
                this.garfoMaior = garfo1;
            }
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
            
            synchronized (garfoMenor) {
                synchronized (garfoMaior) {
                    estado = Estado.COMENDO;
                    System.out.println("Filosofo " + id + " comendo (garfos " + 
                                     garfoMenor.id + " e " + garfoMaior.id + ")");
                    Thread.sleep((long) (Math.random() * 1000));
                    vezesQueComeu++;
                }
            }
        }
    }
    
    static class Garfo {
        final int id;
        public Garfo(int id) {
            this.id = id;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== JANTAR DOS FILOSOFOS - SOLUCAO SEM DEADLOCK ===");
        System.out.println("Estrategia: Hierarquia de Recursos\n");
        
        final int NUM_FILOSOFOS = 5;
        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo(i);
        }
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito);
        }
        
        System.out.println("Iniciando filosofos...\n");
        long inicio = System.currentTimeMillis();
        
        for (Filosofo filosofo : filosofos) {
            filosofo.start();
        }
        
        for (Filosofo filosofo : filosofos) {
            filosofo.join();
        }
        
        long duracao = System.currentTimeMillis() - inicio;
        
        System.out.println("\n=== RESULTADO ===");
        System.out.println("Todos os filosofos terminaram com sucesso");
        System.out.println("Tempo total: " + duracao + " ms");
        System.out.println("Nenhum deadlock ocorreu (espera circular eliminada)");
    }
}
