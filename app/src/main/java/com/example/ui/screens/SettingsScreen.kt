package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CategoryEntity
import com.example.data.SettingsEntity
import com.example.ui.components.TopAppHeader
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NeoEmerald
import com.example.ui.theme.NeoEmeraldDark
import com.example.ui.theme.NeoEmeraldLight
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark

@Composable
fun SettingsScreen(
    categories: List<CategoryEntity>,
    settings: SettingsEntity,
    onUpdateRate: (Int) -> Unit,
    onUpdateCategoryPoints: (Long, Int) -> Unit,
    onAddCategory: (String, String, Int) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onUpdateToggles: (Boolean, Boolean) -> Unit,
    onRestoreDefaults: () -> Unit,
    onResetAll: () -> Unit,
    onResetGraphOnly: () -> Unit,
    onResetPointsAndCoinsOnly: () -> Unit,
    onShowMessage: (String) -> Unit,
    onUpdateMonthlyPoints: (Int) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var pointsState by remember(settings.monthlyPoints) { mutableIntStateOf(settings.monthlyPoints) }
    var rateState by remember(settings.coinsPer10Pts) { mutableIntStateOf(settings.coinsPer10Pts) }
    var autoResetState by remember(settings.autoMonthlyReset) { mutableStateOf(settings.autoMonthlyReset) }
    var earlyAlertState by remember(settings.earlyAlert) { mutableStateOf(settings.earlyAlert) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showResetAllConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                TopAppHeader(
                    coins = settings.accumulatedCoins,
                    profileImageUri = settings.profileImageUri,
                    onProfileClick = onProfileClick
                )
            }

            // Screen Header & Subtitle
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeoEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ECONOMIA & GAME LOOP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeoEmerald,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Text(
                                text = "v2.5 Ativo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Regras de Pontuação",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Defina a equivalência de moedas obtidas e ajuste o ganho de pontos por esforço em cada missão.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariant
                        )
                    )
                }
            }

            // Card: Foto de Perfil & Avatar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("settings_profile_card"),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                        .border(2.dp, NeoEmerald, CircleShape)
                                        .clickable { onProfileClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!settings.profileImageUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = settings.profileImageUri,
                                            contentDescription = "Foto de perfil",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Avatar padrão",
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Sua Foto de Perfil",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (!settings.profileImageUri.isNullOrBlank()) "Foto personalizada ativa" else "Toque para definir sua foto",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (!settings.profileImageUri.isNullOrBlank()) NeoEmeraldLight else OnSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = onProfileClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                                modifier = Modifier.testTag("btn_change_profile_photo")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = NeoEmeraldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Alterar",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeoEmeraldDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Card 0: Saldo de Pontos do Ciclo (Ajuste Direto de Pontos)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("adjust_monthly_points_card"),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x3310B981)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = NeoEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Saldo de Pontos do Mês",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x2210B981)
                            ) {
                                Text(
                                    text = "$pointsState pts",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeoEmeraldLight,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Ajuste o total de pontos acumulados. O valor é salvo permanentemente e persiste mesmo ao fechar e reiniciar o aplicativo.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stepper row with direct typing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = {
                                    pointsState = (pointsState - 10).coerceAtLeast(0)
                                    onUpdateMonthlyPoints(pointsState)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerHigh,
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                    Text("−10", color = SolarAmber, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = if (pointsState == 0) "0" else pointsState.toString(),
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    val newPts = digits.toIntOrNull() ?: 0
                                    pointsState = newPts
                                    onUpdateMonthlyPoints(newPts)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                label = { Text("Digitar pontos exatos") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = NeoEmerald,
                                    unfocusedBorderColor = OutlineColor
                                )
                            )

                            Surface(
                                onClick = {
                                    pointsState = pointsState + 10
                                    onUpdateMonthlyPoints(pointsState)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerHigh,
                                border = BorderStroke(1.dp, CardBorder)
                            ) {
                                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                    Text("+10", color = NeoEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick buttons to adjust points
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(-50, -10, 10, 50).forEach { delta ->
                                OutlinedButton(
                                    onClick = {
                                        pointsState = (pointsState + delta).coerceAtLeast(0)
                                        onUpdateMonthlyPoints(pointsState)
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
                    }
                }
            }

            // Card 1: Conversão de Moedas
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("currency_conversion_rules_card"),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = SolarAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Conversão de Moedas",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x33F59E0B)
                            ) {
                                Text(
                                    text = "Taxa Base",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SolarAmber,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Moedas a cada 10 pontos",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Quantas moedas o jogador desbloqueia ao acumular uma dezena de pontos.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stepper Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x33F59E0B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = SolarAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "A cada 10 pts ganha:",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        onClick = {
                                            if (rateState > 1) {
                                                rateState -= 1
                                                onUpdateRate(rateState)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerLow,
                                        border = BorderStroke(1.dp, CardBorder)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(36.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "−",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Text(
                                        text = "$rateState",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SolarAmber,
                                            fontSize = 22.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    Surface(
                                        onClick = {
                                            if (rateState < 10) {
                                                rateState += 1
                                                onUpdateRate(rateState)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerLow,
                                        border = BorderStroke(1.dp, CardBorder)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(36.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulação Instantânea Container
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SIMULAÇÃO INSTANTÂNEA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = SolarAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "100 pontos valem",
                                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = "${(100 / 10) * rateState}",
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SolarAmber,
                                                    fontSize = 22.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "moedas",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSurfaceVariant
                                                ),
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "500 pontos valem",
                                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = "${(500 / 10) * rateState}",
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SolarAmber,
                                                    fontSize = 22.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "moedas",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSurfaceVariant
                                                ),
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Pontos por Tarefa (Categories)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("task_point_categories_card"),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = NeoEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pontos por Tarefa",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceContainerHigh
                            ) {
                                Text(
                                    text = "${categories.size} Categorias",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category items
                        categories.forEach { cat ->
                            CategorySettingsRow(
                                category = cat,
                                onPointsChange = { newPts ->
                                    onUpdateCategoryPoints(cat.id, newPts)
                                },
                                onDelete = {
                                    onDeleteCategory(cat)
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Add category action button
                        Surface(
                            onClick = { showAddCategoryDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerHigh,
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_new_category_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = NeoEmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Adicionar Nova Categoria de Pontuação",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Card 3: Regras do Ciclo Mensal
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("monthly_cycle_rules_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = NeoEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Regras do Ciclo Mensal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggle 1: Reiniciar pontuação todo mês
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Reiniciar pontuação todo mês",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0x2210B981)
                                    ) {
                                        Text(
                                            text = "Automático",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NeoEmerald,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Zera o saldo de pontos acumulados todo dia 01 às 00:00 para manter a competitividade.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Switch(
                                checked = autoResetState,
                                onCheckedChange = {
                                    autoResetState = it
                                    onUpdateToggles(autoResetState, earlyAlertState)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeoEmeraldDark,
                                    checkedTrackColor = NeoEmerald,
                                    uncheckedThumbColor = OnSurfaceVariant,
                                    uncheckedTrackColor = SurfaceContainerHigh
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggle 2: Alerta prévio de conversão
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Alerta prévio de conversão",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notificar 3 dias antes do fim do mês para converter pontos não utilizados em recompensas.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Switch(
                                checked = earlyAlertState,
                                onCheckedChange = {
                                    earlyAlertState = it
                                    onUpdateToggles(autoResetState, earlyAlertState)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeoEmeraldDark,
                                    checkedTrackColor = NeoEmerald,
                                    uncheckedThumbColor = OnSurfaceVariant,
                                    uncheckedTrackColor = SurfaceContainerHigh
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Actions: Salvar & Restaurar Padrões
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onUpdateMonthlyPoints(pointsState)
                            onUpdateRate(rateState)
                            onUpdateToggles(autoResetState, earlyAlertState)
                            onShowMessage("Regras e pontos salvos com sucesso!")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_rules_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = NeoEmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Salvar Alterações de Regras",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeoEmeraldDark
                            )
                        )
                    }

                    TextButton(
                        onClick = {
                            onRestoreDefaults()
                            pointsState = 420
                            rateState = 2
                            autoResetState = true
                            earlyAlertState = true
                        },
                        modifier = Modifier.testTag("restore_defaults_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Restaurar Padrões Recomendados",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = OnSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Card 4: ZERAR PONTOS, MOEDAS E GRÁFICO (Zona de Reinício)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .testTag("danger_reset_zone_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1F93000A)),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DangerRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Zerar Economia & Gráfico",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DangerRed.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "Reset Total",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DangerRed,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Zere todos os seus pontos acumulados, esvazie o cofre de moedas e redefina o gráfico de rendimento semanal para começar um novo ciclo do zero.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button to trigger full reset
                        Button(
                            onClick = { showResetAllConfirmDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DangerRed,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_reset_all_everything")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zerar Tudo (Pontos, Moedas e Gráfico)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary actions: individual resets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onResetPointsAndCoinsOnly,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_reset_points_coins_only")
                            ) {
                                Text(
                                    text = "Zerar Só Saldo",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = onResetGraphOnly,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_reset_graph_only")
                            ) {
                                Text(
                                    text = "Zerar Só Gráfico",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onConfirm = { name, desc, pts ->
                    onAddCategory(name, desc, pts)
                    showAddCategoryDialog = false
                }
            )
        }

        // Reset All Confirmation Dialog
        if (showResetAllConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetAllConfirmDialog = false },
                containerColor = SurfaceContainer,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zerar Tudo?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                text = {
                    Text(
                        text = "Esta ação vai zerar todos os seus pontos acumulados (0 pts), zerar o cofre de moedas (0 moedas), desmarcar tarefas concluídas e resetar o gráfico semanal para 0 pts. Deseja continuar?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetAll()
                            showResetAllConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Sim, Zerar Tudo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showResetAllConfirmDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun CategorySettingsRow(
    category: CategoryEntity,
    onPointsChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2210B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${category.points}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeoEmeraldLight
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            // Right Steppers & Delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = {
                        if (category.points > 5) onPointsChange(category.points - 5)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainerLow,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Text("−", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${category.points}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeoEmeraldLight
                        )
                    )
                    Text(
                        text = "pts",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 9.sp
                        )
                    )
                }

                Surface(
                    onClick = {
                        if (category.points < 200) onPointsChange(category.points + 5)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainerLow,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir categoria",
                        tint = OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, desc: String, points: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var points by remember { mutableIntStateOf(25) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text("Nova Categoria de Pontuação", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Categoria") },
                    placeholder = { Text("Ex: Super Foco / Leitura") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeoEmerald,
                        unfocusedBorderColor = OutlineColor
                    )
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descrição / Esforço") },
                    placeholder = { Text("Ex: 45 min • Sem distrações") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeoEmerald,
                        unfocusedBorderColor = OutlineColor
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pontos Base:", color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (points > 5) points -= 5 }) {
                            Text("−", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("$points pts", color = NeoEmeraldLight, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (points < 200) points += 5 }) {
                            Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, desc, points) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald)
            ) {
                Text("Adicionar", color = NeoEmeraldDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}
