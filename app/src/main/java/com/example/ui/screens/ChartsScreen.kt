package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConversionEntity
import com.example.data.SettingsEntity
import com.example.ui.DayPoint
import com.example.ui.components.TopAppHeader
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HyperViolet
import com.example.ui.theme.NeoEmerald
import com.example.ui.theme.NeoEmeraldDark
import com.example.ui.theme.NeoEmeraldLight
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark

@Composable
fun ChartsScreen(
    weeklyDays: List<DayPoint>,
    settings: SettingsEntity,
    availablePoints: Int,
    completedTasksCount: Int,
    selectedPeriod: String = "Semana",
    conversions: List<ConversionEntity> = emptyList(),
    onPeriodSelect: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onGoToConverter: () -> Unit
) {
    var activePeriod by remember(selectedPeriod) {
        val initial = when {
            selectedPeriod.startsWith("Sem", ignoreCase = true) -> "Semana"
            selectedPeriod.startsWith("M", ignoreCase = true) -> "Mês"
            selectedPeriod.startsWith("A", ignoreCase = true) -> "Ano"
            else -> "Semana"
        }
        mutableStateOf(initial)
    }

    val currentPeriod = activePeriod

    val todayIdx = remember {
        try {
            (java.time.LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
        } catch (e: Exception) {
            5
        }
    }

    var selectedDayIndex by remember { mutableIntStateOf(todayIdx) }

    val isZeroed = settings.isGraphZeroed || availablePoints == 0

    // Dynamic points for each period: Semana, Mês, Ano
    val currentDisplayDays = remember(currentPeriod, weeklyDays, isZeroed, availablePoints, conversions) {
        when (currentPeriod) {
            "Semana" -> weeklyDays
            "Mês" -> {
                if (isZeroed || availablePoints == 0) {
                    listOf(
                        DayPoint("sem1", "Sem 1", 0),
                        DayPoint("sem2", "Sem 2", 0),
                        DayPoint("sem3", "Sem 3", 0),
                        DayPoint("sem4", "Sem 4", 0)
                    )
                } else {
                    val w1 = (availablePoints * 0.20f).toInt()
                    val w2 = (availablePoints * 0.25f).toInt()
                    val w3 = (availablePoints * 0.32f).toInt()
                    val w4 = (availablePoints - w1 - w2 - w3).coerceAtLeast(0)
                    val pts = listOf(
                        DayPoint("sem1", "Sem 1", w1),
                        DayPoint("sem2", "Sem 2", w2),
                        DayPoint("sem3", "Sem 3", w3),
                        DayPoint("sem4", "Sem 4", w4)
                    )
                    val maxVal = pts.maxOf { it.points }
                    pts.map { it.copy(isPeak = it.points == maxVal && it.points > 0) }
                }
            }
            "Ano" -> {
                val months = listOf(
                    "jan" to "Jan", "fev" to "Fev", "mar" to "Mar", "abr" to "Abr",
                    "mai" to "Mai", "jun" to "Jun", "jul" to "Jul", "ago" to "Ago",
                    "set" to "Set", "out" to "Out", "nov" to "Nov", "dez" to "Dez"
                )
                if (isZeroed) {
                    months.map { (key, label) -> DayPoint(key, label, 0) }
                } else {
                    val curMonthCode = settings.currentMonthName.take(3).lowercase()
                    val yearList = months.map { (key, label) ->
                        val pts = if (key == curMonthCode || key == "out") {
                            availablePoints
                        } else {
                            conversions.filter {
                                it.monthCode.equals(label, ignoreCase = true) ||
                                it.monthName.startsWith(label, ignoreCase = true)
                            }.sumOf { it.pointsConverted }
                        }
                        DayPoint(key, label, pts)
                    }
                    val maxVal = yearList.maxOfOrNull { it.points } ?: 0
                    yearList.map { it.copy(isPeak = it.points == maxVal && it.points > 0) }
                }
            }
            else -> weeklyDays
        }
    }

    // Reset selected index when switching period
    LaunchedEffect(currentPeriod, currentDisplayDays.size) {
        selectedDayIndex = when (currentPeriod) {
            "Semana" -> todayIdx.coerceAtMost(currentDisplayDays.size - 1)
            "Mês" -> 2.coerceAtMost(currentDisplayDays.size - 1)
            "Ano" -> 9.coerceAtMost(currentDisplayDays.size - 1) // Outubro
            else -> 0
        }
    }

    val periodSum = if (isZeroed) 0 else currentDisplayDays.sumOf { it.points }

    val periodTitle = when (currentPeriod) {
        "Semana" -> "PONTUAÇÃO SEMANAL"
        "Mês" -> "PONTUAÇÃO MENSAL (${settings.currentMonthName.uppercase()})"
        "Ano" -> "PONTUAÇÃO ANUAL (2026)"
        else -> "PONTUAÇÃO"
    }

    val periodPoints = "$periodSum pts"

    val peakDay = currentDisplayDays.filter { it.points > 0 }.maxByOrNull { it.points }
    val peakBadgeText = when {
        isZeroed || peakDay == null || peakDay.points == 0 -> "Ciclo Zerado"
        else -> "Pico: ${peakDay.dayLabel} (+${peakDay.points} pts)"
    }

    val statTotal = "$periodSum"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                TopAppHeader(
                    coins = settings.accumulatedCoins,
                    profileImageUri = settings.profileImageUri,
                    onProfileClick = onProfileClick,
                    onCoinsClick = onGoToConverter
                )
            }

            // Title & Live Tag
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DESEMPENHO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeoEmerald,
                                letterSpacing = 1.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(NeoEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ao Vivo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Evolução de Pontos",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Monitore seu ritmo de execução e rendimento do ciclo atual.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariant
                        )
                    )
                }
            }

            // Top Stat Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (selectedPeriod == "Anual") "TOTAL DO ANO" else "TOTAL DO PERÍODO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = statTotal,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 24.sp,
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
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = NeoEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isZeroed) "0.0% vs. anterior" else "+18.4% vs. anterior",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeoEmerald,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val avgTitle = when (currentPeriod) {
                                "Semana" -> "MÉDIA DIÁRIA"
                                "Mês" -> "MÉDIA SEMANAL"
                                "Ano" -> "MÉDIA MENSAL"
                                else -> "MÉDIA P/ DIA"
                            }
                            val divisor = when (currentPeriod) {
                                "Semana" -> 7f
                                "Mês" -> 4f
                                "Ano" -> 10f // Jan a Out
                                else -> 7f
                            }
                            val avgPoints = if (isZeroed || periodSum == 0) {
                                "0.0"
                            } else {
                                String.format(java.util.Locale.US, "%.1f", periodSum.toFloat() / divisor)
                            }
                            val avgUnit = when (currentPeriod) {
                                "Semana" -> "pts/dia"
                                "Mês" -> "pts/sem"
                                "Ano" -> "pts/mês"
                                else -> "pts"
                            }

                            Text(
                                text = avgTitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = avgPoints,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SolarAmber
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = avgUnit,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SolarAmber,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val effDelta = if (isZeroed || periodSum == 0) {
                                    "0.0"
                                } else {
                                    String.format(java.util.Locale.US, "%.1f", (periodSum.toFloat() / divisor) * 0.12f)
                                }
                                Text(
                                    text = if (isZeroed || periodSum == 0) "⚡ 0.0 pts eficiência" else "⚡ +$effDelta pts eficiência",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SolarAmber,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Period Segmented Tabs (Semana, Mês, Ano)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, CardBorder, RoundedCornerShape(9999.dp))
                        .padding(4.dp)
                ) {
                    listOf("Semana", "Mês", "Ano").forEach { period ->
                        val isSelected = currentPeriod == period
                        Surface(
                            onClick = {
                                activePeriod = period
                                onPeriodSelect(period)
                            },
                            shape = RoundedCornerShape(9999.dp),
                            color = if (isSelected) NeoEmerald else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("period_tab_$period")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) NeoEmeraldDark else OnSurface,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Chart Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("weekly_performance_chart_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = periodTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = periodPoints,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(9999.dp),
                                        color = Color(0x2210B981)
                                    ) {
                                        Text(
                                            text = if (isZeroed) "Inativo" else "Consistente",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NeoEmerald,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Peak day badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerHigh,
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = if (isZeroed) OnSurfaceVariant else SolarAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = peakBadgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected Day Tooltip Callout
                        val activeDay = currentDisplayDays.getOrNull(selectedDayIndex) ?: currentDisplayDays.lastOrNull()
                        if (activeDay != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceContainerHigh,
                                    border = BorderStroke(1.dp, NeoEmerald.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = NeoEmerald,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val pointsText = if (activeDay.points > 0) "+${activeDay.points} pts" else "0 pts"
                                        val peakExtra = if (activeDay.isPeak && activeDay.points > 0 && !isZeroed) " ★ (Pico)" else ""
                                        Text(
                                            text = "${activeDay.dayLabel}: $pointsText$peakExtra",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = NeoEmeraldLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Glowing Curved Canvas Line Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(currentDisplayDays) {
                                        detectTapGestures { offset ->
                                            if (currentDisplayDays.isNotEmpty()) {
                                                val colWidth = size.width / currentDisplayDays.size
                                                val clickedIdx = (offset.x / colWidth).toInt()
                                                    .coerceIn(0, currentDisplayDays.size - 1)
                                                selectedDayIndex = clickedIdx
                                            }
                                        }
                                    }
                            ) {
                                if (currentDisplayDays.isEmpty()) return@Canvas

                                val width = size.width
                                val height = size.height
                                val maxVal = (currentDisplayDays.maxOfOrNull { it.points } ?: 100).toFloat().coerceAtLeast(50f)
                                val count = currentDisplayDays.size
                                val colWidth = width / count

                                // Subtle dotted horizontal grid line
                                val guideY = height * 0.45f
                                drawLine(
                                    color = CardBorder.copy(alpha = 0.5f),
                                    start = Offset(colWidth * 0.5f, guideY),
                                    end = Offset(width - colWidth * 0.5f, guideY),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )

                                val pointsCoordinates = currentDisplayDays.mapIndexed { index, dp ->
                                    val x = colWidth * (index + 0.5f)
                                    val normalizedY = if (isZeroed || maxVal == 0f) 0.05f else (dp.points.toFloat() / maxVal).coerceIn(0.05f, 1f)
                                    val y = height - (normalizedY * (height * 0.68f) + height * 0.16f)
                                    Offset(x, y)
                                }

                                // Build smooth bezier path
                                val strokePath = Path().apply {
                                    moveTo(pointsCoordinates.first().x, pointsCoordinates.first().y)
                                    for (i in 0 until pointsCoordinates.size - 1) {
                                        val p0 = pointsCoordinates[i]
                                        val p1 = pointsCoordinates[i + 1]
                                        val controlX = (p0.x + p1.x) / 2
                                        cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                    }
                                }

                                val fillPath = Path().apply {
                                    addPath(strokePath)
                                    lineTo(pointsCoordinates.last().x, height)
                                    lineTo(pointsCoordinates.first().x, height)
                                    close()
                                }

                                // 1. Draw glowing gradient fill under the line
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            NeoEmerald.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )

                                // 2. Draw outer glow stroke
                                drawPath(
                                    path = strokePath,
                                    color = NeoEmerald.copy(alpha = 0.3f),
                                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                                )

                                // 3. Draw main neon emerald stroke
                                drawPath(
                                    path = strokePath,
                                    color = NeoEmerald,
                                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                                )

                                // 4. Draw vertical highlight line for selected day
                                val selectedOffset = pointsCoordinates.getOrNull(selectedDayIndex)
                                if (selectedOffset != null) {
                                    drawLine(
                                        color = NeoEmerald.copy(alpha = 0.45f),
                                        start = Offset(selectedOffset.x, selectedOffset.y),
                                        end = Offset(selectedOffset.x, height),
                                        strokeWidth = 1.5.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                    )
                                }

                                // 5. Draw node circles
                                pointsCoordinates.forEachIndexed { idx, offset ->
                                    val isSelected = idx == selectedDayIndex
                                    val isPeak = currentDisplayDays[idx].isPeak && !isZeroed

                                    // Outer ring
                                    drawCircle(
                                        color = if (isSelected) NeoEmerald else if (isPeak) SolarAmber else NeoEmerald.copy(alpha = 0.8f),
                                        radius = if (isSelected) 8.dp.toPx() else if (isPeak) 6.dp.toPx() else 4.dp.toPx(),
                                        center = offset
                                    )

                                    // Inner core
                                    drawCircle(
                                        color = if (isSelected) Color.White else Color(0xFF0B1326),
                                        radius = if (isSelected) 4.dp.toPx() else if (isPeak) 3.dp.toPx() else 2.dp.toPx(),
                                        center = offset
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // X-axis Day/Period Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            currentDisplayDays.forEachIndexed { idx, day ->
                                val isSelected = idx == selectedDayIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDayIndex = idx },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.dayLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) NeoEmerald else OnSurfaceVariant,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = if (currentDisplayDays.size > 7) 10.sp else 12.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Info Card: Reinício Mensal Automático
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = SolarAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reinício Mensal Automático",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Todo dia 1º seus pontos de tarefas reiniciam para incentivar a consistência contínua. Seu saldo de moedas convertidas nunca expira!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // Bottom CTA: Ir para o Conversor
            item {
                Surface(
                    onClick = onGoToConverter,
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("charts_go_to_converter_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x2210B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = NeoEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Trocar Pontos por Moedas",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "$availablePoints pts disponíveis no ciclo",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Avançar",
                            tint = NeoEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
