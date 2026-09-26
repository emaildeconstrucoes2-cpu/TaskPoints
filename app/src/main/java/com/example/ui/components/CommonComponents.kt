package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.util.FileUtils
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GlowAmber
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
fun TopAppHeader(
    coins: Int,
    profileImageUri: String? = null,
    onProfileClick: () -> Unit = {},
    onCoinsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity: Logo icon + Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("app_branding_row")
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F2B20), Color(0xFF061410))
                        )
                    )
                    .border(
                        1.dp,
                        NeoEmerald.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = "TaskPoints Logo",
                    tint = NeoEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "TaskPoints",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        // Coins Badge + Profile Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Coin Counter Pill
            Surface(
                onClick = onCoinsClick,
                shape = RoundedCornerShape(9999.dp),
                color = SurfaceContainerHigh,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.testTag("coins_counter_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Moedas",
                        tint = SolarAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$coins",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // User Profile Avatar with Online indicator
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh)
                    .border(1.dp, NeoEmerald.copy(alpha = 0.6f), CircleShape)
                    .clickable { onProfileClick() }
                    .testTag("profile_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                if (!profileImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Sua foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Active green dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(NeoEmerald)
                        .border(1.5.dp, SurfaceDark, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun SquircleCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(targetValue = if (checked) 1.05f else 1f, label = "scale")
    val bgColor by animateColorAsState(
        targetValue = if (checked) NeoEmerald else Color.Transparent,
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) NeoEmerald else OutlineColor,
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .scale(scale)
            .clip(RoundedCornerShape(7.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(7.dp))
            .clickable { onCheckedChange() }
            .testTag("squircle_checkbox"),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Concluída",
                tint = NeoEmeraldDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun FullImageViewerDialog(
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.94f))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_image_viewer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar visualizador",
                            tint = Color.White
                        )
                    }
                }

                // Image display
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagem da tarefa",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Bottom link open action if URL
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    Surface(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao abrir link", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(9999.dp),
                        color = SurfaceContainerHigh,
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Abrir original",
                                tint = NeoEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Abrir Imagem no Navegador",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePictureDialog(
    currentImageUri: String?,
    onDismiss: () -> Unit,
    onSaveImage: (String?) -> Unit
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf(currentImageUri) }
    var urlInput by remember { mutableStateOf("") }
    var isInputtingUrl by remember { mutableStateOf(false) }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80"
    )

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = FileUtils.copyUriToInternalStorage(context, uri, "profile_avatar")
            selectedUri = permanentPath
            urlInput = ""
        }
    }

    // Android General File / Gallery Picker fallback
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentPath = FileUtils.copyUriToInternalStorage(context, uri, "profile_avatar")
            selectedUri = permanentPath
            urlInput = ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = NeoEmerald,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sua Foto de Perfil",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Escolha um avatar da galeria, arquivos do aparelho ou selecione um modelo pronto abaixo.",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )

                // Avatar Preview
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                        .border(2.5.dp, NeoEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!selectedUri.isNullOrBlank()) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Prévia da foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Quick Presets label
                Text(
                    text = "Ou escolha um avatar rápido:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeoEmeraldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                // Horizontal scroll of preset avatars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetAvatars.forEachIndexed { index, avatarUrl ->
                        val isChosen = selectedUri == avatarUrl
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                                .border(
                                    width = if (isChosen) 2.5.dp else 1.dp,
                                    color = if (isChosen) NeoEmerald else OutlineColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedUri = avatarUrl
                                    urlInput = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar modelo ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }
                }

                // Action buttons: Galeria, Arquivos, URL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_pick_profile_gallery")
                    ) {
                        Text(
                            text = "Galeria",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeoEmeraldDark
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_pick_profile_files")
                    ) {
                        Text("Arquivos", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = { isInputtingUrl = !isInputtingUrl },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_toggle_profile_url")
                    ) {
                        Text("Link URL", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }

                // URL Input if toggled
                if (isInputtingUrl) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it
                            if (it.isNotBlank()) {
                                selectedUri = it.trim()
                            }
                        },
                        label = { Text("URL da imagem (HTTPS)") },
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

                // Remove photo button if exists
                if (!selectedUri.isNullOrBlank()) {
                    TextButton(
                        onClick = {
                            selectedUri = null
                            urlInput = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Remover Foto", color = DangerRed)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalUri = if (isInputtingUrl && urlInput.isNotBlank()) urlInput.trim() else selectedUri
                    onSaveImage(finalUri)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeoEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_save_profile_picture")
            ) {
                Text(
                    text = "Salvar Foto",
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
