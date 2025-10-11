package net.weichware.jbdao.spec;


import com.google.gson.annotations.SerializedName;
import net.weichware.jbdao.util.NameUtil;

import java.util.List;

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
    private Boolean setter;
    private Boolean toString;
    private Boolean hashEquals;
    private Boolean csv;
    @SerializedName("default")
    private String defaultValue;
    private String min;
    private String max;
    private String pattern;
    private String maskPattern;
    private String maskReplace;

    private String getterName;
    private String setterName;

    private boolean isEnum;
    private String path;

    private List<Value> values;

    private String databaseType;

    private Specification specification;

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public String name() {
        return name;
    }

    public String type() {
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

    public Boolean generateSetter() {
        if (setter == null) {
            setter = !isImmutable();
        }
        return setter;
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

    public boolean isEnum() {
        return isEnum;
    }

    public String path() {
        return path;
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

    public String databaseName() {
        if (databaseName == null) {
            databaseName = NameUtil.camelToSnakeUpperCase(name);
        }
        return databaseName;
    }

    public String displayName() {
        if (displayName == null) {
            displayName = NameUtil.camelToDisplay(name);
        }
        return displayName;
    }

    public String csvName() {
        if (csvName == null) {
            csvName = databaseName();
        }
        return csvName;
    }

    public String jsonName() {
        if (jsonName == null) {
            jsonName = name;
        }
        return jsonName;
    }

    public String defaultValue() {
        return defaultValue("");
    }

    public String defaultValue(String prefix) {
        if (defaultValue == null) {
            return "";
        }
        if (type().equals("String")) {
            return prefix + "\"" + defaultValue + "\"";
        } else {
            return prefix + defaultValue;
        }
    }

    public String min() {
        return min;
    }

    public String max() {
        return max;
    }

    public String pattern() {
        if (pattern == null) {
            return null;
        } else if (pattern.isEmpty()) {
            return null;
        }
        return pattern.replace("\\", "\\\\");
    }

    public String getterName() {
        if (getterName == null) {
            if (specification.accessorPrefix()) {
                getterName = "get" + NameUtil.firstCharacterUpper(name());
            } else {
                getterName = name();
            }
        }
        return getterName;
    }

    public String setterName() {
        if (setterName == null) {
            if (specification.accessorPrefix()) {
                setterName = "set" + NameUtil.firstCharacterUpper(name());
            } else {
                setterName = name();
            }
        }
        return setterName;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public boolean nonEmpty() {
        return type.equals("String") && !acceptsEmpty() && min() == null;
    }

    public boolean hasMinMax() {
        return min != null || max != null || type.equals("String");
    }

    public boolean hasMasking() {
        return maskPattern != null && !maskPattern.isEmpty() && maskReplace != null;
    }

    public String maskPattern() {
        return maskPattern;
    }

    public String maskReplace() {
        return maskReplace;
    }

    public Boolean hasCsv() {
        if (csv == null) {
            csv = true;
        }
        return csv;
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

    public String getValue(String name) {
        Value value = values.stream()
                .filter(v -> v.name().equals(name)).findFirst()
                .orElseThrow(() -> new SpecificationException("Member " + name + " has no value of name '" + name + "'"));
        return value.value();
    }

    public String databaseType() {
        if (databaseType != null && !databaseType.isEmpty()){
            return databaseType;
        }
        switch (type()) {
            case "String":
                return "VARCHAR2(" + (max == null ? "255" : max()) + " CHAR)";
            case "Char":
            case "char":
                return "CHAR";
            case "Short":
            case "short":
                return "NUMBER(5)";
            case "Integer":
            case "int":
                return "NUMBER(10)";
            case "Long":
            case "long":
                return "NUMBER(19)";
            case "Float":
            case "float":
                return "FLOAT(32)";
            case "Double":
            case "double":
                return "FLOAT(64)";
            case "LocalDate":
            case "LocalDateTime":
                return "DATE";
        }
        throw new SpecificationException("Unknown Datatype");
    }
}
