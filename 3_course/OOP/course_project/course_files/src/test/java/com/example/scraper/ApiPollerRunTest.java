package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

/**
 * Тест класса ApiPoller: проверяет парсинг и запуск run()
 */
public class ApiPollerRunTest {

  @BeforeAll
  static void setup() throws IOException {
    ApiPoller.initHttpClient();
    // init требует путь + формат
    WriterManager.init("results", "json");
  }

  @Test
  void testParseAndAnnotate() throws IOException {
    String body = "{\"a\":1}";
    JsonNode node = ApiPoller.parseAndAnnotate(body, "https://fake", 1, 123L);
    assertNotNull(node);
  }

  @Test
  void testRunWithMockedWriter() throws IOException {
    try (MockedStatic<WriterManager> mocked = Mockito.mockStatic(WriterManager.class)) {
      mocked.when(() -> WriterManager.write(any(JsonNode.class)))
          .thenAnswer(invocation -> {
            System.out.println("Mocked write() вызван");
            return null;
          });

      ApiPoller poller = new ApiPoller("https://dog.ceo/api/breeds/image/random");
      poller.run();

      mocked.verify(() -> WriterManager.write(any(JsonNode.class)), times(1));
    }
  }
}
