package net.weichware.myapp;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weichware.jbdao.ValidationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Product {
    VCE("VCE", "VOICE", "voice", 0.1, true),
    VID("VID", "VIDEO", "video", 0.2, true),
    SMS("SMS", "SMS", "SMS", 0.1, true),
    MMS("MMS", "MMS", "MMS", 0.3, true),
    PS("PS", "PS", "PS", 1, false);

    private static final Map<String, Product> databaseMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.database, v -> v));
    private static final Map<String, Product> csvMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.csv, v -> v));
    private static final Map<String, Product> jsonMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.json, v -> v));

    private final String database;
    private final String csv;
    private final String json;
    private final double rate;
    private final boolean isCS;

    Product(String database, String csv, String json, double rate, boolean isCS) {
        this.database = database;
        this.csv = csv;
        this.json = json;
        this.rate = rate;
        this.isCS = isCS;
    }

    public double rate() {
        return rate;
    }

    public boolean isCS() {
        return isCS;
    }

    public String toDatabase() {
        return database;
    }

    public String toCsv() {
        return csv;
    }

    public String toJson() {
        return json;
    }

    public static Product fromDatabase(String value) {
        return optionalFromDatabase(value).orElseThrow(() -> new ValidationException("database value '" + value + "' for enum Product is unknown"));
    }

    public static Product fromCsv(String value) {
        return optionalFromCsv(value).orElseThrow(() -> new ValidationException("csv value '" + value + "' for enum Product is unknown"));
    }

    public static Product fromJson(String value) {
        return optionalFromJson(value).orElseThrow(() -> new ValidationException("json value '" + value + "' for enum Product is unknown"));
    }

    public static Optional<Product> optionalFromDatabase(String value) {
        return Optional.ofNullable(databaseMap.get(value));
    }

    public static Optional<Product> optionalFromCsv(String value) {
        return Optional.ofNullable(csvMap.get(value));
    }

    public static Optional<Product> optionalFromJson(String value) {
        return Optional.ofNullable(jsonMap.get(value));
    }

    public static class GsonAdapter extends TypeAdapter<Product> {
        @Override
        public void write(final JsonWriter jsonWriter, final Product product) throws IOException {
            jsonWriter.value(product.toJson());
        }

        @Override
        public Product read(final JsonReader jsonReader) throws IOException {
            return Product.fromJson(jsonReader.nextString());
        }
    }
}
