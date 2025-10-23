package com.example.langchain4j.agents.app;

import com.example.langchain4j.agents.tools.TemperatureTool;
import com.example.langchain4j.agents.tools.WeatherTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing tools as HTTP endpoints for Azure AI Agent Service.
 * These endpoints match the OpenAPI specification for agent tool calling.
 */
@RestController
@RequestMapping("/api/tools")
public class ToolsController {

    private static final Logger log = LoggerFactory.getLogger(ToolsController.class);

    private final WeatherTool weatherTool;
    private final TemperatureTool temperatureTool;

    public ToolsController(WeatherTool weatherTool, TemperatureTool temperatureTool) {
        this.weatherTool = weatherTool;
        this.temperatureTool = temperatureTool;
    }

    // ========== Weather Tool Endpoints ==========

    @PostMapping("/weather/current")
    public ResponseEntity<Map<String, Object>> getCurrentWeather(@RequestBody Map<String, String> request) {
        String location = request.get("location");
        log.info("Tool call: getCurrentWeather for location: {}", location);

        try {
            String result = weatherTool.getCurrentWeather(location);
            
            // Parse the mock result to return structured data
            return ResponseEntity.ok(Map.of(
                "location", location,
                "description", result
            ));
        } catch (Exception e) {
            log.error("Error getting weather", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/weather/forecast")
    public ResponseEntity<Map<String, Object>> getWeatherForecast(@RequestBody Map<String, Object> request) {
        String location = (String) request.get("location");
        int days = ((Number) request.get("days")).intValue();
        log.info("Tool call: getWeatherForecast for location: {}, days: {}", location, days);

        try {
            String result = weatherTool.getWeatherForecast(location, days);
            
            return ResponseEntity.ok(Map.of(
                "location", location,
                "days", days,
                "forecast", result
            ));
        } catch (Exception e) {
            log.error("Error getting forecast", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ========== Temperature Conversion Tool Endpoints ==========

    @PostMapping("/temperature/celsius-to-fahrenheit")
    public ResponseEntity<Map<String, Object>> celsiusToFahrenheit(@RequestBody Map<String, Object> request) {
        double celsius = ((Number) request.get("celsius")).doubleValue();
        log.info("Tool call: celsiusToFahrenheit({})", celsius);

        try {
            String result = temperatureTool.celsiusToFahrenheit(celsius);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/temperature/fahrenheit-to-celsius")
    public ResponseEntity<Map<String, Object>> fahrenheitToCelsius(@RequestBody Map<String, Object> request) {
        double fahrenheit = ((Number) request.get("fahrenheit")).doubleValue();
        log.info("Tool call: fahrenheitToCelsius({})", fahrenheit);

        try {
            String result = temperatureTool.fahrenheitToCelsius(fahrenheit);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/temperature/celsius-to-kelvin")
    public ResponseEntity<Map<String, Object>> celsiusToKelvin(@RequestBody Map<String, Object> request) {
        double celsius = ((Number) request.get("celsius")).doubleValue();
        log.info("Tool call: celsiusToKelvin({})", celsius);

        try {
            String result = temperatureTool.celsiusToKelvin(celsius);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/temperature/kelvin-to-celsius")
    public ResponseEntity<Map<String, Object>> kelvinToCelsius(@RequestBody Map<String, Object> request) {
        double kelvin = ((Number) request.get("kelvin")).doubleValue();
        log.info("Tool call: kelvinToCelsius({})", kelvin);

        try {
            String result = temperatureTool.kelvinToCelsius(kelvin);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid temperature value");
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/temperature/fahrenheit-to-kelvin")
    public ResponseEntity<Map<String, Object>> fahrenheitToKelvin(@RequestBody Map<String, Object> request) {
        double fahrenheit = ((Number) request.get("fahrenheit")).doubleValue();
        log.info("Tool call: fahrenheitToKelvin({})", fahrenheit);

        try {
            String result = temperatureTool.fahrenheitToKelvin(fahrenheit);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/temperature/kelvin-to-fahrenheit")
    public ResponseEntity<Map<String, Object>> kelvinToFahrenheit(@RequestBody Map<String, Object> request) {
        double kelvin = ((Number) request.get("kelvin")).doubleValue();
        log.info("Tool call: kelvinToFahrenheit({})", kelvin);

        try {
            String result = temperatureTool.kelvinToFahrenheit(kelvin);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid temperature value");
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error in temperature conversion", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "tools",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
