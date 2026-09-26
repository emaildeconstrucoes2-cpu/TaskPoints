package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao,
    private val conversionDao: ConversionDao,
    private val settingsDao: SettingsDao,
    private val preferencesManager: PreferencesManager
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks().map { list ->
        val deleted = preferencesManager.deletedTaskTitles
        if (deleted.isEmpty()) {
            list
        } else {
            list.filterNot { deleted.contains(it.title.trim().lowercase()) }
        }
    }
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allConversions: Flow<List<ConversionEntity>> = conversionDao.getAllConversions()
    val settings: Flow<SettingsEntity?> = settingsDao.getSettings()

    suspend fun insertTask(task: TaskEntity): Long {
        preferencesManager.unmarkTaskAsDeleted(task.title)
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) {
        preferencesManager.markTaskAsDeleted(task.title)
        taskDao.deleteTaskByTitle(task.title)
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        val task = taskDao.getTaskById(id)
        if (task != null) {
            preferencesManager.markTaskAsDeleted(task.title)
            taskDao.deleteTaskByTitle(task.title)
        }
        taskDao.deleteTaskById(id)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity): Boolean {
        val willBeCompleted = !task.isCompleted
        val timestamp = if (willBeCompleted) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(task.id, willBeCompleted, timestamp)

        // Persist points adjustment in settings database & SharedPreferences atomically
        val currentSettings = settingsDao.getSettingsSync() ?: SettingsEntity(id = 1)
        val currentPoints = currentSettings.monthlyPoints
        val pointsDelta = if (willBeCompleted) task.points else -task.points
        val newPoints = if (willBeCompleted) {
            currentPoints + task.points
        } else {
            (currentPoints - task.points).coerceAtLeast(0)
        }

        val updatedWeekly = calculateUpdatedWeeklyPoints(
            currentWeeklyString = currentSettings.weeklyPointsString,
            pointsDelta = pointsDelta,
            isZeroing = (newPoints == 0)
        )

        val todayIdx = try {
            (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
        } catch (e: Exception) {
            5
        }
        val todayPts = updatedWeekly.split(",").getOrNull(todayIdx)?.trim()?.toIntOrNull() ?: 0
        val newDailyRecord = maxOf(currentSettings.dailyRecordPoints, todayPts)

        preferencesManager.monthlyPoints = newPoints
        preferencesManager.weeklyPointsString = updatedWeekly
        preferencesManager.isGraphZeroed = (newPoints == 0)

        val updatedSettings = currentSettings.copy(
            id = 1,
            monthlyPoints = newPoints,
            weeklyPointsString = updatedWeekly,
            isGraphZeroed = (newPoints == 0),
            dailyRecordPoints = newDailyRecord
        )
        settingsDao.insertOrUpdate(updatedSettings)
        settingsDao.updateMonthlyPoints(newPoints)
        return willBeCompleted
    }

    private fun calculateUpdatedWeeklyPoints(
        currentWeeklyString: String,
        pointsDelta: Int,
        isZeroing: Boolean
    ): String {
        if (isZeroing) {
            return "0,0,0,0,0,0,0"
        }
        val currentWeeklyList = currentWeeklyString.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toMutableList()
        if (currentWeeklyList.size != 7) {
            currentWeeklyList.clear()
            currentWeeklyList.addAll(listOf(45, 65, 50, 70, 60, 85, 45))
        }
        val todayIdx = try {
            (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
        } catch (e: Exception) {
            5
        }
        val currentVal = currentWeeklyList[todayIdx]
        currentWeeklyList[todayIdx] = (currentVal + pointsDelta).coerceAtLeast(0)
        return currentWeeklyList.joinToString(",")
    }

    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun updateCategoryPoints(id: Long, points: Int) = categoryDao.updateCategoryPoints(id, points)

    suspend fun updateSettings(settings: SettingsEntity) {
        preferencesManager.monthlyPoints = settings.monthlyPoints
        preferencesManager.accumulatedCoins = settings.accumulatedCoins
        preferencesManager.coinsPer10Pts = settings.coinsPer10Pts
        preferencesManager.autoMonthlyReset = settings.autoMonthlyReset
        preferencesManager.earlyAlert = settings.earlyAlert
        preferencesManager.isGraphZeroed = settings.isGraphZeroed
        preferencesManager.weeklyPointsString = settings.weeklyPointsString
        preferencesManager.profileImageUri = settings.profileImageUri
        settingsDao.insertOrUpdate(settings.copy(id = 1))
    }

    suspend fun updateMonthlyPoints(points: Int) {
        val safePoints = points.coerceAtLeast(0)
        preferencesManager.monthlyPoints = safePoints
        val current = settingsDao.getSettingsSync() ?: SettingsEntity(id = 1)
        val isZero = (safePoints == 0)

        val updatedWeekly = if (isZero) {
            "0,0,0,0,0,0,0"
        } else {
            val oldPoints = current.monthlyPoints.coerceAtLeast(1)
            val ratio = safePoints.toFloat() / oldPoints.toFloat()
            val list = current.weeklyPointsString.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .ifEmpty { listOf(45, 65, 50, 70, 60, 85, 45) }
            val scaled = list.map { (it * ratio).toInt().coerceAtLeast(0) }
            if (scaled.sum() == 0 && safePoints > 0) {
                val perDay = safePoints / 7
                val rem = safePoints % 7
                List(7) { idx -> perDay + (if (idx == 5) rem else 0) }.joinToString(",")
            } else {
                scaled.joinToString(",")
            }
        }

        val todayIdx = try {
            (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
        } catch (e: Exception) {
            5
        }
        val todayPts = updatedWeekly.split(",").getOrNull(todayIdx)?.trim()?.toIntOrNull() ?: 0
        val newDailyRecord = maxOf(current.dailyRecordPoints, todayPts)

        preferencesManager.weeklyPointsString = updatedWeekly
        preferencesManager.isGraphZeroed = isZero

        val updated = current.copy(
            id = 1,
            monthlyPoints = safePoints,
            weeklyPointsString = updatedWeekly,
            isGraphZeroed = isZero,
            dailyRecordPoints = newDailyRecord
        )
        settingsDao.insertOrUpdate(updated)
        settingsDao.updateMonthlyPoints(safePoints)
    }

    suspend fun ensureInitialized(database: AppDatabase, appContext: android.content.Context) {
        AppDatabase.populateInitialData(database, appContext)
    }

    suspend fun updateProfileImage(uri: String?) {
        preferencesManager.profileImageUri = uri
        val current = settingsDao.getSettingsSync() ?: SettingsEntity()
        settingsDao.insertOrUpdate(current.copy(profileImageUri = uri))
    }

    suspend fun updateConversionRate(rate: Int) {
        val safeRate = rate.coerceIn(1, 20)
        preferencesManager.coinsPer10Pts = safeRate
        val current = settingsDao.getSettingsSync() ?: SettingsEntity()
        settingsDao.insertOrUpdate(current.copy(coinsPer10Pts = safeRate))
    }

    suspend fun updateToggles(reset: Boolean, alert: Boolean) {
        preferencesManager.autoMonthlyReset = reset
        preferencesManager.earlyAlert = alert
        val current = settingsDao.getSettingsSync() ?: SettingsEntity()
        settingsDao.insertOrUpdate(current.copy(autoMonthlyReset = reset, earlyAlert = alert))
    }

    suspend fun convertPointsToCoins(
        pointsToConvert: Int,
        coinsEarned: Int,
        monthName: String,
        monthCode: String
    ) {
        // Save conversion record in DB
        conversionDao.insertConversion(
            ConversionEntity(
                monthCode = monthCode,
                monthName = monthName,
                pointsConverted = pointsToConvert,
                coinsEarned = coinsEarned
            )
        )
        // Deduct points and credit coins in DB and Preferences
        val currentSettings = settingsDao.getSettingsSync() ?: SettingsEntity(id = 1)
        val remainingPoints = (currentSettings.monthlyPoints - pointsToConvert).coerceAtLeast(0)
        val newCoins = currentSettings.accumulatedCoins + coinsEarned

        val updatedWeekly = calculateUpdatedWeeklyPoints(
            currentWeeklyString = currentSettings.weeklyPointsString,
            pointsDelta = -pointsToConvert,
            isZeroing = (remainingPoints == 0)
        )

        preferencesManager.monthlyPoints = remainingPoints
        preferencesManager.accumulatedCoins = newCoins
        preferencesManager.weeklyPointsString = updatedWeekly
        if (remainingPoints == 0) {
            preferencesManager.isGraphZeroed = true
        }

        settingsDao.insertOrUpdate(
            currentSettings.copy(
                id = 1,
                monthlyPoints = remainingPoints,
                accumulatedCoins = newCoins,
                weeklyPointsString = updatedWeekly,
                isGraphZeroed = if (remainingPoints == 0) true else currentSettings.isGraphZeroed
            )
        )
        settingsDao.updateMonthlyPoints(remainingPoints)
    }

    suspend fun restoreDefaultCategories() {
        categoryDao.clearAll()
        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    name = "Normal / Rápida",
                    description = "Até 15 min • Rotinas simples",
                    points = 10,
                    iconType = "timer"
                ),
                CategoryEntity(
                    name = "Média / Importante",
                    description = "30 a 60 min • Foco direto",
                    points = 20,
                    iconType = "check"
                ),
                CategoryEntity(
                    name = "Complexa / Prioridade",
                    description = "Deep Work • Alta exigência",
                    points = 35,
                    iconType = "flame"
                ),
                CategoryEntity(
                    name = "Épica / Semanal",
                    description = "Grandes metas • Conquista mestre",
                    points = 50,
                    iconType = "star"
                )
            )
        )
        // Restore default settings values in DB and Preferences
        preferencesManager.restoreDefaults()
        val current = settingsDao.getSettingsSync() ?: SettingsEntity()
        settingsDao.insertOrUpdate(
            current.copy(
                id = 1,
                monthlyPoints = PreferencesManager.DEFAULT_POINTS,
                accumulatedCoins = PreferencesManager.DEFAULT_COINS,
                coinsPer10Pts = PreferencesManager.DEFAULT_RATE,
                autoMonthlyReset = true,
                earlyAlert = true,
                isGraphZeroed = false,
                weeklyPointsString = PreferencesManager.DEFAULT_WEEKLY_POINTS
            )
        )
    }

    suspend fun resetAllEconomyPointsAndGraph() {
        preferencesManager.resetAll()
        settingsDao.resetAll()
        conversionDao.clearAll()
    }

    suspend fun resetGraphOnly() {
        preferencesManager.resetGraphOnly()
        settingsDao.resetGraph()
    }

    suspend fun resetPointsAndCoinsOnly() {
        preferencesManager.resetPointsAndCoinsOnly()
        settingsDao.resetPointsAndCoins()
    }
}
