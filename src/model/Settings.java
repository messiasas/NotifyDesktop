package model;

import java.time.Duration;
import java.time.LocalTime;

public class Settings {

    private Duration intervaloNotificacoes;
    private LocalTime inicioSilencio;
    private LocalTime fimSilencio;
    private String idioma; // opcional

    public Settings() {
        // valores padrão de primeira execução
        this.intervaloNotificacoes = Duration.ofMinutes(5);
        this.inicioSilencio = LocalTime.of(22, 0);
        this.fimSilencio = LocalTime.of(8, 0);
        this.idioma = "en";
    }

    public Settings(Duration intervaloNotificacoes, LocalTime inicioSilencio, LocalTime fimSilencio, String idioma) {
        this.intervaloNotificacoes = intervaloNotificacoes;
        this.inicioSilencio = inicioSilencio;
        this.fimSilencio = fimSilencio;
        this.idioma = idioma;
    }

    public Duration getIntervaloNotificacoes() {
        return intervaloNotificacoes;
    }

    public void setIntervaloNotificacoes(Duration intervaloNotificacoes) {
        this.intervaloNotificacoes = intervaloNotificacoes;
    }

    public LocalTime getInicioSilencio() {
        return inicioSilencio;
    }

    public void setInicioSilencio(LocalTime inicioSilencio) {
        this.inicioSilencio = inicioSilencio;
    }

    public LocalTime getFimSilencio() {
        return fimSilencio;
    }

    public void setFimSilencio(LocalTime fimSilencio) {
        this.fimSilencio = fimSilencio;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    @Override
    public String toString() {
        return "Settings{intervalo=" + intervaloNotificacoes.toMinutes() + "min, silêncio=" +
                inicioSilencio + "-" + fimSilencio + ", idioma=" + idioma + "}";
    }
}