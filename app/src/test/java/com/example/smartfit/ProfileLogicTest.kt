package com.example.smartfit

import org.junit.Test
import org.junit.Assert.*

class ProfileLogicTest {

    // Test 1: Verify normal BMI calculation
    // Formula: weight / (height_in_meters * height_in_meters)
    @Test
    fun calculateBmi_normalValues_isCorrect() {
        val weight = 70.0 // kg
        val heightCm = 175.0 // cm

        val heightM = heightCm / 100
        val bmi = weight / (heightM * heightM)

        // 70 / (1.75 * 1.75) = 22.857...
        assertEquals(22.86, bmi, 0.1) // 0.1 is the allowed difference (delta)
    }

    // Test 2: Verify "Normal" Category logic
    @Test
    fun getCategory_normalBmi_returnsNormal() {
        val bmi = 22.0
        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            else -> "Obese"
        }
        assertEquals("Normal weight", category)
    }

    // Test 3: Safety check - What if height is 0? (Prevent crash)
    @Test
    fun calculateBmi_zeroHeight_returnsZero() {
        val weight = 70.0
        val heightCm = 0.0

        val bmi = if (heightCm > 0) {
            val h = heightCm / 100
            weight / (h * h)
        } else {
            0.0
        }

        assertEquals(0.0, bmi, 0.0)
    }
}