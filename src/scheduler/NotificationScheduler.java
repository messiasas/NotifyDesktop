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
import java.util.function.BiConsumer;

public class NotificationScheduler {

    private static final int MAX_CICLOS = 3;

    private final WordRepository wordRepository;
    private final SettingsRepository settingsRepository;
    private final BiConsumer<List<Word>, Integer> aoNotificar;

    private List<Word> palavras;
    private Settings settings;
    private int indiceAtual = 0;
    private int ciclosCompletos = 0;

    private ScheduledExecutorService executor;

    public NotificationScheduler(WordRepository wordRepository, SettingsRepository settingsRepository, BiConsumer<List<Word>, Integer> aoNotificar) {
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

        long intervaloSegundos = settings.getIntervaloNotificacoes().toSeconds();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::tick, intervaloSegundos, intervaloSegundos, TimeUnit.SECONDS);
    }

    private void tick() {
        if (ciclosCompletos >= MAX_CICLOS) {
            System.out.println("Ciclo máximo atingido (" + MAX_CICLOS + "x). Notificações pausadas.");
            parar();
            return;
        }

        if (estaNoHorarioDeSilencio()) {
            System.out.println("Dentro do horário de silêncio. Notificação pulada.");
            return;
        }

        aoNotificar.accept(palavras, indiceAtual);

        indiceAtual++;
        if (indiceAtual >= palavras.size()) {
            indiceAtual = 0;
            ciclosCompletos++;
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
        if (executor != null) {
            executor.shutdown();
        }
    }
}