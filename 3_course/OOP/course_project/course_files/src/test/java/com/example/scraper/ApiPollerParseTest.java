package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApiPollerParseTest {

  @Test
  void testParseValidJson() throws Exception {
    String json = "{\"name\":\"John\"}";
    JsonNode node = ApiPoller.parseAndAnnotate(json, "https://test.api", 1, 101L);

    assertTrue(node.has("name"));
    assertTrue(node.has("__source"));
    assertTrue(node.has("__seq"));
    assertTrue(node.has("__thread_id"));
    assertEquals("https://test.api", node.get("__source").asText());
    assertEquals(1, node.get("__seq").asInt());
    assertEquals(101L, node.get("__thread_id").asLong());
  }

  @Test
  void testParseInvalidJson() throws Exception {
    String invalid = "not_a_json";
    JsonNode node = ApiPoller.parseAndAnnotate(invalid, "https://broken.api", 2, 999L);

    assertTrue(node.has("raw"));
    assertTrue(node.has("__source"));
    assertTrue(node.has("__seq"));
    assertTrue(node.has("__thread_id"));
    assertEquals("https://broken.api", node.get("__source").asText());
    assertEquals(2, node.get("__seq").asInt());
    assertEquals(999L, node.get("__thread_id").asLong());
  }
}
