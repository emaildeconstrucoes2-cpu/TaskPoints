package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.util.FileUtils
import com.example.data.CategoryEntity
import com.example.data.SettingsEntity
import com.example.data.TaskEntity
import com.example.ui.components.FullImageViewerDialog
import com.example.ui.components.SquircleCheckbox
import com.example.ui.components.TopAppHeader
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NeoEmerald
import com.example.ui.theme.NeoEmeraldDark
import com.example.ui.theme.NeoEmeraldLight
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.SolarAmberDark
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    categories: List<CategoryEntity>,
    settings: SettingsEntity,
    availablePoints: Int,
    todayEarnedPoints: Int = 45,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onAddTask: (String, String, String, Int, String, String?, String?) -> Unit,
    onUpdateMonthlyPoints: (Int) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onGoToConverter: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showAdjustPointsDialog by remember { mutableStateOf(false) }
    var viewingImage by remember { mutableStateOf<Pair<String, String>?>(null) } // (url, title)

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size

    val filteredTasks = remember(tasks, selectedFilter) {
        when (selectedFilter) {
            "Todas" -> tasks
            "Hoje" -> tasks.filter { it.scheduledTime.isNotEmpty() || !it.isCompleted }
            "Trabalho" -> tasks.filter { it.category.contains("Trabalho", ignoreCase = true) }
            "Saúde" -> tasks.filter { it.category.contains("Saúde", ignoreCase = true) }
            "Estudos" -> tasks.filter { it.category.contains("Estudos", ignoreCase = true) }
            "Desenvolvimento" -> tasks.filter { it.category.contains("Desenvolvimento", ignoreCase = true) }
            else -> tasks
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header
            item {
                TopAppHeader(
                    coins = settings.accumulatedCoins,
                    profileImageUri = settings.profileImageUri,
                    onProfileClick = onProfileClick,
                    onCoinsClick = onGoToConverter
                )
            }

            // 2. Monthly Cycle Card ("Ciclo de Outubro")
            item {
                MonthlyCycleCard(
                    monthName = settings.currentMonthName,
                    daysRemaining = settings.cycleDaysRemaining,
                    currentDay = settings.currentCycleDay,
                    totalDays = settings.cycleTotalDays
                )
            }

            // 3. Dual Metrics: Pontos do Mês & Moedas Acumuladas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Metric Card: Pontos do Mês (Clickable to adjust directly!)
                    Card(
                        onClick = { showAdjustPointsDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_monthly_points"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PONTOS DO MÊS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "Ajustar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeoEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$availablePoints",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoEmeraldLight
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "pts",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = NeoEmeraldLight,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = NeoEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (todayEarnedPoints >= 0) "+$todayEarnedPoints hoje" else "$todayEarnedPoints hoje",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeoEmerald,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Right Metric Card: Moedas Acumuladas
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_coins_accumulated"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "MOEDAS ACUMULADAS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${settings.accumulatedCoins}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SolarAmber
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "moedas",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = SolarAmber,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = SolarAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${10 / settings.coinsPer10Pts} pts = 1 moeda",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnSurfaceVariant,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. Highlight Banner: "Converter Pontos em Moedas >"
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Surface(
                        onClick = onGoToConverter,
                        shape = RoundedCornerShape(14.dp),
                        color = SolarAmber,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cta_convert_points_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = SolarAmberDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Converter Pontos em Moedas >",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SolarAmberDark
                                )
                            )
                        }
                    }
                }
            }

            // 5. Section Header & Filter Chips
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Minhas Tarefas",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "$completedCount/$totalCount concluídas",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterPill(
                            label = "Todas",
                            isSelected = selectedFilter == "Todas",
                            onClick = { onFilterSelect("Todas") }
                        )
                        FilterPill(
                            label = "⚡ Hoje",
                            isSelected = selectedFilter == "Hoje",
                            onClick = { onFilterSelect("Hoje") }
                        )
                        FilterPill(
                            label = "💼 Trabalho +25 pts",
                            isSelected = selectedFilter == "Trabalho",
                            onClick = { onFilterSelect("Trabalho") }
                        )
                        FilterPill(
                            label = "🎓 Estudos",
                            isSelected = selectedFilter == "Estudos",
                            onClick = { onFilterSelect("Estudos") }
                        )
                        FilterPill(
                            label = "❤️ Saúde",
                            isSelected = selectedFilter == "Saúde",
                            onClick = { onFilterSelect("Saúde") }
                        )
                        FilterPill(
                            label = "📖 Desenvolvimento",
                            isSelected = selectedFilter == "Desenvolvimento",
                            onClick = { onFilterSelect("Desenvolvimento") }
                        )
                    }
                }
            }

            // 6. Task List Items
            items(filteredTasks, key = { it.id }) { task ->
                TaskCardItem(
                    task = task,
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task.id) },
                    onViewImage = { url, title -> viewingImage = Pair(url, title) }
                )
            }
        }

        // Floating "+ Nova Tarefa" Neo Emerald button
        Surface(
            onClick = { showAddDialog = true },
            shape = RoundedCornerShape(9999.dp),
            color = NeoEmerald,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 20.dp)
                .testTag("floating_add_task_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar tarefa",
                    tint = NeoEmeraldDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Nova Tarefa",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeoEmeraldDark
                    )
                )
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddTaskDialog(
                categories = categories,
                onDismiss = { showAddDialog = false },
                onConfirm = { title, cat, prio, pts, time, link, img ->
                    onAddTask(title, cat, prio, pts, time, link, img)
                    showAddDialog = false
                }
            )
        }

        if (showAdjustPointsDialog) {
            AdjustMonthlyPointsDialog(
                currentPoints = availablePoints,
                onDismiss = { showAdjustPointsDialog = false },
                onConfirm = { newPoints ->
                    onUpdateMonthlyPoints(newPoints)
                    showAdjustPointsDialog = false
                }
            )
        }

        viewingImage?.let { (url, title) ->
            FullImageViewerDialog(
                imageUrl = url,
                title = title,
                onDismiss = { viewingImage = null }
            )
        }
    }
}

