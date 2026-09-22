package scheduler;

import model.Settings;
import model.Word;
import persistence.SettingsRepository;
import persistence.WordRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    private static final int MAX_CICLOS = 3;

    private final WordRepository wordRepository;
    private final SettingsRepository settingsRepository;
    private final NotificationCallback aoNotificar;

    private List<Word> palavras;
    private Settings settings;
    private int indiceAtual = 0;
    private int ciclosCompletos = 0;
    private volatile boolean pausado = true;

    private ScheduledExecutorService executor;

    public NotificationScheduler(WordRepository wordRepository, SettingsRepository settingsRepository, NotificationCallback aoNotificar) {
        this.wordRepository = wordRepository;
        this.settingsRepository = settingsRepository;
        this.aoNotificar = aoNotificar;
    }

    public void iniciar() {
        palavras = wordRepository.carregar();
        settings = settingsRepository.carregar();

        if (palavras.isEmpty()) {
            System.out.println("Nenhuma palavra cadastrada ainda. Scheduler não iniciado.");
            return;
        }

        pausado = false;
        executor = Executors.newSingleThreadScheduledExecutor();
        agendarProximaRodada(settings.getIntervaloNotificacoes().toSeconds());
    }

    private void agendarProximaRodada(long atrasoSegundos) {
        if (pausado || executor == null || executor.isShutdown()) {
            return;
        }
        executor.schedule(this::avancarOuIniciarRodada, atrasoSegundos, TimeUnit.SECONDS);
    }

    /** Mostra as palavras da lista em sequência: cada notificação, ao fechar, dispara a próxima. */
    private void avancarOuIniciarRodada() {
        if (pausado) {
            return;
        }

        if (ciclosCompletos >= MAX_CICLOS) {
            System.out.println("Ciclo máximo atingido (" + MAX_CICLOS + "x). Notificações pausadas.");
            parar();
            return;
        }

        if (estaNoHorarioDeSilencio()) {
            System.out.println("Dentro do horário de silêncio. Rodada adiada.");
            agendarProximaRodada(settings.getIntervaloNotificacoes().toSeconds());
            return;
        }

        boolean ultimaDaRodada = indiceAtual == palavras.size() - 1;
        aoNotificar.notificar(palavras, indiceAtual, () -> aoNotificacaoFechada(ultimaDaRodada));
    }

    private void aoNotificacaoFechada(boolean ultimaDaRodada) {
        if (pausado) {
            return;
        }

        if (ultimaDaRodada) {
            indiceAtual = 0;
            ciclosCompletos++;
            agendarProximaRodada(settings.getIntervaloNotificacoes().toSeconds());
        } else {
            indiceAtual++;
            avancarOuIniciarRodada();
        }
    }

    private boolean estaNoHorarioDeSilencio() {
        LocalTime agora = LocalTime.now();
        LocalTime inicio = settings.getInicioSilencio();
        LocalTime fim = settings.getFimSilencio();

        if (inicio.isBefore(fim)) {
            return !agora.isBefore(inicio) && agora.isBefore(fim);
        } else {
            return !agora.isBefore(inicio) || agora.isBefore(fim);
        }
    }

    public void reiniciarCiclo() {
        indiceAtual = 0;
        ciclosCompletos = 0;
    }

    public void parar() {
        pausado = true;
        if (executor != null) {
            executor.shutdown();
        }
    }

    /** Reaplica as configurações salvas (ex.: novo intervalo) reiniciando o agendamento. */
    public void aplicarConfiguracoesAtualizadas() {
        boolean estavaRodando = executor != null && !executor.isShutdown();
        parar();
        if (estavaRodando) {
            iniciar();
        }
    }
}
