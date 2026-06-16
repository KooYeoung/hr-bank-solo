package com.kooyeoung.hrbank.service;

import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface GeneratedFileWriter {
    void write(Path path) throws IOException;
}
