package com.example.smartfit

import org.junit.Test
import org.junit.Assert.*

class ActivityLogicTest {

    // Test 1: Adding an activity increases the list count
    @Test
    fun addActivity_increasesListSize() {
        // Arrange
        val currentList = emptyList<ActivityItem>()
        val newItem = ActivityItem(name = "Running", duration = "30", calories = "300")

        // Act
        val updatedList = currentList + newItem

        // Assert
        assertEquals(1, updatedList.size)
        assertEquals("Running", updatedList[0].name)
    }

    // Test 2: Deleting an activity removes it
    @Test
    fun deleteActivity_removesItem() {
        // Arrange
        val item1 = ActivityItem(name = "Run", duration = "30", calories = "300")
        val item2 = ActivityItem(name = "Swim", duration = "45", calories = "400")
        val currentList = listOf(item1, item2)

        // Act (Remove the first one)
        val updatedList = currentList - item1

        // Assert
        assertEquals(1, updatedList.size)
        assertEquals("Swim", updatedList[0].name) // Swim should be left
    }
}