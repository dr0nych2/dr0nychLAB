package com.example.scraper;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.out.println("Usage: java -jar api-scrapper.jar <n> <t> <configPath> <format>");
      return;
    }

    int maxThreads = Integer.parseInt(args[0]);
    int interval = Integer.parseInt(args[1]);
    String configPath = args[2];
    String format = args[3];

    List<String> services = ConfigLoader.loadServices(configPath);
    System.out.println("Loaded " + services.size() + " services.");

    WriterManager.init(format, "output." + format);
    ApiPoller.initHttpClient();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(maxThreads);

    for (int i = 0; i < services.size(); i++) {
      String api = services.get(i);
      scheduler.scheduleWithFixedDelay(new ApiPoller(api), 0, interval, TimeUnit.SECONDS);
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutting down...");
      scheduler.shutdown();
      WriterManager.close();
    }));
  }
}
