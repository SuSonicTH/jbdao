package net.weichware.jbdao.spec;


import com.google.gson.annotations.SerializedName;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.util.NameUtil;

public class Member {
    private String name;
    private String type;

    private String databaseName;
    private String csvName;
    private String jsonName;
    private String displayName;

    private boolean primary;
    private Boolean immutable;
    private Boolean nullable;
    private Boolean acceptEmpty;
    private Boolean with;
    private Boolean getter;
    private Boolean toString;
    private Boolean hashEquals;
    @SerializedName("default")
    private String defaultValue;
    private String min;
    private String max;
    private String pattern;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public Boolean generateToString() {
        if (toString == null) {
            toString = true;
        }
        return toString;
    }

    public Boolean generateHashEquals() {
        if (hashEquals == null) {
            hashEquals = true;
        }
        return hashEquals;
    }

    public Boolean generateGetter() {
        if (getter == null) {
            getter = true;
        }
        return getter;
    }

    public Boolean isImmutable() {
        if (immutable == null) {
            immutable = true;
        }
        return immutable;
    }

    public Boolean isNullable() {
        if (nullable == null) {
            nullable = false;
        }
        return nullable;
    }

    public Boolean isNotNullable() {
        return !isNullable();
    }

    public boolean acceptsEmpty() {
        if (acceptEmpty == null) {
            acceptEmpty = isNullable();
        }
        return acceptEmpty;
    }

    public boolean generateWith() {
        if (with == null) {
            with = true;
        }
        return with;
    }

    public String getDatabaseName() {
        if (databaseName == null) {
            databaseName = NameUtil.camelToSnakeUpperCase(name);
        }
        return databaseName;
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = NameUtil.camelToDisplay(name);
        }
        return displayName;
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

    public String getDefaultValue() {
        return getDefaultValue("");
    }

    public String getDefaultValue(String prefix) {
        if (defaultValue == null) {
            return "";
        }
        if (getType().equals("String")) {
            return prefix + "\"" + defaultValue + "\"";
        } else {
            return prefix + defaultValue;
        }
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getPattern() {
        if (pattern == null) {
            return null;
        } else if (pattern.isEmpty()) {
            return null;
        }
        return pattern.replace("\\", "\\\\");
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", csvName='" + csvName + '\'' +
                ", jsonName='" + jsonName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", primary=" + primary +
                ", immutable=" + immutable +
                ", nullable=" + nullable +
                ", acceptEmpty=" + acceptEmpty +
                ", with=" + with +
                ", getter=" + getter +
                ", toString=" + toString +
                ", hashEquals=" + hashEquals +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public boolean nonEmpty() {
        return type.equals("String") && !acceptsEmpty() && getMin() == null;
    }

    public boolean hasMinMax() {
        return min != null || max != null;
    }
}
