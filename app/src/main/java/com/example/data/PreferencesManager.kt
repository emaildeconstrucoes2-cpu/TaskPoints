package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskpoints_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MONTHLY_POINTS = "monthly_points"
        private const val KEY_ACCUMULATED_COINS = "accumulated_coins"
        private const val KEY_COINS_PER_10_PTS = "coins_per_10_pts"
        private const val KEY_AUTO_MONTHLY_RESET = "auto_monthly_reset"
        private const val KEY_EARLY_ALERT = "early_alert"
        private const val KEY_IS_GRAPH_ZEROED = "is_graph_zeroed"
        private const val KEY_WEEKLY_POINTS = "weekly_points"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        private const val KEY_SELECTED_CHART_PERIOD = "selected_chart_period"
        private const val KEY_HAS_INITIALIZED = "has_initialized_defaults"
        private const val KEY_HAS_INITIALIZED_TASKS = "has_initialized_tasks"
        private const val KEY_DELETED_TASK_TITLES = "deleted_task_titles"

        // Default initial values
        const val DEFAULT_POINTS = 420
        const val DEFAULT_COINS = 84
        const val DEFAULT_RATE = 2
        const val DEFAULT_WEEKLY_POINTS = "45,65,50,70,60,85,45"
    }

    var hasInitializedDefaults: Boolean
        get() = prefs.getBoolean(KEY_HAS_INITIALIZED, false)
        set(value) { prefs.edit().putBoolean(KEY_HAS_INITIALIZED, value).commit() }

    var hasInitializedTasks: Boolean
        get() = prefs.getBoolean(KEY_HAS_INITIALIZED_TASKS, false)
        set(value) { prefs.edit().putBoolean(KEY_HAS_INITIALIZED_TASKS, value).commit() }

    var deletedTaskTitles: Set<String>
        get() = prefs.getStringSet(KEY_DELETED_TASK_TITLES, emptySet()) ?: emptySet()
        set(value) { prefs.edit().putStringSet(KEY_DELETED_TASK_TITLES, value).commit() }

    fun markTaskAsDeleted(title: String) {
        val updated = deletedTaskTitles.toMutableSet()
        updated.add(title.trim().lowercase())
        deletedTaskTitles = updated
    }

    fun unmarkTaskAsDeleted(title: String) {
        val updated = deletedTaskTitles.toMutableSet()
        updated.remove(title.trim().lowercase())
        deletedTaskTitles = updated
    }

    fun isTaskMarkedDeleted(title: String): Boolean {
        return deletedTaskTitles.contains(title.trim().lowercase())
    }

    var monthlyPoints: Int
        get() = prefs.getInt(KEY_MONTHLY_POINTS, DEFAULT_POINTS)
        set(value) { prefs.edit().putInt(KEY_MONTHLY_POINTS, value.coerceAtLeast(0)).commit() }

    var accumulatedCoins: Int
        get() = prefs.getInt(KEY_ACCUMULATED_COINS, DEFAULT_COINS)
        set(value) { prefs.edit().putInt(KEY_ACCUMULATED_COINS, value.coerceAtLeast(0)).commit() }

    var coinsPer10Pts: Int
        get() = prefs.getInt(KEY_COINS_PER_10_PTS, DEFAULT_RATE)
        set(value) { prefs.edit().putInt(KEY_COINS_PER_10_PTS, value.coerceIn(1, 20)).commit() }

    var autoMonthlyReset: Boolean
        get() = prefs.getBoolean(KEY_AUTO_MONTHLY_RESET, true)
        set(value) { prefs.edit().putBoolean(KEY_AUTO_MONTHLY_RESET, value).commit() }

    var earlyAlert: Boolean
        get() = prefs.getBoolean(KEY_EARLY_ALERT, true)
        set(value) { prefs.edit().putBoolean(KEY_EARLY_ALERT, value).commit() }

    var isGraphZeroed: Boolean
        get() = prefs.getBoolean(KEY_IS_GRAPH_ZEROED, false)
        set(value) { prefs.edit().putBoolean(KEY_IS_GRAPH_ZEROED, value).commit() }

    var weeklyPointsString: String
        get() = prefs.getString(KEY_WEEKLY_POINTS, DEFAULT_WEEKLY_POINTS) ?: DEFAULT_WEEKLY_POINTS
        set(value) { prefs.edit().putString(KEY_WEEKLY_POINTS, value).commit() }

    var profileImageUri: String?
        get() = prefs.getString(KEY_PROFILE_IMAGE_URI, null)
        set(value) { prefs.edit().putString(KEY_PROFILE_IMAGE_URI, value).commit() }

    var selectedChartPeriod: String
        get() = prefs.getString(KEY_SELECTED_CHART_PERIOD, "Semana") ?: "Semana"
        set(value) { prefs.edit().putString(KEY_SELECTED_CHART_PERIOD, value).commit() }

    fun resetAll() {
        prefs.edit()
            .putInt(KEY_MONTHLY_POINTS, 0)
            .putInt(KEY_ACCUMULATED_COINS, 0)
            .putBoolean(KEY_IS_GRAPH_ZEROED, true)
            .putString(KEY_WEEKLY_POINTS, "0,0,0,0,0,0,0")
            .commit()
    }

    fun resetGraphOnly() {
        prefs.edit()
            .putBoolean(KEY_IS_GRAPH_ZEROED, true)
            .putString(KEY_WEEKLY_POINTS, "0,0,0,0,0,0,0")
            .commit()
    }

    fun resetPointsAndCoinsOnly() {
        prefs.edit()
            .putInt(KEY_MONTHLY_POINTS, 0)
            .putInt(KEY_ACCUMULATED_COINS, 0)
            .commit()
    }

    fun restoreDefaults() {
        prefs.edit()
            .putInt(KEY_MONTHLY_POINTS, DEFAULT_POINTS)
            .putInt(KEY_ACCUMULATED_COINS, DEFAULT_COINS)
            .putInt(KEY_COINS_PER_10_PTS, DEFAULT_RATE)
            .putBoolean(KEY_AUTO_MONTHLY_RESET, true)
            .putBoolean(KEY_EARLY_ALERT, true)
            .putBoolean(KEY_IS_GRAPH_ZEROED, false)
            .putString(KEY_WEEKLY_POINTS, DEFAULT_WEEKLY_POINTS)
            .commit()
    }
}
