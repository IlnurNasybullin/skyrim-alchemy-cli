package io.github.ilnurnasybullin.skyrim.alchemy.repository.csv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public interface CsvReader extends AutoCloseable {

    CsvReader inputStream(InputStream inputStream);
    CsvReader separator(String separator);
    CsvReader headers(String... headers);
    CsvReader charset(Charset charset);
    Stream<Row> values() throws IOException;

    static CsvReader getInstance() {
        return ServiceLoader.load(CsvReader.class)
                .findFirst()
                .orElseThrow();
    }
}
