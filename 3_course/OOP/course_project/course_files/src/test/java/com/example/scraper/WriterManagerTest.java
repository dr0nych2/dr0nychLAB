package com.example.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class WriterManagerTest {

  @Test
  public void testInitAndWriteJson() throws Exception {
    File tmp = File.createTempFile("writer", ".json");
    tmp.deleteOnExit();

    WriterManager.init("json", tmp.getAbsolutePath());
    ObjectMapper m = new ObjectMapper();
    ObjectNode node = m.createObjectNode();
    node.put("x", 42);
    WriterManager.write(node);
    WriterManager.close();

    String content = Files.readString(tmp.toPath());
    assertTrue(content.contains("42"));
    assertTrue(content.contains("x"));
  }

  @Test
  public void testInitAndWriteCsv() throws Exception {
    File tmp = File.createTempFile("writer", ".csv");
    tmp.deleteOnExit();

    WriterManager.init("csv", tmp.getAbsolutePath());
    ObjectMapper m = new ObjectMapper();
    ObjectNode node = m.createObjectNode();
    node.put("field", "data");
    WriterManager.write(node);
    WriterManager.close();

    String content = Files.readString(tmp.toPath());
    assertTrue(content.contains("field"));
    assertTrue(content.contains("data"));
  }
}
