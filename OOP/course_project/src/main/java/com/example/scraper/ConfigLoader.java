package com.example.scraper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigLoader {

  public static List<String> loadServices(String filePath) throws IOException {
    return Files.lines(Path.of(filePath))
        .map(String::trim)
        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
        .collect(Collectors.toList());
  }
}
