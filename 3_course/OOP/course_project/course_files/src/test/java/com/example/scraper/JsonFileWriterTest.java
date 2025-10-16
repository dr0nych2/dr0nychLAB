package com.example.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileWriterTest {

  @Test
  public void testWriteJson() throws Exception {
    File tmp = File.createTempFile("out", ".json");
    tmp.deleteOnExit();

    JsonFileWriter.init(tmp.getAbsolutePath());

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    node.put("key", "value");
    JsonFileWriter.write(node);
    JsonFileWriter.close();

    String content = Files.readString(tmp.toPath());
    assertTrue(content.contains("key"));
    assertTrue(content.contains("value"));
    assertTrue(content.startsWith("["));
  }
}
