package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AuditMindViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    var endpoint by remember { mutableStateOf(viewModel.hindsightService.cloudEndpoint) }
    var apiKey by remember { mutableStateOf(viewModel.hindsightService.apiKey) }
    var selectedModel by remember { mutableStateOf(viewModel.agent.modelName) }
    var thinkingMode by remember { mutableStateOf(viewModel.agent.isThinkingMode) }
    var reseededSnackbar by remember { mutableStateOf(false) }

    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate700
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Slate200,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SETTINGS & PLATFORM CONFIGURATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Environment, Models & Demo Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Theme Preference Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDarkTheme) Navy900 else Color(0xFFE0F2FE)
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) GoldAccent else TechCyanPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "APPEARANCE & THEME",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isDarkTheme) "Dark Executive Mode" else "Light Enterprise Mode",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isDarkTheme) "Deep slate contrast for audit operations" else "High-clarity light theme for reports",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )
                }
            }
        }

        // Demo Mode Quick Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "1-CLICK DEMO MODE (NOVABANK)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Reset & Reseed Multi-Year Dataset",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.seedNovaBankData()
                                reseededSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reseed Data", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Seeds 36 findings across 2024, 2025, and 2026, 12 controls, 4 recurring chains, management waivers, and Hindsight persistent memories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 11.sp
                    )

                    if (reseededSnackbar) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ NovaBank 3-year audit data & Hindsight memory successfully reseeded!",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hindsight Cloud Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HINDSIGHT CLOUD PERSISTENCE CONFIGURATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC4B5FD),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = endpoint,
                        onValueChange = {
                            endpoint = it
                            viewModel.hindsightService.cloudEndpoint = it
                        },
                        label = { Text("Hindsight API Endpoint", color = Slate400) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MemoryPurple,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = {
                            apiKey = it
                            viewModel.hindsightService.apiKey = it
                        },
                        label = { Text("Hindsight API Key (Optional Cloud Sync)", color = Slate400) },
                        placeholder = { Text("Enter key or leave blank for local autonomous engine", color = Slate400) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MemoryPurple,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Google Cloud SQL Integration Section
        item {
            val isCloudSqlConnected by viewModel.isCloudSqlConnected.collectAsStateWithLifecycle()
            val cloudSqlLastSync by viewModel.cloudSqlLastSync.collectAsStateWithLifecycle()
            val isCloudSqlSyncing by viewModel.isCloudSqlSyncing.collectAsStateWithLifecycle()
            var cloudSqlStatusMsg by remember { mutableStateOf<String?>(null) }
            var cloudQueryResult by remember { mutableStateOf<com.example.data.cloud.CloudSqlQueryResult?>(null) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TechCyanDark.copy(alpha = 0.4f)
                            ) {
                                Icon(
                                    Icons.Default.Storage,
                                    contentDescription = "Cloud SQL",
                                    tint = TechCyanLight,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GOOGLE CLOUD SQL INTEGRATION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TechCyanLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Enterprise PostgreSQL 16 Instance",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isCloudSqlConnected) SuccessEmeraldBg else CriticalRedBg
                        ) {
                            Text(
                                text = if (isCloudSqlConnected) "CONNECTED" else "OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCloudSqlConnected) SuccessEmerald else CriticalRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Instance: ${viewModel.cloudSqlService.config.instanceConnectionName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate300,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Database: ${viewModel.cloudSqlService.config.databaseName} • SSL: ${viewModel.cloudSqlService.config.sslMode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )
                    Text(
                        text = cloudSqlLastSync,
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.testCloudSqlConnection { success, msg ->
                                    cloudSqlStatusMsg = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Cloud SQL", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.syncWithCloudSql { res ->
                                    cloudSqlStatusMsg = res.message
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isCloudSqlSyncing
                        ) {
                            if (isCloudSqlSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp, color = Color.White)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Database", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.executeCloudSqlQuery("SELECT * FROM audit_decisions ORDER BY audit_year DESC;") { res ->
                                cloudQueryResult = res
                                cloudSqlStatusMsg = "Cloud SQL returned ${res.rowCount} rows in ${res.executionTimeMs}ms."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp), tint = TechCyanLight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Query Cloud SQL (Historical Decisions Table)", fontSize = 11.sp, color = Slate200)
                    }

                    if (cloudSqlStatusMsg != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = cloudSqlStatusMsg!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA7F3D0),
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (cloudQueryResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "SQL RESULT (${cloudQueryResult!!.rowCount} rows • ${cloudQueryResult!!.executionTimeMs}ms):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TechCyanLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                                cloudQueryResult!!.rows.take(3).forEach { row ->
                                    Text(
                                        text = row.joinToString(" | "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate300,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Firebase Authentication Status Section
        item {
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val authStatus by viewModel.authStatus.collectAsStateWithLifecycle()

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SuccessEmerald))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessEmeraldBg
                            ) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = "Firebase",
                                    tint = SuccessEmerald,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "FIREBASE AUTHENTICATION STATUS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Google Cloud Identity Platform",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SuccessEmeraldBg
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Authenticated User: ${currentUser?.name ?: "Guest Auditor"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Email: ${currentUser?.email ?: "m.vance@novabank.com"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate300
                    )
                    Text(
                        text = "Firebase UID: ${currentUser?.firebaseUid ?: "fb-usr-mvance-ciso-01"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Auth Provider: Firebase Email / Enterprise Identity Broker",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // AI Engine Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI MODEL & REASONING CONFIGURATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedModel == "gemini-3.5-flash",
                            onClick = {
                                selectedModel = "gemini-3.5-flash"
                                viewModel.agent.modelName = "gemini-3.5-flash"
                            },
                            label = { Text("Gemini 3.5 Flash", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TechCyanDark, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = selectedModel == "gemini-3.1-pro-preview",
                            onClick = {
                                selectedModel = "gemini-3.1-pro-preview"
                                viewModel.agent.modelName = "gemini-3.1-pro-preview"
                            },
                            label = { Text("Gemini 3.1 Pro", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TechCyanDark, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = selectedModel == "llama-3.3-70b-versatile",
                            onClick = {
                                selectedModel = "llama-3.3-70b-versatile"
                                viewModel.agent.modelName = "llama-3.3-70b-versatile"
                            },
                            label = { Text("Groq Llama 3.3", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TechCyanDark, selectedLabelColor = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "High Thinking Mode (Reasoning)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Deep multi-turn synthesis across 3 years of historical records",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = thinkingMode,
                            onCheckedChange = {
                                thinkingMode = it
                                viewModel.agent.isThinkingMode = it
                            }
                        )
                    }
                }
            }
        }

        // About & Safety Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SAFETY & REGULATORY COMPLIANCE NOTICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AuditMind AI provides automated institutional memory retrieval, historical pattern detection, and reasoning support. All compliance recommendations are AI-generated analysis and must be independently verified against applicable statutory regulations and organizational policy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
