package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TaskPoints", appName)
  }

  @Test
  fun `verify monthly points persistence and update`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.data.PreferencesManager(context)
    
    // Test initial points
    assertEquals(420, prefs.monthlyPoints)

    // Update points
    prefs.monthlyPoints = 550
    assertEquals(550, prefs.monthlyPoints)

    // Re-instantiate PreferencesManager to simulate app restart
    val newPrefs = com.example.data.PreferencesManager(context)
    assertEquals(550, newPrefs.monthlyPoints)
  }

  @Test
  fun `verify weekly points string update on points change`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.data.PreferencesManager(context)
    val initialWeekly = prefs.weeklyPointsString
    val parts = initialWeekly.split(",").mapNotNull { it.trim().toIntOrNull() }
    assertEquals(7, parts.size)

    val todayIdx = try {
      (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
    } catch (e: Exception) {
      5
    }
    val todayOldPoints = parts[todayIdx]

    // Simulate task completion delta +30
    val updatedList = parts.toMutableList()
    updatedList[todayIdx] = todayOldPoints + 30
    prefs.weeklyPointsString = updatedList.joinToString(",")

    val readBack = prefs.weeklyPointsString.split(",").mapNotNull { it.trim().toIntOrNull() }
    assertEquals(todayOldPoints + 30, readBack[todayIdx])
  }

  @Test
  fun `verify deleted tasks tracking and persistence across restart`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.data.PreferencesManager(context)

    val taskTitle = "Finalizar relatório trimestral de desempenho"
    org.junit.Assert.assertFalse(prefs.isTaskMarkedDeleted(taskTitle))

    // Mark task as deleted
    prefs.markTaskAsDeleted(taskTitle)
    org.junit.Assert.assertTrue(prefs.isTaskMarkedDeleted(taskTitle))
    prefs.hasInitializedTasks = true

    // Simulate restart with new PreferencesManager instance
    val restartedPrefs = com.example.data.PreferencesManager(context)
    org.junit.Assert.assertTrue(restartedPrefs.isTaskMarkedDeleted(taskTitle))
    org.junit.Assert.assertTrue(restartedPrefs.hasInitializedTasks)
  }
}
