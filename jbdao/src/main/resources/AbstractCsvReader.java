package net.weichware.jbdao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public abstract class AbstractCsvReader<T> extends Spliterators.AbstractSpliterator<T> implements AutoCloseable {
    private final BufferedReader bufferedReader;
    private final ArrayList<String> fields = new ArrayList<>();
    private boolean hasHeader;
    private long lineNumber = 0;
    protected final Map<String, Integer> header;

    protected AbstractCsvReader(Reader reader, boolean hasHeader) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.hasHeader = hasHeader;
        if (hasHeader) {
            header = new HashMap<>();
        } else {
            header = null;
        }

        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }

    protected abstract T create(List<String> fields);

    protected void validateHeader(Map<String, Integer> header) {
        //to be overwritten if needed
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            String line = bufferedReader.readLine();
            if (line == null) {
                return false;
            }

            lineNumber++;
            if (hasHeader) {
                hasHeader = false;
                parseHeader(line);
                validateHeader(header);
                return tryAdvance(action);
            }

            if (line.trim().isEmpty()) {
                return tryAdvance(action);
            }

            action.accept(create(parseLine(line)));
            return true;
        } catch (Exception e) {
            throw new CsvReaderException("Could not map line " + lineNumber + " to object", e);
        }
    }

    private void parseHeader(String line) {
        List<String> headerFields = parseLine(line);
        for (int i = 0; i < headerFields.size(); i++) {
            header.put(headerFields.get(i), i);
        }
    }

    private List<String> parseLine(final String line) {
        fields.clear();
        return parseLine(line, fields);
    }

    public static List<String> parseLine(final String line, List<String> fields) {
        fields.clear();
        int start = 0;
        Mode mode = Mode.NONE;

        for (int position = 0; position < line.length(); position++) {
            switch (mode) {
                case NONE:
                    if (line.charAt(position) == ',') {
                        fields.add(line.substring(start, position));
                        start = position + 1;
                    } else if (line.charAt(position) == '"') {
                        start = position + 1;
                        mode = Mode.SEARCH_QUOTE;
                    }
                    continue;
                case SEARCH_QUOTE:
                    if (line.charAt(position) == '"' && line.charAt(position - 1) != '\\') {
                        fields.add(line.substring(start, position - 1));
                        mode = Mode.SEARCH_COMMA;
                    }
                    continue;
                case SEARCH_COMMA:
                    if (line.charAt(position) == ',') {
                        start = position + 1;
                        mode = Mode.NONE;
                    }
            }
        }
        switch (mode) {
            case NONE:
            case SEARCH_COMMA:
                if (start < line.length()) {
                    fields.add(line.substring(start));
                }
                break;
            case SEARCH_QUOTE:
                throw new CsvReaderException("Reached end of line while searching for closing quote");
        }
        return fields;
    }

    private enum Mode {
        NONE, SEARCH_QUOTE, SEARCH_COMMA
    }

    @Override
    public void close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }
}
