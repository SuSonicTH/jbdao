package net.weichware.myapp;

import net.weichware.jbdao.ValidationException;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Product {
    VCE("VCE", "VOICE", "voice"),
    VID("VID", "VIDEO", "video"),
    SMS("SMS", "SMS", "SMS"),
    MMS("MMS", "MMS", "MMS");

    private static final Map<String, Product> databaseMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.database, v -> v));
    private static final Map<String, Product> csvMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.csv, v -> v));
    private static final Map<String, Product> jsonMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.json, v -> v));

    private final String database;
    private final String csv;
    private final String json;

    Product(String database, String csv, String json) {
        this.database = database;
        this.csv = csv;
        this.json = json;
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

    public String toDatabase() {
        return database;
    }

    public String toCsv() {
        return csv;
    }

    public String toJson() {
        return json;
    }
}
