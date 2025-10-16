package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class JsonFileWriter {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static BufferedWriter writer;
  private static final ReentrantLock lock = new ReentrantLock();
  private static boolean first = true;

  public static void init(String filePath) throws IOException {
    writer = new BufferedWriter(new FileWriter(filePath, false));
    writer.write("[\n");
  }

  public static void write(JsonNode node) {
    lock.lock();
    try {
      if (!first) {
        writer.write(",\n");
      } else {
        first = false;
      }
      writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
      writer.flush();
    } catch (IOException e) {
      System.err.println("Error writing JSON: " + e.getMessage());
    } finally {
      lock.unlock();
    }
  }

  public static void close() {
    lock.lock();
    try {
      writer.write("\n]");
      writer.close();
    } catch (IOException ignored) {
    } finally {
      lock.unlock();
    }
  }
}
