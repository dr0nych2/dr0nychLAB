package com.example.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class CsvFileWriterTest {

  @Test
  public void testWriteCsv() throws Exception {
    File tmp = File.createTempFile("csvout", ".csv");
    tmp.deleteOnExit();

    CsvFileWriter.init(tmp.getAbsolutePath());
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    node.put("name", "John");
    node.put("age", 30);
    CsvFileWriter.write(node);
    CsvFileWriter.close();

    String content = Files.readString(tmp.toPath());
    assertTrue(content.contains("name"));
    assertTrue(content.contains("John"));
    assertTrue(content.contains("age"));
  }
}
