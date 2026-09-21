package persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Word;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// WordRepository sabe carregar e salvar a lista de palavras em words.json

public class WordRepository {

    private static final String NOTIFY_DIR = System.getProperty("user.home") + File.separator + ".notify";
    private static final String WORDS_FILE = NOTIFY_DIR + File.separator + "words.json";

    private final Gson gson = GsonFactory.criarGson();

    public List<Word> carregar() {
        garantirDiretorio();
        File arquivo = new File(WORDS_FILE);

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(arquivo)) {
            Type tipoLista = new TypeToken<List<Word>>() {}.getType();

            List<Word> palavras = gson.fromJson(reader, tipoLista);
            return palavras != null ? palavras : new ArrayList<>();

        } catch (IOException e) {
            System.err.println("Erro ao carregar words.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void salvar(List<Word> palavras) {
        garantirDiretorio();

        try (FileWriter writer = new FileWriter(WORDS_FILE)) {
            gson.toJson(palavras, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar words.json: " + e.getMessage());
        }
    }

    private void garantirDiretorio() {
        File dir = new File(NOTIFY_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
