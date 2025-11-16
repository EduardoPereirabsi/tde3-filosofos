package parte2;

import java.util.concurrent.*;

public class    CorridaSemControle {

    static int count = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("===========================================================");
        System.out.println("    PARTE 2 - CONDICAO DE CORRIDA (SEM SINCRONIZACAO)");
        System.out.println("===========================================================");
        System.out.println("PROBLEMA: Incremento count++ NAO e atomico");
        System.out.println("Multiplas threads podem intercalar operacoes");
        System.out.println("===========================================================\n");

        int T = 8;
        int M = 250_000;
        int esperado = T * M;

        System.out.println("Configuracao:");
        System.out.println("  - Threads: " + T);
        System.out.println("  - Incrementos por thread: " + M);
        System.out.println("  - Valor esperado: " + esperado + "\n");

        ExecutorService pool = Executors.newFixedThreadPool(T);

        Runnable tarefa = () -> {
            for (int i = 0; i < M; i++) {
                count++;
            }
        };

        System.out.println("Iniciando " + T + " threads...");
        long t0 = System.nanoTime();

        for (int i = 0; i < T; i++) {
            pool.submit(tarefa);
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);

        long t1 = System.nanoTime();
        double tempoSegundos = (t1 - t0) / 1e9;

        System.out.println("\n===========================================================");
        System.out.println("RESULTADOS:");
        System.out.println("===========================================================");
        System.out.printf("  Esperado: %,d%n", esperado);
        System.out.printf("  Obtido:   %,d%n", count);
        System.out.printf("  Diferenca: %,d (%.2f%% de perda)%n",
                esperado - count,
                ((esperado - count) * 100.0) / esperado);
        System.out.printf("  Tempo de execucao: %.3f segundos%n", tempoSegundos);
        System.out.println("===========================================================");

        if (count < esperado) {
            System.out.println("\nCONDICAO DE CORRIDA DETECTADA!");
            System.out.println("===========================================================");
            System.out.println("Analise:");
            System.out.println("  - count++ = LER -> INCREMENTAR -> ESCREVER (3 operacoes)");
            System.out.println("  - Threads intercalam essas etapas");
            System.out.println("  - Resultado: incrementos perdidos");
            System.out.println("\nExemplo de intercalacao:");
            System.out.println("  Thread 1: LER count (100)");
            System.out.println("  Thread 2: LER count (100)  <- Leu valor desatualizado");
            System.out.println("  Thread 1: ESCREVER 101");
            System.out.println("  Thread 2: ESCREVER 101     <- Perdeu incremento da T1");
            System.out.println("\nSolucao: Usar Semaphore, synchronized ou AtomicInteger");
        } else {
            System.out.println("\nValor correto obtido (caso raro com poucas threads)");
            System.out.println("Execute novamente para aumentar chance de race condition");
        }

        System.out.println("===========================================================");
    }
}
