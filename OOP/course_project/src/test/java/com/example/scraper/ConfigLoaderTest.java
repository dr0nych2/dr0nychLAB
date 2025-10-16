package com.example.scraper;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

  @Test
  public void testLoadServices() throws Exception {
    Path tmp = Files.createTempFile("services", ".txt");
    try (FileWriter w = new FileWriter(tmp.toFile())) {
      w.write("https://api.agify.io?name=michael\n");
      w.write("https://api.adviceslip.com/advice\n");
    }

    List<String> services = ConfigLoader.loadServices(tmp.toString());
    assertEquals(2, services.size());
    assertTrue(services.get(0).contains("agify"));
    assertTrue(services.get(1).contains("advice"));
  }

  @Test
  public void testEmptyFile() throws Exception {
    Path tmp = Files.createTempFile("empty", ".txt");
    List<String> services = ConfigLoader.loadServices(tmp.toString());
    assertEquals(0, services.size());
  }
}
