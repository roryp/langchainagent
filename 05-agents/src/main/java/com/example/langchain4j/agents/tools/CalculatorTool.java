package com.example.langchain4j.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Calculator tool for mathematical operations.
 * Demonstrates tool parameter handling with LangChain4j.
 */
@Component
public class CalculatorTool {

    private static final Logger log = LoggerFactory.getLogger(CalculatorTool.class);

    /**
     * Calculate the sum of two numbers.
     */
    @Tool("Calculate the sum of two numbers")
    public double add(
            @P("First number") double a, 
            @P("Second number") double b) {
        log.info("Calculating {} + {}", a, b);
        return a + b;
    }

    /**
     * Calculate the difference between two numbers.
     */
    @Tool("Calculate the difference between two numbers")
    public double subtract(
            @P("First number (minuend)") double a, 
            @P("Second number (subtrahend)") double b) {
        log.info("Calculating {} - {}", a, b);
        return a - b;
    }

    /**
     * Calculate the product of two numbers.
     */
    @Tool("Calculate the product of two numbers")
    public double multiply(
            @P("First number") double a, 
            @P("Second number") double b) {
        log.info("Calculating {} * {}", a, b);
        return a * b;
    }

    /**
     * Calculate the division of two numbers.
     */
    @Tool("Calculate the division of two numbers")
    public double divide(
            @P("Dividend") double a, 
            @P("Divisor") double b) {
        log.info("Calculating {} / {}", a, b);
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    /**
     * Calculate the power of a number.
     */
    @Tool("Calculate the power of a number")
    public double power(
            @P("Base number") double base, 
            @P("Exponent") double exponent) {
        log.info("Calculating {} ^ {}", base, exponent);
        return Math.pow(base, exponent);
    }

    /**
     * Calculate the square root of a number.
     */
    @Tool("Calculate the square root of a number")
    public double squareRoot(@P("Number") double number) {
        log.info("Calculating square root of {}", number);
        if (number < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        return Math.sqrt(number);
    }
}