@Composable
fun MonthlyCycleCard(
    monthName: String,
    daysRemaining: Int,
    currentDay: Int,
    totalDays: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("monthly_cycle_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ciclo de $monthName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = SurfaceContainerHigh,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "Reinicia em $daysRemaining dias",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { currentDay.toFloat() / totalDays.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(9999.dp)),
                color = NeoEmerald,
                trackColor = SurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Os pontos acumulam no mês e reiniciam dia 01",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
                Text(
                    text = "Dia $currentDay/$totalDays",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeoEmeraldLight
                    )
                )
            }
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(9999.dp),
        color = if (isSelected) NeoEmerald else SurfaceContainerHigh,
        border = BorderStroke(1.dp, if (isSelected) NeoEmerald else CardBorder),
        modifier = Modifier.testTag("filter_pill_$label")
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeoEmeraldDark else OnSurface
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TaskCardItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onViewImage: (url: String, title: String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color(0x1A10B981) else SurfaceContainerLow
        ),
        border = BorderStroke(
            1.dp,
            if (task.isCompleted) NeoEmerald.copy(alpha = 0.3f) else CardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Interactive Checkbox
                SquircleCheckbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggle,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Task Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isCompleted) OnSurfaceVariant else Color.White,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tags & Metadata
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Category Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HyperVioletColor(task.category),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Status or Priority
                        if (task.isCompleted) {
                            Text(
                                text = "Concluída • Creditado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeoEmerald,
                                    fontSize = 11.sp
                                )
                            )
                        } else if (task.priority.isNotEmpty()) {
                            Text(
                                text = task.priority,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Direct Web/HTML Link Pill
                    if (!task.linkUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            onClick = {
                                try {
                                    val uri = if (!task.linkUrl.startsWith("http://") && !task.linkUrl.startsWith("https://")) {
                                        Uri.parse("https://${task.linkUrl}")
                                    } else {
                                        Uri.parse(task.linkUrl)
                                    }
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Não foi possível abrir o link", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(9999.dp),
                            color = SurfaceContainerHigh,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Link externo",
                                    tint = NeoEmeraldLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.linkUrl,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeoEmeraldLight,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 180.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }

                    // Attached Image from Gallery or Web URL
                    if (!task.imageUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .clickable { onViewImage(task.imageUri, task.title) }
                                .padding(4.dp)
                        ) {
                            AsyncImage(
                                model = task.imageUri,
                                contentDescription = "Imagem da tarefa",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Anexo de Imagem",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Toque para ampliar",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Points Badge & Delete Icon
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = if (task.isCompleted) Color(0x3310B981) else SurfaceContainerHigh,
                        border = BorderStroke(1.dp, if (task.isCompleted) NeoEmerald else CardBorder)
                    ) {
                        Text(
                            text = "+${task.points} pts",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) NeoEmerald else NeoEmeraldLight
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("delete_task_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir tarefa",
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun HyperVioletColor(cat: String): Color {
    return when {
        cat.contains("Trabalho", true) -> Color(0xFF818CF8)
        cat.contains("Saúde", true) -> NeoEmerald
        cat.contains("Estudos", true) -> Color(0xFFA78BFA)
        cat.contains("Desenvolvimento", true) -> SolarAmber
        else -> NeoEmeraldLight
    }
}

@Composable
fun AddTaskDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        category: String,
        priority: String,
        points: Int,
        time: String,
        linkUrl: String?,
        imageUri: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first().name else "Trabalho") }
    var points by remember { mutableIntStateOf(if (categories.isNotEmpty()) categories.first().points else 20) }
    var scheduledTime by remember { mutableStateOf("14:30") }
    var priorityNote by remember { mutableStateOf("Alta prioridade") }
    var linkUrl by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var directImageUrl by remember { mutableStateOf("") }
    var isEnteringDirectUrl by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Android Zero-Permission Photo Picker launcher with permanent storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = FileUtils.copyUriToInternalStorage(context, uri, "task_img")
            imageUri = permanentPath
            directImageUrl = ""
        }
    }

    // Android File Picker fallback
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = FileUtils.copyUriToInternalStorage(context, uri, "task_img")
            imageUri = permanentPath
            directImageUrl = ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Nova Tarefa / Missão",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title Field
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título da missão") },
                        placeholder = { Text("Ex: Finalizar relatório financeiro") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeoEmerald,
                            unfocusedBorderColor = OutlineColor,
                            focusedLabelColor = NeoEmerald
                        )
                    )
                }

                // Preset Difficulty / Category
                item {
                    Text(
                        text = "Categoria de Esforço & Pontos:",
                        style = MaterialTheme.typography.labelMedium.copy(color = OnSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isChosen = selectedCategory == cat.name
                            Surface(
                                onClick = {
                                    selectedCategory = cat.name
                                    points = cat.points
                                },
                                shape = RoundedCornerShape(9999.dp),
                                color = if (isChosen) NeoEmerald else SurfaceContainerHigh,
                                border = BorderStroke(1.dp, if (isChosen) NeoEmerald else CardBorder)
                            ) {
                                Text(
                                    text = "${cat.name} (+${cat.points} pts)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isChosen) NeoEmeraldDark else Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Time and Priority
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = scheduledTime,
                            onValueChange = { scheduledTime = it },
                            label = { Text("Horário") },
                            placeholder = { Text("14:30") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeoEmerald,
                                unfocusedBorderColor = OutlineColor
                            )
                        )
                        OutlinedTextField(
                            value = priorityNote,
                            onValueChange = { priorityNote = it },
                            label = { Text("Prioridade / Foco") },
                            placeholder = { Text("Alta prioridade") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeoEmerald,
                                unfocusedBorderColor = OutlineColor
                            )
                        )
                    }
                }

                // Web / HTML Link Input
                item {
                    OutlinedTextField(
                        value = linkUrl,
                        onValueChange = { linkUrl = it },
                        label = { Text("Link URL / HTML / Documentação") },
                        placeholder = { Text("https://... ou figma.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Link, contentDescription = null, tint = NeoEmerald)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_link_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeoEmerald,
                            unfocusedBorderColor = OutlineColor,
                            focusedLabelColor = NeoEmerald
                        )
                    )
                }

                // Direct Gallery Access or HTML Image URL
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Anexar Imagem (Galeria ou Link Web)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Button to open Gallery Photo Picker directly
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_gallery_button")
                            ) {
                                Text(
                                    text = "Galeria",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeoEmeraldDark
                                    )
                                )
                            }

                            // Button to open File Picker fallback
                            OutlinedButton(
                                onClick = { filePickerLauncher.launch("image/*") },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_files_button")
                            ) {
                                Text(
                                    text = "Arquivos",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White
                                    )
                                )
                            }

                            // Button to toggle Direct HTML/Web Image URL
                            OutlinedButton(
                                onClick = { isEnteringDirectUrl = !isEnteringDirectUrl },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("direct_url_toggle_button")
                            ) {
                                Text(
                                    text = "URL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        // Direct URL field if expanded
                        if (isEnteringDirectUrl) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = directImageUrl,
                                onValueChange = {
                                    directImageUrl = it
                                    imageUri = it
                                },
                                label = { Text("Link direto da imagem (HTML/HTTPS)") },
                                placeholder = { Text("https://exemplo.com/foto.jpg") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = NeoEmerald,
                                    unfocusedBorderColor = OutlineColor
                                )
                            )
                        }

                        // Preview of chosen image
                        if (!imageUri.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Prévia",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, NeoEmerald, RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Imagem pronta para anexo",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeoEmeraldLight),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    imageUri = null
                                    directImageUrl = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remover imagem",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val fullPriority = if (scheduledTime.isNotEmpty()) {
                            "$scheduledTime • $priorityNote"
                        } else {
                            priorityNote
                        }
                        onConfirm(
                            title,
                            selectedCategory,
                            fullPriority,
                            points,
                            scheduledTime,
                            linkUrl.ifBlank { null },
                            imageUri?.ifBlank { null }
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_task_button")
            ) {
                Text(
                    text = "Criar Missão (+$points pts)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeoEmeraldDark
                    )
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Text(text = "Cancelar", color = Color.White)
            }
        }
    )
}

