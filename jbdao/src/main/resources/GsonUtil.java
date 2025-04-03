package net.weichware.jbdao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class GsonUtil {
    public static final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter().nullSafe());

    public static final Gson gson = gsonBuilder.create();

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
}
