package com.example.langchain4j.agents.app;

import com.example.langchain4j.agents.tools.CalculatorTool;
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
    private final CalculatorTool calculatorTool;

    public ToolsController(WeatherTool weatherTool, CalculatorTool calculatorTool) {
        this.weatherTool = weatherTool;
        this.calculatorTool = calculatorTool;
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

    // ========== Calculator Tool Endpoints ==========

    @PostMapping("/calculator/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, Object> request) {
        double a = ((Number) request.get("a")).doubleValue();
        double b = ((Number) request.get("b")).doubleValue();
        log.info("Tool call: add({}, {})", a, b);

        try {
            double result = calculatorTool.add(a, b);
            return ResponseEntity.ok(Map.of(
                "result", result,
                "operation", "addition"
            ));
        } catch (Exception e) {
            log.error("Error in addition", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculator/subtract")
    public ResponseEntity<Map<String, Object>> subtract(@RequestBody Map<String, Object> request) {
        double a = ((Number) request.get("a")).doubleValue();
        double b = ((Number) request.get("b")).doubleValue();
        log.info("Tool call: subtract({}, {})", a, b);

        try {
            double result = calculatorTool.subtract(a, b);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in subtraction", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculator/multiply")
    public ResponseEntity<Map<String, Object>> multiply(@RequestBody Map<String, Object> request) {
        double a = ((Number) request.get("a")).doubleValue();
        double b = ((Number) request.get("b")).doubleValue();
        log.info("Tool call: multiply({}, {})", a, b);

        try {
            double result = calculatorTool.multiply(a, b);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error in multiplication", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculator/divide")
    public ResponseEntity<Map<String, Object>> divide(@RequestBody Map<String, Object> request) {
        double a = ((Number) request.get("a")).doubleValue();
        double b = ((Number) request.get("b")).doubleValue();
        log.info("Tool call: divide({}, {})", a, b);

        try {
            double result = calculatorTool.divide(a, b);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            log.warn("Division by zero attempted");
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Division by zero"));
        } catch (Exception e) {
            log.error("Error in division", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculator/power")
    public ResponseEntity<Map<String, Object>> power(@RequestBody Map<String, Object> request) {
        double base = ((Number) request.get("base")).doubleValue();
        double exponent = ((Number) request.get("exponent")).doubleValue();
        log.info("Tool call: power({}, {})", base, exponent);

        try {
            double result = calculatorTool.power(base, exponent);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            log.error("Error calculating power", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculator/sqrt")
    public ResponseEntity<Map<String, Object>> squareRoot(@RequestBody Map<String, Object> request) {
        double number = ((Number) request.get("number")).doubleValue();
        log.info("Tool call: sqrt({})", number);

        try {
            double result = calculatorTool.squareRoot(number);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            log.warn("Square root of negative number attempted");
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Cannot calculate square root of negative number"));
        } catch (Exception e) {
            log.error("Error calculating square root", e);
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
