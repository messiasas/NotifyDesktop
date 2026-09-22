import model.Settings;
import notification.NotificationToast;
import persistence.SettingsRepository;
import persistence.WordRepository;
import scheduler.NotificationScheduler;
import tray.TrayIconManager;
import ui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        WordRepository wordRepository = new WordRepository();
        SettingsRepository settingsRepository = new SettingsRepository();

        NotificationScheduler scheduler = new NotificationScheduler(
                wordRepository,
                settingsRepository,
                (listaPalavras, indice, aoFechar) -> SwingUtilities.invokeLater(() -> new NotificationToast(listaPalavras, indice, aoFechar).exibir())
        );

        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow(wordRepository, settingsRepository, scheduler);
            TrayIconManager trayIconManager = new TrayIconManager(scheduler, mainWindow);
            trayIconManager.exibir();
            mainWindow.setVisible(true); // já abre a tela principal ao iniciar
        });

        scheduler.iniciar();
    }
}