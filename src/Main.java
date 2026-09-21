import model.Settings;
import model.Word;
import notification.NotificationToast;
import persistence.SettingsRepository;
import persistence.WordRepository;
import scheduler.NotificationScheduler;
import tray.TrayIconManager;

import javax.swing.*;
import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        WordRepository wordRepository = new WordRepository();
        SettingsRepository settingsRepository = new SettingsRepository();

        List<Word> palavras = wordRepository.carregar();
        if (palavras.isEmpty()) {
            palavras.add(new Word("ubiquitous", "onipresente", "Smartphones are ubiquitous today."));
            palavras.add(new Word("resilient", "resiliente", "She remained resilient through hardship."));
            palavras.add(new Word("threshold", "limiar", "We crossed the threshold into a new era."));
            wordRepository.salvar(palavras);
        }

        Settings settings = settingsRepository.carregar();

        settings.setIntervaloNotificacoes(Duration.ofSeconds(5)); // <- ajuste aqui
        settingsRepository.salvar(settings);

        NotificationScheduler scheduler = new NotificationScheduler(
                wordRepository,
                settingsRepository,
                (listaPalavras, indice) -> SwingUtilities.invokeLater(() -> new NotificationToast(listaPalavras, indice).exibir())
        );

        TrayIconManager trayIconManager = new TrayIconManager(scheduler);
        trayIconManager.exibir();

        scheduler.iniciar();
    }
}