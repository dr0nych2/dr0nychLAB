package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class CsvFileWriter {

  private static BufferedWriter writer;
  private static final ObjectMapper mapper = new ObjectMapper();
  private static boolean headerWritten = false;
  private static final ReentrantLock lock = new ReentrantLock();

  public static void init(String filePath) throws IOException {
    writer = new BufferedWriter(new FileWriter(filePath, false));
    headerWritten = false;
  }

  public static void write(JsonNode node) {
    lock.lock();
    try {
      if (node == null) {
        return;
      }
      if (!node.isObject()) {
        node = mapper.createObjectNode().put("raw", node.toString());
      }

      ObjectNode obj = (ObjectNode) node;
      if (!headerWritten) {
        Iterator<String> keys = obj.fieldNames();
        boolean first = true;
        while (keys.hasNext()) {
          if (!first) {
            writer.write(",");
          }
          writer.write(keys.next());
          first = false;
        }
        writer.newLine();
        headerWritten = true;
      }

      Iterator<String> fields = obj.fieldNames();
      boolean first = true;
      while (fields.hasNext()) {
        if (!first) {
          writer.write(",");
        }
        String k = fields.next();
        String v = obj.get(k).isContainerNode() ?
            mapper.writeValueAsString(obj.get(k)) :
            obj.get(k).asText().replace(",", " ");
        writer.write(v);
        first = false;
      }
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      System.err.println("Error writing CSV: " + e.getMessage());
    } finally {
      lock.unlock();
    }
  }

  public static void close() {
    lock.lock();
    try {
      writer.close();
    } catch (IOException ignored) {
    } finally {
      lock.unlock();
    }
  }
}
