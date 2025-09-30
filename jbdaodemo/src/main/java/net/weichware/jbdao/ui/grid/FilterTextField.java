package net.weichware.jbdao.ui.grid;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterTextField<T, C> extends ComboBox<String> {
    public static final String EMPTY = " <empty> ";
    private static final Pattern DATE_SEARCH_PATTERN = Pattern.compile("(\\d\\d\\d\\d)-?(\\d\\d)?-?(\\d\\d)?");
    private final AdvancedGrid<T> grid;
    private final ValueProvider<T, C> valueProvider;
    private SerializablePredicate<T> filter;
    private String compareAgainst;
    private T compareAgainstObject;
    private boolean compareAgainstObjectIsInvalid;
    private boolean fromSelection = true;

    FilterTextField(AdvancedGrid<T> grid, ValueProvider<T, C> valueProvider, String columnHeader) {
        this.grid = grid;
        this.valueProvider = valueProvider;
        this.setId(columnHeader.replace(" ", "_") + "_filter");
        this.setClearButtonVisible(true);
        addValueChangeListener(event -> {
            createValueFilter();
            grid.updateFilters();
            fromSelection = true;
        });
        addCustomValueSetListener(event -> {
            fromSelection = false;
            this.setValue(event.getDetail());
        });

        setSizeFull();
        setPlaceholder("Filter");
    }

    public void updateList() {
        if (grid.dataProvider != null) {
            setItems(grid.dataProvider.getItems().stream().map(valueProvider).map(this::getValue).distinct().sorted().toList());
        }
    }

    private String getValue(Object object) {
        String value = object == null ? "" : object.toString();
        return value.isEmpty() ? EMPTY : value;
    }

    public ValueProvider<T, C> getValueProvider() {
        return valueProvider;
    }

    public SerializablePredicate<T> getFieldFilter() {
        return filter;
    }

    private void createValueFilter() {
        String filterString = getValue();
        if (filterString == null) filterString = "";

        if (fromSelection) {
            if (filterString.equals(EMPTY)) {
                setEmptyFilter();
            } else {
                setComparisonFilter("=" + filterString);
            }
            return;
        }

        String filterStringTrimmed = filterString.trim();
        if (filterStringTrimmed.startsWith("#") || filterStringTrimmed.startsWith("^") || filterStringTrimmed.endsWith("$")) {
            setRegexFilter(filterStringTrimmed);
            return;
        } else if (filterStringTrimmed.startsWith("\\#")) {
            setContainsFilter(filterString.substring(1));
            return;
        } else if (filterStringTrimmed.startsWith(">") || filterStringTrimmed.startsWith("<") || filterStringTrimmed.startsWith("=")) {
            setComparisonFilter(filterStringTrimmed);
            return;
        }
        setContainsFilter(filterString);
    }

    private void setRegexFilter(String filterString) {
        if (filterString.startsWith("#")) {
            filterString = filterString.substring(1);
        }
        if (!filterString.startsWith("^")) {
            filterString = ".*" + filterString;
        }
        if (!filterString.endsWith("$")) {
            filterString = filterString + ".*";
        }
        Pattern pattern = Pattern.compile(filterString);
        filter = value -> pattern.matcher(value.toString()).matches();
    }

    private void setContainsFilter(String match) {
        filter = value -> StringUtils.containsIgnoreCase(value == null ? "" : value.toString(), match);
    }

    private void setEmptyFilter() {
        filter = value -> value == null || value.toString().isEmpty();
    }

    private void setComparisonFilter(String filterString) {
        compareAgainstObject = null;
        compareAgainstObjectIsInvalid = false;
        filter = null;
        this.setInvalid(false);

        ComparisonType comparisonType = null;
        if (filterString.startsWith(">=")) {
            compareAgainst = filterString.substring(2);
            comparisonType = ComparisonType.GREATER_THEN_EQUAL;
        } else if (filterString.startsWith(">")) {
            compareAgainst = filterString.substring(1);
            comparisonType = ComparisonType.GREATER_THEN;
        } else if (filterString.startsWith("<=")) {
            compareAgainst = filterString.substring(2);
            comparisonType = ComparisonType.LOWER_THEN_EQUAL;
        } else if (filterString.startsWith("<")) {
            compareAgainst = filterString.substring(1);
            comparisonType = ComparisonType.LOWER_THEN;
        } else if (filterString.startsWith("=")) {
            compareAgainst = filterString.substring(1);
            comparisonType = ComparisonType.EQUAL;
        }
        if (compareAgainst.trim().isEmpty()) {
            return;
        }

        ComparisonType type = comparisonType;
        filter = value -> doCompare(value, type);
    }

    private boolean doCompare(T value, ComparisonType type) {
        if (compareAgainstObjectIsInvalid || value == null) {
            return false;
        }
        if (compareAgainstObject == null) {
            try {
                compareAgainstObject = createCompareAgainstObject(value, compareAgainst);
                if (compareAgainstObject == null) {
                    return false;
                }
            } catch (CompareAgainstObjectIsInvalidException e) {
                this.setInvalid(true);
                this.setErrorMessage(e.getMessage());
                compareAgainstObjectIsInvalid = true;
                return false;
            }

        }

        int result = getCompareResult(value);
        if (result == Integer.MIN_VALUE) return false;
        return ComparisonType.matches(type, result);
    }

    private int getCompareResult(T value) {
        if (value instanceof String) {
            return ((String) value).compareTo(((String) compareAgainstObject));
        } else if (value instanceof Integer) {
            return ((Integer) value).compareTo(((Integer) compareAgainstObject));
        } else if (value instanceof Long) {
            return ((Long) value).compareTo(((Long) compareAgainstObject));
        } else if (value instanceof Double) {
            return ((Double) value).compareTo(((Double) compareAgainstObject));
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).compareTo(((LocalDate) compareAgainstObject));
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).compareTo(((LocalDateTime) compareAgainstObject));
        } else {
            return (value.toString()).compareTo(((String) compareAgainstObject));
        }
    }

    private T createCompareAgainstObject(T type, String compareAgainst) throws CompareAgainstObjectIsInvalidException {
        if (type instanceof String) {
            return (T) compareAgainst;
        } else if (type instanceof Integer) {
            try {
                return (T) (Integer) Integer.parseInt(compareAgainst);
            } catch (Exception e) {
                throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid integer number");
            }
        } else if (type instanceof Long) {
            try {
                return (T) (Long) Long.parseLong(compareAgainst);
            } catch (Exception e) {
                throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid long number");
            }
        } else if (type instanceof Double) {
            try {
                return (T) (Double) Double.parseDouble(compareAgainst);
            } catch (Exception e) {
                throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid decimal number");
            }

        } else if (type instanceof LocalDate) {
            try {
                Matcher matcher = DATE_SEARCH_PATTERN.matcher(compareAgainst);
                if (matcher.matches()) {
                    return (T) LocalDate.of(getIntOrDefaultOne(matcher.group(1)), getIntOrDefaultOne(matcher.group(2)), getIntOrDefaultOne(matcher.group(3)));
                } else {
                    throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid date");
                }
            } catch (Exception e) {
                throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid date");
            }
        } else if (type instanceof LocalDateTime) {
            try {
                return (T) LocalDateTime.parse(compareAgainst);
            } catch (Exception e) {
                throw new CompareAgainstObjectIsInvalidException("'" + compareAgainst + "' is not a valid date & time");
            }
        }
        return (T) compareAgainst;
    }

    private int getIntOrDefaultOne(String number) {
        if (number == null) {
            return 1;
        }
        return Integer.parseInt(number);
    }

    private enum ComparisonType {
        GREATER_THEN,
        GREATER_THEN_EQUAL,
        LOWER_THEN,
        LOWER_THEN_EQUAL,
        EQUAL;

        static boolean matches(ComparisonType type, int result) {
            switch (type) {
                case GREATER_THEN:
                    return result > 0;
                case GREATER_THEN_EQUAL:
                    return result >= 0;
                case LOWER_THEN:
                    return result < 0;
                case LOWER_THEN_EQUAL:
                    return result <= 0;
                case EQUAL:
                    return result == 0;
                default:
                    return false;
            }
        }
    }

    private static class CompareAgainstObjectIsInvalidException extends Exception {
        public CompareAgainstObjectIsInvalidException(String message) {
            super(message);
        }
    }
}
