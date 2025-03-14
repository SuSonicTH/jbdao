package net.weichware.jbdao.spec;

import lombok.Data;

@Data
public class Member {
    private final String name;
    private final String type;
    private String databaseName;
    private String csvName;
    private String jsonName;
    private String displayName;
    private Boolean immutable;
    private Boolean nullable;
    private Boolean acceptEmpty;

    public Boolean getImmutable() {
        if (immutable == null) {
            immutable = true;
        }
        return immutable;
    }

    public Boolean getNullable() {
        if (nullable == null) {
            nullable = false;
        }
        return nullable;
    }

    public boolean getAcceptEmpty() {
        if (acceptEmpty == null) {
            acceptEmpty = false;
        }
        return acceptEmpty;
    }

    public String getDatabaseName() {
        if (databaseName == null) {
            databaseName = NameUtil.camelToDisplay(name);
        }
        return databaseName;
    }

    public String getDisplayName() {
        if (databaseName == null) {
            databaseName = NameUtil.camelToSnakeUpperCase(name);
        }
        return databaseName;
    }

    public String getCsvName() {
        if (csvName == null) {
            csvName = getDatabaseName();
        }
        return csvName;
    }

    public String getJsonName() {
        if (jsonName == null) {
            jsonName = name;
        }
        return jsonName;
    }
}
