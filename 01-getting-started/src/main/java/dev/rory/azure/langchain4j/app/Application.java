package dev.rory.azure.langchain4j.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Getting Started module.  This simple Spring Boot
 * application exposes a chat endpoint backed by Azure OpenAI via
 * LangChain4j.
 */
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}