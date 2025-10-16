package com.example.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiPoller implements Runnable {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static CloseableHttpClient httpClient;
  private static final AtomicInteger seq = new AtomicInteger(1);

  private final String url;

  public ApiPoller(String url) {
    this.url = url;
  }

  public static void initHttpClient() {
    httpClient = HttpClients.createDefault();
  }

  @Override
  public void run() {
    long threadId = Thread.currentThread().getId();
    String threadName = Thread.currentThread().getName();
    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

    System.out.printf("[%s | Thread-%d] -> Начинаю запрос: %s%n", time, threadId, url);

    try {
      HttpGet request = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        String body;
        try {
          body = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
          System.err.printf("[%s | Thread-%d] Ошибка парсинга %s: %s%n",
              time, threadId, url, e.getMessage());
          return;
        }

        JsonNode node = parseAndAnnotate(body, url, seq.getAndIncrement(), threadId);
        WriterManager.write(node);

        String doneTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.printf("[%s | Thread-%d] Данные сохранены: %s%n",
            doneTime, threadId, url);
      }
    } catch (IOException e) {
      String errTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
      System.err.printf("[%s | Thread-%d] Ошибка запроса %s: %s%n",
          errTime, threadId, url, e.getMessage());
    }
  }

  static JsonNode parseAndAnnotate(String body, String url, int seq, long threadId)
      throws IOException {
    JsonNode node;
    try {
      node = mapper.readTree(body);
    } catch (Exception e) {
      node = mapper.createObjectNode().put("raw", body);
    }
    if (node.isObject()) {
      ((ObjectNode) node).put("__source", url);
      ((ObjectNode) node).put("__seq", seq);
      ((ObjectNode) node).put("__thread_id", threadId);
      ((ObjectNode) node).put("__timestamp", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
    return node;
  }
}
