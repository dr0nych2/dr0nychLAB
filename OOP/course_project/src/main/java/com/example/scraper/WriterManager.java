package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class WriterManager {

  private static String format;

  public static void init(String formatStr, String outPath) throws IOException {
    format = formatStr.toLowerCase();
    if (format.equals("csv")) {
      CsvFileWriter.init(outPath);
    } else {
      JsonFileWriter.init(outPath);
    }
    System.out.println("Writer initialized: " + format.toUpperCase());
  }

  public static void write(JsonNode node) {
    if ("csv".equals(format)) {
      CsvFileWriter.write(node);
    } else {
      JsonFileWriter.write(node);
    }
  }

  public static void close() {
    if ("csv".equals(format)) {
      CsvFileWriter.close();
    } else {
      JsonFileWriter.close();
    }
  }
}
