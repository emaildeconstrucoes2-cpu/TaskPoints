package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CategoryEntity
import com.example.data.ConversionEntity
import com.example.data.PreferencesManager
import com.example.data.SettingsEntity
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayPoint(
    val dayKey: String,
    val dayLabel: String,
    val points: Int,
    val isPeak: Boolean = false
)

data class UiState(
    val tasks: List<TaskEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val conversions: List<ConversionEntity> = emptyList(),
    val settings: SettingsEntity = SettingsEntity(),
    val currentTab: Int = 0,
    val selectedFilter: String = "Todas",
    val availablePoints: Int = 420,
    val todayEarnedPoints: Int = 45,
    val weeklyDays: List<DayPoint> = emptyList(),
    val selectedChartPeriod: String = "Semana",
    val messageSnackbar: String? = null
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val repository: TaskRepository

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Todas")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedChartPeriod = MutableStateFlow(preferencesManager.selectedChartPeriod)
    val selectedChartPeriod: StateFlow<String> = _selectedChartPeriod.asStateFlow()

    private val _messageSnackbar = MutableStateFlow<String?>(null)
    val messageSnackbar: StateFlow<String?> = _messageSnackbar.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = TaskRepository(
            database.taskDao(),
            database.categoryDao(),
            database.conversionDao(),
            database.settingsDao(),
            preferencesManager
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialized(database, application)
        }
    }

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversions: StateFlow<List<ConversionEntity>> = repository.allConversions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val defaultInitialSettings = SettingsEntity(
        id = 1,
        monthlyPoints = preferencesManager.monthlyPoints,
        accumulatedCoins = preferencesManager.accumulatedCoins,
        coinsPer10Pts = preferencesManager.coinsPer10Pts,
        autoMonthlyReset = preferencesManager.autoMonthlyReset,
        earlyAlert = preferencesManager.earlyAlert,
        isGraphZeroed = preferencesManager.isGraphZeroed,
        weeklyPointsString = preferencesManager.weeklyPointsString,
        profileImageUri = preferencesManager.profileImageUri
    )

    val settings: StateFlow<SettingsEntity> = repository.settings
        .combine(MutableStateFlow(Unit)) { s, _ -> s ?: defaultInitialSettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultInitialSettings)

    val uiState: StateFlow<UiState> = combine(
        tasks,
        categories,
        conversions,
        settings,
        _currentTab,
        _selectedFilter,
        _selectedChartPeriod,
        _messageSnackbar
    ) { args ->
        val tList = args[0] as List<TaskEntity>
        val cList = args[1] as List<CategoryEntity>
        val convList = args[2] as List<ConversionEntity>
        val set = args[3] as SettingsEntity
        val tab = args[4] as Int
        val filter = args[5] as String
        val chartPeriod = args[6] as String
        val msg = args[7] as String?

        // Calculate available points strictly from persisted settings
        val calculatedAvailable = set.monthlyPoints.coerceAtLeast(0)

        // Today earned points: reactive from completed tasks
        val completedSum = tList.filter { it.isCompleted }.sumOf { it.points }
        val todayPts = if (set.isGraphZeroed || set.monthlyPoints == 0) {
            0
        } else {
            completedSum
        }

        // Parse weekly points array from persisted DB string
        val weekly = parseWeeklyPoints(
            pointsStr = set.weeklyPointsString,
            isZeroed = set.isGraphZeroed || set.monthlyPoints == 0,
            todayPts = todayPts
        )

        UiState(
            tasks = tList,
            categories = cList,
            conversions = convList,
            settings = set,
            currentTab = tab,
            selectedFilter = filter,
            availablePoints = calculatedAvailable,
            todayEarnedPoints = todayPts,
            weeklyDays = weekly,
            selectedChartPeriod = chartPeriod,
            messageSnackbar = msg
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState(
            availablePoints = preferencesManager.monthlyPoints,
            settings = defaultInitialSettings,
            selectedChartPeriod = preferencesManager.selectedChartPeriod
        )
    )

    private fun parseWeeklyPoints(pointsStr: String, isZeroed: Boolean, todayPts: Int = 0): List<DayPoint> {
        val days = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        val keys = listOf("seg", "ter", "qua", "qui", "sex", "sab", "dom")

        if (isZeroed) {
            return keys.mapIndexed { idx, key ->
                DayPoint(dayKey = key, dayLabel = days[idx], points = 0, isPeak = false)
            }
        }

        val parts = pointsStr.split(",").mapNotNull { it.trim().toIntOrNull() }
        val values = (if (parts.size == 7) parts else listOf(45, 65, 50, 70, 60, 85, 45)).toMutableList()

        val todayIdx = try {
            (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
        } catch (e: Exception) {
            5
        }

        val peakVal = values.maxOrNull() ?: 0

        return values.mapIndexed { idx, pt ->
            DayPoint(
                dayKey = keys[idx],
                dayLabel = days[idx],
                points = pt,
                isPeak = (pt == peakVal && pt > 0)
            )
        }
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setChartPeriod(period: String) {
        _selectedChartPeriod.value = period
        preferencesManager.selectedChartPeriod = period
    }

    fun clearSnackbar() {
        _messageSnackbar.value = null
    }

    fun showMessage(msg: String) {
        _messageSnackbar.value = msg
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val wasCompleted = repository.toggleTaskCompletion(task)
            if (wasCompleted) {
                _messageSnackbar.value = "Tarefa concluída! +${task.points} pontos creditados"
            } else {
                _messageSnackbar.value = "Tarefa desmarcada"
            }
        }
    }

    fun addTask(
        title: String,
        category: String,
        priority: String,
        points: Int,
        scheduledTime: String,
        linkUrl: String?,
        imageUri: String?
    ) {
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title.trim(),
                category = category,
                priority = priority,
                points = points,
                scheduledTime = scheduledTime,
                isCompleted = false,
                linkUrl = if (linkUrl.isNullOrBlank()) null else linkUrl.trim(),
                imageUri = if (imageUri.isNullOrBlank()) null else imageUri.trim()
            )
            repository.insertTask(newTask)
            _messageSnackbar.value = "Nova missão adicionada: +$points pts!"
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
            _messageSnackbar.value = "Tarefa removida"
        }
    }

    fun convertPoints(pointsToConvert: Int, coinsToEarn: Int) {
        viewModelScope.launch {
            val curSettings = settings.value
            repository.convertPointsToCoins(
                pointsToConvert = pointsToConvert,
                coinsEarned = coinsToEarn,
                monthName = curSettings.currentMonthName,
                monthCode = curSettings.currentMonthName.take(3).uppercase()
            )
            _messageSnackbar.value = "Sucesso! $pointsToConvert pts trocados por $coinsToEarn Moedas!"
        }
    }

    fun updateMonthlyPoints(points: Int) {
        viewModelScope.launch {
            val safePoints = points.coerceAtLeast(0)
            repository.updateMonthlyPoints(safePoints)
            _messageSnackbar.value = "Pontos do mês definidos para $safePoints pts!"
        }
    }

    fun updateProfileImage(uri: String?) {
        viewModelScope.launch {
            repository.updateProfileImage(uri)
            _messageSnackbar.value = if (uri != null) "Sua foto de perfil foi atualizada com sucesso!" else "Foto de perfil removida."
        }
    }

    fun updateConversionRate(newRate: Int) {
        viewModelScope.launch {
            if (newRate in 1..20) {
                repository.updateConversionRate(newRate)
            }
        }
    }

    fun updateCategoryPoints(categoryId: Long, newPoints: Int) {
        viewModelScope.launch {
            if (newPoints in 5..500) {
                repository.updateCategoryPoints(categoryId, newPoints)
            }
        }
    }

    fun addNewCategory(name: String, desc: String, points: Int) {
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    description = desc.trim(),
                    points = points,
                    iconType = "star"
                )
            )
            _messageSnackbar.value = "Nova categoria adicionada com sucesso!"
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _messageSnackbar.value = "Categoria removida"
        }
    }

    fun updateToggles(reset: Boolean, alert: Boolean) {
        viewModelScope.launch {
            repository.updateToggles(reset, alert)
        }
    }

    fun restoreDefaults() {
        viewModelScope.launch {
            repository.restoreDefaultCategories()
            _messageSnackbar.value = "Padrões recomendados restaurados!"
        }
    }

    fun resetAllEconomyPointsAndGraph() {
        viewModelScope.launch {
            repository.resetAllEconomyPointsAndGraph()
            _messageSnackbar.value = "Pontos, moedas e gráfico foram zerados com sucesso!"
        }
    }

    fun resetGraphOnly() {
        viewModelScope.launch {
            repository.resetGraphOnly()
            _messageSnackbar.value = "Gráfico semanal zerado com sucesso!"
        }
    }

    fun resetPointsAndCoinsOnly() {
        viewModelScope.launch {
            repository.resetPointsAndCoinsOnly()
            _messageSnackbar.value = "Pontos e moedas zerados com sucesso!"
        }
    }
}