@Composable
fun AdjustMonthlyPointsDialog(
    currentPoints: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var pointsInput by remember { mutableStateOf(currentPoints.toString()) }
    var pointsValue by remember { mutableIntStateOf(currentPoints) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = NeoEmerald,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ajustar Pontos do Mês",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Defina o saldo de pontos acumulados do ciclo atual. O novo valor será salvo permanentemente.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                )

                // Current Points Display & Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saldo Atual:",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                    )
                    Text(
                        text = "$pointsValue pts",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeoEmeraldLight
                        )
                    )
                }

                // Quick Increment/Decrement Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(-50, -10, 10, 50).forEach { delta ->
                        OutlinedButton(
                            onClick = {
                                pointsValue = (pointsValue + delta).coerceAtLeast(0)
                                pointsInput = pointsValue.toString()
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (delta > 0) "+$delta" else "$delta",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (delta > 0) NeoEmerald else SolarAmber
                                )
                            )
                        }
                    }
                }

                // Direct number input
                OutlinedTextField(
                    value = pointsInput,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        pointsInput = digits
                        digits.toIntOrNull()?.let { pointsValue = it }
                    },
                    label = { Text("Digitar quantidade exata") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeoEmerald,
                        unfocusedBorderColor = OutlineColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(pointsValue) },
                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Confirmar ($pointsValue pts)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeoEmeraldDark
                    )
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}
