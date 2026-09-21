package tray;

import scheduler.NotificationScheduler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TrayIconManager {

    private final NotificationScheduler scheduler;
    private TrayIcon trayIcon;
    private MenuItem itemPausarRetomar;
    private boolean pausado = false;

    public TrayIconManager(NotificationScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void exibir() {
        if (!SystemTray.isSupported()) {
            System.err.println("Bandeja do sistema não suportada neste ambiente.");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        PopupMenu menu = new PopupMenu();

        itemPausarRetomar = new MenuItem("Pausar notificações");
        itemPausarRetomar.addActionListener(e -> alternarPausa());
        menu.add(itemPausarRetomar);

        MenuItem itemSair = new MenuItem("Sair");
        itemSair.addActionListener(e -> sair());
        menu.add(itemSair);

        trayIcon = new TrayIcon(criarIcone(), "Notify", menu);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone à bandeja: " + e.getMessage());
        }
    }

    private void alternarPausa() {
        if (pausado) {
            scheduler.iniciar();
            itemPausarRetomar.setLabel("Pausar notificações");
            trayIcon.displayMessage("Notify", "Notificações retomadas", TrayIcon.MessageType.INFO);
        } else {
            scheduler.parar();
            itemPausarRetomar.setLabel("Retomar notificações");
            trayIcon.displayMessage("Notify", "Notificações pausadas", TrayIcon.MessageType.INFO);
        }
        pausado = !pausado;
    }

    private void sair() {
        scheduler.parar();
        SystemTray.getSystemTray().remove(trayIcon);
        System.exit(0);
    }

    private Image criarIcone() {
        int tamanho = 16;
        BufferedImage imagem = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(30, 144, 255));
        g.fillOval(1, 1, tamanho - 2, tamanho - 2);
        g.setColor(Color.WHITE);
        g.drawString("N", 4, 12);
        g.dispose();
        return imagem;
    }
}
