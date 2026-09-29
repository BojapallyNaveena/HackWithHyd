package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
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
fun MemoryHubScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.allMemories.collectAsStateWithLifecycle()

    var recallQuery by remember { mutableStateOf("") }
    var recalledResults by remember { mutableStateOf<List<com.example.data.model.HindsightMemory>>(emptyList()) }
    var isRecalling by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var newFactText by remember { mutableStateOf("") }
    var newTagsText by remember { mutableStateOf("audit:2026, type:finding, department:security") }
    var newEntityText by remember { mutableStateOf("Core Banking Bastion") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
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
                            Icons.Default.Psychology,
                            contentDescription = "Hindsight",
                            tint = Color(0xFFC4B5FD),
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "HINDSIGHT PERSISTENT MEMORY",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Bank: novabank-compliance (${memories.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MemoryPurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retain", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hindsight Memory Mission Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "HINDSIGHT MEMORY MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC4B5FD),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${viewModel.hindsightService.missionStatement}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Live Hindsight Recall Tester
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LIVE HINDSIGHT RECALL TESTER",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = recallQuery,
                            onValueChange = { recallQuery = it },
                            placeholder = { Text("Query semantic & keyword memory...", color = Slate400, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechCyanLight,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (recallQuery.isNotBlank()) {
                                    isRecalling = true
                                    viewModel.testRecall(recallQuery) { results ->
                                        recalledResults = results
                                        isRecalling = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Recall", modifier = Modifier.size(16.dp))
                        }
                    }

                    if (recalledResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Recalled ${recalledResults.size} Matching Memories:",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        recalledResults.forEach { mem ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Navy900,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "[${mem.temporalYear}] ${mem.text}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate200,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Entities: ${mem.entities.joinToString(", ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechCyanLight,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // All Retained Memories
        item {
            Text(
                text = "STORED INSTITUTIONAL MEMORIES (${memories.size}):",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(memories, key = { it.id }) { mem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MemoryPurpleBg
                        ) {
                            Text(
                                text = "${mem.id} • ${mem.temporalYear}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC4B5FD),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate700
                        ) {
                            Text(
                                text = mem.memoryType.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate200,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = mem.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tags: ${mem.tags.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    // Retain New Memory Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Hindsight Retain Primitive", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Store durable compliance fact into persistent institutional memory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                    OutlinedTextField(
                        value = newFactText,
                        onValueChange = { newFactText = it },
                        label = { Text("Durable Fact / Observation", color = Slate400) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTagsText,
                        onValueChange = { newTagsText = it },
                        label = { Text("Tags (comma-separated)", color = Slate400) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEntityText,
                        onValueChange = { newEntityText = it },
                        label = { Text("Primary Entity / System", color = Slate400) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFactText.isNotBlank()) {
                            viewModel.retainCustomMemory(newFactText, newTagsText, 2026, newEntityText)
                            newFactText = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MemoryPurple)
                ) {
                    Text("Retain Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate800
        )
    }
}
