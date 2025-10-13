package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {

        @Test
        @DisplayName("Should add two positive numbers")
        void shouldAddPositiveNumbers() {
            assertEquals(5, calculator.add(2, 3));
        }

        @Test
        @DisplayName("Should add positive and negative numbers")
        void shouldAddPositiveAndNegativeNumbers() {
            assertEquals(1, calculator.add(3, -2));
        }

        @Test
        @DisplayName("Should add two negative numbers")
        void shouldAddNegativeNumbers() {
            assertEquals(-5, calculator.add(-2, -3));
        }

        @Test
        @DisplayName("Should add zero")
        void shouldAddZero() {
            assertEquals(5, calculator.add(5, 0));
            assertEquals(5, calculator.add(0, 5));
        }
    }

    @Nested
    @DisplayName("Subtraction Tests")
    class SubtractionTests {

        @Test
        @DisplayName("Should subtract two positive numbers")
        void shouldSubtractPositiveNumbers() {
            assertEquals(2, calculator.subtract(5, 3));
        }

        @Test
        @DisplayName("Should subtract negative from positive")
        void shouldSubtractNegativeFromPositive() {
            assertEquals(7, calculator.subtract(5, -2));
        }

        @Test
        @DisplayName("Should result in negative")
        void shouldResultInNegative() {
            assertEquals(-2, calculator.subtract(3, 5));
        }
    }

    @Nested
    @DisplayName("Multiplication Tests")
    class MultiplicationTests {

        @Test
        @DisplayName("Should multiply two positive numbers")
        void shouldMultiplyPositiveNumbers() {
            assertEquals(6, calculator.multiply(2, 3));
        }

        @Test
        @DisplayName("Should multiply by zero")
        void shouldMultiplyByZero() {
            assertEquals(0, calculator.multiply(5, 0));
        }

        @Test
        @DisplayName("Should multiply positive and negative")
        void shouldMultiplyPositiveAndNegative() {
            assertEquals(-6, calculator.multiply(2, -3));
        }

        @Test
        @DisplayName("Should multiply two negative numbers")
        void shouldMultiplyNegativeNumbers() {
            assertEquals(6, calculator.multiply(-2, -3));
        }
    }

    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {

        @Test
        @DisplayName("Should divide two numbers")
        void shouldDivideTwoNumbers() {
            assertEquals(2.0, calculator.divide(6, 3));
        }

        @Test
        @DisplayName("Should divide with decimal result")
        void shouldDivideWithDecimal() {
            assertEquals(2.5, calculator.divide(5, 2));
        }

        @Test
        @DisplayName("Should throw exception when dividing by zero")
        void shouldThrowExceptionWhenDividingByZero() {
            ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculator.divide(5, 0)
            );
            assertEquals("Cannot divide by zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should divide negative numbers")
        void shouldDivideNegativeNumbers() {
            assertEquals(-2.0, calculator.divide(-6, 3));
            assertEquals(2.0, calculator.divide(-6, -3));
        }
    }

    @Nested
    @DisplayName("Square Tests")
    class SquareTests {

        @Test
        @DisplayName("Should square positive number")
        void shouldSquarePositiveNumber() {
            assertEquals(25, calculator.square(5));
        }

        @Test
        @DisplayName("Should square negative number")
        void shouldSquareNegativeNumber() {
            assertEquals(25, calculator.square(-5));
        }

        @Test
        @DisplayName("Should square zero")
        void shouldSquareZero() {
            assertEquals(0, calculator.square(0));
        }
    }

    @Nested
    @DisplayName("Absolute Value Tests")
    class AbsoluteValueTests {

        @Test
        @DisplayName("Should return absolute value of positive number")
        void shouldReturnAbsoluteValueOfPositive() {
            assertEquals(5, calculator.abs(5));
        }

        @Test
        @DisplayName("Should return absolute value of negative number")
        void shouldReturnAbsoluteValueOfNegative() {
            assertEquals(5, calculator.abs(-5));
        }

        @Test
        @DisplayName("Should return zero for zero")
        void shouldReturnZeroForZero() {
            assertEquals(0, calculator.abs(0));
        }
    }
}

