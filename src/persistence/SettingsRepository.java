package persistence;

import com.google.gson.Gson;
import model.Settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//SettingsRepository sabe carregar e salvar as configurações em settings.json

public class SettingsRepository {

    private static final String NOTIFY_DIR = System.getProperty("user.home") + File.separator + ".notify";
    private static final String SETTINGS_FILE = NOTIFY_DIR + File.separator + "settings.json";

    private final Gson gson = GsonFactory.criarGson();

    public Settings carregar() {
        garantirDiretorio();
        File arquivo = new File(SETTINGS_FILE);

        if (!arquivo.exists()) {
            Settings padrao = new Settings();
            salvar(padrao); // já grava o padrão pra próxima vez
            return padrao;
        }

        // O FileReader abre o arquivo settings.json pra leitura.
        try (FileReader reader = new FileReader(arquivo)) {

            // le lê o conteúdo do arquivo (via o reader) e converte o texto JSON em um objeto Java do tipo Settings
            Settings settings = gson.fromJson(reader, Settings.class);
            return settings != null ? settings : new Settings(); // new Settings() tem um valor padrão, se não for especificado antes

        } catch (IOException e) {
            System.err.println("Erro ao carregar settings.json: " + e.getMessage());
            return new Settings();
        }
    }

    public void salvar(Settings settings) {
        garantirDiretorio();

        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar settings.json: " + e.getMessage());
        }
    }

    private void garantirDiretorio() {
        File dir = new File(NOTIFY_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}