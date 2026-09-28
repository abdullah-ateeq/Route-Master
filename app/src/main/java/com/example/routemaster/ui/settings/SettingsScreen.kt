package com.example.routemaster.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.data.AppThemeSetting
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.ui.theme.AccentRose
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.Graphite700
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Graphite850,
            titleContentColor = TextWhitePrimary,
            textContentColor = TextGraySecondary,
            title = {
                Text("Reset Statistics?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text("This will permanently clear your games played, best route, and record moves.", fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetStatistics()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentRose)
                ) {
                    Text("Reset All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhitePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite950,
                    titleContentColor = TextWhitePrimary
                )
            )
        },
        containerColor = Graphite950,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // App Appearance Group
            SettingsGroup(title = "APPEARANCE") {
                // App Theme Selector
                Text(
                    text = "App Theme",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhitePrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeSegmentOption(
                        label = "Dark",
                        isSelected = settings.theme == AppThemeSetting.DARK,
                        onSelect = { viewModel.setAppTheme(AppThemeSetting.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeSegmentOption(
                        label = "Light",
                        isSelected = settings.theme == AppThemeSetting.LIGHT,
                        onSelect = { viewModel.setAppTheme(AppThemeSetting.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeSegmentOption(
                        label = "System",
                        isSelected = settings.theme == AppThemeSetting.SYSTEM,
                        onSelect = { viewModel.setAppTheme(AppThemeSetting.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Board Theme Selector
                Text(
                    text = "Board Material",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhitePrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeSegmentOption(
                        label = "Midnight (Cyber Teal)",
                        isSelected = settings.boardTheme == BoardThemeSetting.MIDNIGHT,
                        onSelect = { viewModel.setBoardTheme(BoardThemeSetting.MIDNIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeSegmentOption(
                        label = "Classic (Bronze Slate)",
                        isSelected = settings.boardTheme == BoardThemeSetting.CLASSIC,
                        onSelect = { viewModel.setBoardTheme(BoardThemeSetting.CLASSIC) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Gameplay Options Group
            SettingsGroup(title = "GAMEPLAY & VISUALS") {
                SettingsSwitchRow(
                    title = "Highlight Longest Route",
                    subtitle = "Luminously illuminate the best connected network",
                    checked = settings.highlightLongestRoute,
                    onCheckedChange = { viewModel.setHighlightLongestRoute(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Show Match Timer",
                    subtitle = "Display elapsed duration during games",
                    checked = settings.showTimer,
                    onCheckedChange = { viewModel.setShowTimer(it) }
                )
            }

            // Audio & Haptics Group
            SettingsGroup(title = "AUDIO & FEEDBACK") {
                SettingsSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Tactile clicks, track chimes, and finish cues",
                    checked = settings.soundEffects,
                    onCheckedChange = { viewModel.setSoundEffects(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibrations for rotation, placement, and records",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { viewModel.setHapticFeedback(it) }
                )
            }

            // Reset Data Group
            SettingsGroup(title = "DATA MANAGEMENT") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Statistics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentRose
                        )
                        Text(
                            text = "Clear high scores, best moves, and records",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    TextButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = AccentRose)
                    ) {
                        Text("Reset", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = AccentTeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Graphite850)
                .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhitePrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Graphite950,
                checkedTrackColor = AccentTeal,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Graphite800
            )
        )
    }
}

@Composable
private fun ThemeSegmentOption(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentTeal else Graphite800)
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Graphite950 else TextGraySecondary
        )
    }
}
