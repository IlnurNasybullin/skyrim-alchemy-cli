package io.github.ilnurnasybullin.skyrim.alchemy.repository.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvReaderImpl implements CsvReader {

    private String separator;
    private Set<String> headers;
    private Charset charset;
    private Stream<String> lines;
    private InputStream inputStream;
    private BufferedReader reader;

    public static class CsvRow implements Row {

        private final Map<String, String> values;

        public CsvRow(Map<String, String> values) {
            this.values = values;
        }

        @Override
        public String value(String header) {
            return values.get(header);
        }
    }

    @Override
    public CsvReader inputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    @Override
    public CsvReader separator(String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public CsvReader headers(String... headers) {
        this.headers = Set.of(headers);
        return this;
    }

    @Override
    public CsvReader charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public Stream<Row> values() {
        var lineMapper = createLineMapper();
        reader = new BufferedReader(
                new InputStreamReader(inputStream, charset)
        );

        lines = reader.lines();

        return lines.map(lineMapper)
                .skip(1);
    }

    private Function<String, Row> createLineMapper() {
        if (headers == null || headers.isEmpty()) {
            // TODO
            throw new IllegalArgumentException();
        }

        var isHeaderLine = new AtomicBoolean(true);
        var headersMap = new HashMap<Integer, String>();

        return line -> {
            if (isHeaderLine.get()) {
                var headers = line.split(separator);
                for (int i = 0; i < headers.length; i++) {
                    var header = headers[i];

                    if (this.headers.contains(header)) {
                        headersMap.put(i, header);
                    }
                }

                isHeaderLine.set(false);
                return new CsvRow(Map.of());
            } else {
                var values = line.split(separator);
                var valuesMap = headersMap.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getValue,
                                entry -> {
                                    Integer index = entry.getKey();
                                    return values[index];
                                }
                        ));

                return new CsvRow(valuesMap);
            }
        };
    }

    @Override
    public void close() throws IOException {
        lines.close();
        reader.close();
    }
}
