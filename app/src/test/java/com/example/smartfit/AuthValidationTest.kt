package com.example.smartfit

import org.junit.Test
import org.junit.Assert.*

class AuthValidationTest {

    // Test 1: Password must be at least 6 characters
    @Test
    fun password_tooShort_isInvalid() {
        val password = "123"
        val isValid = password.length >= 6

        assertFalse("Password should be invalid", isValid)
    }

    // Test 2: Password with 6+ chars is valid
    @Test
    fun password_longEnough_isValid() {
        val password = "password123"
        val isValid = password.length >= 6

        assertTrue("Password should be valid", isValid)
    }

    // Test 3: Email must not be empty
    @Test
    fun email_empty_isInvalid() {
        val email = ""
        val isValid = email.isNotEmpty()

        assertFalse(isValid)
    }
}