package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ProfilePictureDialog
import com.example.ui.screens.ChartsScreen
import com.example.ui.screens.ConverterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NeoEmerald
import com.example.ui.theme.NeoEmeraldDark
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val navItems = listOf(
        BottomNavItem("Tarefas", Icons.Default.Checklist, "nav_item_tarefas"),
        BottomNavItem("Gráficos", Icons.AutoMirrored.Filled.ShowChart, "nav_item_graficos"),
        BottomNavItem("Conversor", Icons.Default.CurrencyExchange, "nav_item_conversor"),
        BottomNavItem("Ajustes", Icons.Default.Tune, "nav_item_ajustes")
    )

    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.messageSnackbar) {
        uiState.messageSnackbar?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (uiState.currentTab != 0) {
        BackHandler {
            viewModel.selectTab(0)
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        containerColor = SurfaceDark,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = SurfaceContainerHigh,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceContainerLow,
                contentColor = OnSurface,
                tonalElevation = 0.dp,
                windowInsets = NavigationBarDefaults.windowInsets,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = CardBorder,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = uiState.currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) NeoEmerald else OnSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) NeoEmerald else OnSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeoEmerald,
                            unselectedIconColor = OnSurfaceVariant,
                            selectedTextColor = NeoEmerald,
                            unselectedTextColor = OnSurfaceVariant,
                            indicatorColor = Color(0x1F10B981)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceDark)
        ) {
            Crossfade(targetState = uiState.currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    0 -> TasksScreen(
                        tasks = uiState.tasks,
                        categories = uiState.categories,
                        settings = uiState.settings,
                        availablePoints = uiState.availablePoints,
                        todayEarnedPoints = uiState.todayEarnedPoints,
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelect = { viewModel.setFilter(it) },
                        onToggleTask = { viewModel.toggleTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onAddTask = { title, cat, prio, pts, time, link, img ->
                            viewModel.addTask(title, cat, prio, pts, time, link, img)
                        },
                        onUpdateMonthlyPoints = { viewModel.updateMonthlyPoints(it) },
                        onProfileClick = { showProfileDialog = true },
                        onGoToConverter = { viewModel.selectTab(2) }
                    )

                    1 -> ChartsScreen(
                        weeklyDays = uiState.weeklyDays,
                        settings = uiState.settings,
                        availablePoints = uiState.availablePoints,
                        completedTasksCount = uiState.tasks.count { it.isCompleted },
                        selectedPeriod = uiState.selectedChartPeriod,
                        onPeriodSelect = { viewModel.setChartPeriod(it) },
                        onProfileClick = { showProfileDialog = true },
                        onGoToConverter = { viewModel.selectTab(2) }
                    )

                    2 -> ConverterScreen(
                        conversions = uiState.conversions,
                        settings = uiState.settings,
                        availablePoints = uiState.availablePoints,
                        onConvert = { pts, coins ->
                            viewModel.convertPoints(pts, coins)
                        },
                        onProfileClick = { showProfileDialog = true },
                        onOpenSettings = { viewModel.selectTab(3) }
                    )

                    3 -> SettingsScreen(
                        categories = uiState.categories,
                        settings = uiState.settings,
                        onUpdateRate = { viewModel.updateConversionRate(it) },
                        onUpdateCategoryPoints = { id, pts -> viewModel.updateCategoryPoints(id, pts) },
                        onAddCategory = { name, desc, pts -> viewModel.addNewCategory(name, desc, pts) },
                        onDeleteCategory = { viewModel.deleteCategory(it) },
                        onUpdateToggles = { reset, alert -> viewModel.updateToggles(reset, alert) },
                        onRestoreDefaults = { viewModel.restoreDefaults() },
                        onResetAll = { viewModel.resetAllEconomyPointsAndGraph() },
                        onResetGraphOnly = { viewModel.resetGraphOnly() },
                        onResetPointsAndCoinsOnly = { viewModel.resetPointsAndCoinsOnly() },
                        onShowMessage = { viewModel.showMessage(it) },
                        onUpdateMonthlyPoints = { viewModel.updateMonthlyPoints(it) },
                        onProfileClick = { showProfileDialog = true }
                    )
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfilePictureDialog(
            currentImageUri = uiState.settings.profileImageUri,
            onDismiss = { showProfileDialog = false },
            onSaveImage = { uri ->
                viewModel.updateProfileImage(uri)
                showProfileDialog = false
            }
        )
    }
}
