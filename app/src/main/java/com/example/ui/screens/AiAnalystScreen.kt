package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.MemoryTransparencyCard
import com.example.ui.theme.*

@Composable
fun AiAnalystScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isThinking by viewModel.isAgentThinking.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var useMemoryToggle by remember { mutableStateOf(true) }

    val suggestedQuestions = listOf(
        "What are our recurring compliance problems?",
        "Why do these problems keep recurring?",
        "What changed since our last audit?",
        "Prepare an investigation plan for tomorrow."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MemoryPurpleBg,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Analyst",
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI COMPLIANCE ANALYST",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Persistent Memory Grounded Agent",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Memory Toggle for Demo Comparison
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (useMemoryToggle) TechCyanDark.copy(alpha = 0.4f) else Slate700
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (useMemoryToggle) "Memory: ON" else "Memory: OFF",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (useMemoryToggle) TechCyanLight else Slate400,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useMemoryToggle,
                        onCheckedChange = { useMemoryToggle = it },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Suggested preset questions chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestedQuestions) { query ->
                AssistChip(
                    onClick = {
                        inputText = query
                        viewModel.askAnalyst(query, useMemory = useMemoryToggle)
                    },
                    label = { Text(query, fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = TechCyanLight,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Slate800,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Thread
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate800)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = TechCyanLight,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Ask anything about NovaBank's audit history",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "AuditMind continuously recalls past audits, findings, decisions, and evidence to answer questions with institutional memory.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate400,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    viewModel.askAnalyst("Why do our audit problems keep recurring?", useMemory = true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = TechCyanLight)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Grounded Answer & Memory Transparency", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            items(messages.size) { index ->
                val response = messages[index]
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // User Question bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 4.dp, bottomStart = 14.dp, bottomEnd = 14.dp),
                            color = TechCyanDark
                        ) {
                            Text(
                                text = response.query,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Agent Answer Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (response.memoryAware) Slate800 else Slate800.copy(alpha = 0.7f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (response.memoryAware) TechCyanLight else Slate600
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (response.memoryAware) TechCyanLight else Slate400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (response.memoryAware) "AUDITMIND MEMORY AGENT" else "GENERIC AI (NO MEMORY)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (response.memoryAware) TechCyanLight else Slate400,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (response.toolsCalled.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Slate700
                                    ) {
                                        Text(
                                            text = "Tools: ${response.toolsCalled.joinToString(", ")}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate200,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = response.answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                lineHeight = 20.sp
                            )

                            // Feature 5: Memory Transparency component
                            if (response.memoryAware && response.memoriesUsed.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                MemoryTransparencyCard(
                                    memories = response.memoriesUsed,
                                    evidenceChain = response.evidenceChain,
                                    confidence = response.confidence,
                                    onWhyDecisionClick = { decisionId -> viewModel.openWhyModal(decisionId) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = response.disclaimer,
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TechCyanLight,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Hindsight recalling institutional memories & reflecting across multi-year audits...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TechCyanLight,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about findings, decisions, controls...", color = Slate400) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyanLight,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val q = inputText
                        inputText = ""
                        viewModel.askAnalyst(q, useMemory = useMemoryToggle)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(TechCyanPrimary, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
