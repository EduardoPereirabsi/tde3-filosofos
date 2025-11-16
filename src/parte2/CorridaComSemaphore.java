package parte2;

import java.util.concurrent.*;

public class CorridaComSemaphore {

    static int count = 0;
    static final Semaphore sem = new Semaphore(1, true);  // 1 permissao, modo justo

    public static void main(String[] args) throws Exception {
        System.out.println("===========================================================");
        System.out.println("      PARTE 2 - SOLUCAO COM SEMAPHORE (SINCRONIZADO)");
        System.out.println("===========================================================");
        System.out.println("SOLUCAO: Semaphore binario com modo justo");
        System.out.println("Garante exclusao mutua e ordem FIFO");
        System.out.println("===========================================================\n");

        int T = 8;
        int M = 250_000;
        int esperado = T * M;

        System.out.println("Configuracao:");
        System.out.println("  - Threads: " + T);
        System.out.println("  - Incrementos por thread: " + M);
        System.out.println("  - Valor esperado: " + esperado);
        System.out.println("  - Semaphore: 1 permissao, fair=true (FIFO)\n");

        ExecutorService pool = Executors.newFixedThreadPool(T);

        Runnable tarefa = () -> {
            for (int i = 0; i < M; i++) {
                try {
                    sem.acquire();
                    count++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    sem.release();
                }
            }
        };

        System.out.println("Iniciando " + T + " threads com Semaphore...");
        long t0 = System.nanoTime();

        for (int i = 0; i < T; i++) {
            pool.submit(tarefa);
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);

        long t1 = System.nanoTime();
        double tempoSegundos = (t1 - t0) / 1e9;

        // Resultados
        System.out.println("\n===========================================================");
        System.out.println("RESULTADOS:");
        System.out.println("===========================================================");
        System.out.printf("  Esperado: %,d%n", esperado);
        System.out.printf("  Obtido:   %,d%n", count);
        System.out.printf("  Diferenca: %,d%n", esperado - count);
        System.out.printf("  Tempo de execucao: %.3f segundos%n", tempoSegundos);
        System.out.println("===========================================================");

        if (count == esperado) {
            System.out.println("\nSUCESSO! Valor correto obtido");
            System.out.println("===========================================================");
            System.out.println("Como o Semaphore resolveu o problema:");
            System.out.println("  1. Exclusao Mutua: Apenas 1 thread na secao critica");
            System.out.println("  2. Ordem FIFO: fair=true evita starvation");
            System.out.println("  3. Happens-before: release(T1) sincroniza com acquire(T2)");
            System.out.println("  4. Visibilidade: Mudancas de T1 visiveis para T2");
            System.out.println("\nTrade-off:");
            System.out.println("  [+] Corretude: Sempre correto");
            System.out.println("  [-] Performance: Mais lento devido a contencao");
            System.out.println("  [-] Sincronizacao tem custo computacional");
            System.out.println("\nComparacao:");
            System.out.println("  - Sem controle: Rapido mas INCORRETO");
            System.out.println("  - Com Semaphore: Mais lento mas CORRETO");
            System.out.println("  - Conclusao: Corretude e mais importante que performance");
        } else {
            System.out.println("\nERRO! Valor incorreto obtido");
            System.out.println("Isso nao deveria acontecer com Semaphore");
            System.out.println("Verifique a implementacao");
        }

        System.out.println("===========================================================");

        System.out.println("\nInformacoes sobre Semaphore:");
        System.out.println("===========================================================");
        System.out.println("Semaphore(1, true):");
        System.out.println("  - 1 = numero de permissoes (binario = mutex)");
        System.out.println("  - true = modo justo (FIFO)");
        System.out.println("\nacquire():");
        System.out.println("  - Decrementa contador de permissoes");
        System.out.println("  - Se contador = 0, bloqueia a thread");
        System.out.println("  - Garante happens-before com release anterior");
        System.out.println("\nrelease():");
        System.out.println("  - Incrementa contador de permissoes");
        System.out.println("  - Desbloqueia uma thread esperando (se houver)");
        System.out.println("  - Modo fair: escolhe thread pela ordem FIFO");
        System.out.println("\nRelacao happens-before:");
        System.out.println("  - Todas as operacoes ANTES de release(T1)");
        System.out.println("  - Sao visiveis DEPOIS de acquire(T2)");
        System.out.println("  - Garante consistencia de memoria");
        System.out.println("===========================================================");
    }
}
