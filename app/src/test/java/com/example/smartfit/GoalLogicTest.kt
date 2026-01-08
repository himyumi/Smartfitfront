package com.example.smartfit


import org.junit.Test
import org.junit.Assert.*

class GoalLogicTest {

    // Test 1: Verify that a normal number string becomes an Int
    @Test
    fun input_validNumber_convertsCorrectly() {
        val input = "5000"
        // This simulates: stepsGoal.toIntOrNull() ?: 0
        val result = input.toIntOrNull() ?: 0

        assertEquals(5000, result)
    }

    // Test 2: Verify that letters (invalid input) become 0
    @Test
    fun input_letters_returnsZero() {
        val input = "five"
        // This ensures your app doesn't crash if someone types words
        val result = input.toIntOrNull() ?: 0

        assertEquals(0, result)
    }

    // Test 3: Verify that empty input becomes 0
    @Test
    fun input_empty_returnsZero() {
        val input = ""
        val result = input.toIntOrNull() ?: 0

        assertEquals(0, result)
    }
}
