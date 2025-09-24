package net.weichware.jbdao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonUtil {
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter().nullSafe())
            .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter().nullSafe());

    public static final Gson GSON = GSON_BUILDER.create();

    private static class GsonLocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.toString());
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString());
        }
    }

    private static class GsonLocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.toString());
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString());
        }
    }
}
