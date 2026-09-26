package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        ConversionEntity::class,
        SettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun conversionDao(): ConversionDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taskpoints_database"
                )
                .addCallback(DatabaseCallback(context.applicationContext, scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = db
                db
            }
        }

        private class DatabaseCallback(
            private val appContext: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.let { populateInitialData(it, appContext) }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.let { populateInitialData(it, appContext) }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase, appContext: Context) {
            val taskDao = database.taskDao()
            val categoryDao = database.categoryDao()
            val conversionDao = database.conversionDao()
            val settingsDao = database.settingsDao()

            val existingSettings = settingsDao.getSettingsSync()
            val prefs = PreferencesManager(appContext)

            // Initial settings if not present
            if (existingSettings == null) {
                settingsDao.insertOrUpdate(
                    SettingsEntity(
                        id = 1,
                        monthlyPoints = prefs.monthlyPoints,
                        accumulatedCoins = prefs.accumulatedCoins,
                        coinsPer10Pts = prefs.coinsPer10Pts,
                        autoMonthlyReset = prefs.autoMonthlyReset,
                        earlyAlert = prefs.earlyAlert,
                        isGraphZeroed = prefs.isGraphZeroed,
                        weeklyPointsString = prefs.weeklyPointsString,
                        profileImageUri = prefs.profileImageUri,
                        currentMonthName = "Outubro",
                        cycleDaysRemaining = 12,
                        currentCycleDay = 19,
                        cycleTotalDays = 31,
                        activeStreakDays = 8,
                        dailyRecordPoints = 95
                    )
                )
            } else {
                // Ensure PreferencesManager is kept in sync with database
                prefs.monthlyPoints = existingSettings.monthlyPoints
                prefs.accumulatedCoins = existingSettings.accumulatedCoins
                prefs.coinsPer10Pts = existingSettings.coinsPer10Pts
                prefs.autoMonthlyReset = existingSettings.autoMonthlyReset
                prefs.earlyAlert = existingSettings.earlyAlert
                prefs.isGraphZeroed = existingSettings.isGraphZeroed
                prefs.weeklyPointsString = existingSettings.weeklyPointsString
                prefs.profileImageUri = existingSettings.profileImageUri
            }

                // Initial Categories matching Screen 4 if not present
                if (categoryDao.getCount() == 0) {
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
                }

                // Initial Conversion History matching Screen 3 if not present
                if (conversionDao.getCount() == 0) {
                    conversionDao.insertAll(
                        listOf(
                            ConversionEntity(
                                monthCode = "SET",
                                monthName = "Setembro",
                                pointsConverted = 520,
                                coinsEarned = 104,
                                timestamp = System.currentTimeMillis() - 86400000L * 25
                            ),
                            ConversionEntity(
                                monthCode = "AGO",
                                monthName = "Agosto",
                                pointsConverted = 480,
                                coinsEarned = 96,
                                timestamp = System.currentTimeMillis() - 86400000L * 55
                            ),
                            ConversionEntity(
                                monthCode = "JUL",
                                monthName = "Julho",
                                pointsConverted = 610,
                                coinsEarned = 122,
                                timestamp = System.currentTimeMillis() - 86400000L * 85
                            )
                        )
                    )
                }

                // Initial Tasks matching Screen 1 - only populate once on first install
                if (!prefs.hasInitializedTasks) {
                    val initialTasks = listOf(
                        TaskEntity(
                            title = "Finalizar relatório trimestral de desempenho",
                            category = "Trabalho",
                            priority = "14:30 • Alta prioridade",
                            points = 30,
                            scheduledTime = "14:30",
                            isCompleted = false,
                            linkUrl = "https://docs.google.com/spreadsheets"
                        ),
                        TaskEntity(
                            title = "Treino de força na academia (45 min)",
                            category = "Saúde",
                            priority = "Concluída • Creditado",
                            points = 45,
                            scheduledTime = "07:30",
                            isCompleted = true,
                            completedAt = System.currentTimeMillis() - 3600000L
                        ),
                        TaskEntity(
                            title = "Estudar módulo 3 de UI/UX Design",
                            category = "Estudos",
                            priority = "18:00 • Foco",
                            points = 20,
                            scheduledTime = "18:00",
                            isCompleted = false,
                            linkUrl = "https://figma.com",
                            imageUri = "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?w=800&auto=format&fit=crop&q=80"
                        ),
                        TaskEntity(
                            title = "Beber 2.5L de água e bater metas de hidratação",
                            category = "Saúde",
                            priority = "Concluída",
                            points = 10,
                            scheduledTime = "12:00",
                            isCompleted = true,
                            completedAt = System.currentTimeMillis() - 7200000L
                        ),
                        TaskEntity(
                            title = "Reunião de alinhamento com equipe de produto",
                            category = "Trabalho",
                            priority = "16:00 • Google Meet",
                            points = 20,
                            scheduledTime = "16:00",
                            isCompleted = false,
                            linkUrl = "https://meet.google.com"
                        ),
                        TaskEntity(
                            title = "Leitura de 20 páginas de livro",
                            category = "Desenvolvimento",
                            priority = "21:30 • Noite",
                            points = 15,
                            scheduledTime = "21:30",
                            isCompleted = false,
                            linkUrl = "https://openlibrary.org"
                        )
                    )
                    for (task in initialTasks) {
                        if (!prefs.isTaskMarkedDeleted(task.title)) {
                            taskDao.insertTask(task)
                        }
                    }
                    prefs.hasInitializedTasks = true
                }
            }
        }
    }
