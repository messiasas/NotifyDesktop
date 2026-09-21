package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Função que faz Gson converter LocalDateTime, LocalTime e Duration para JSON

public class GsonFactory {

    public static Gson criarGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, type, ctx) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, ctx) ->
                        LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, type, ctx) ->
                        new JsonPrimitive(src.toMillis()))
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, type, ctx) ->
                        Duration.ofMillis(json.getAsLong()))
                .create();
    }
}