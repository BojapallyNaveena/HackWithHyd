package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AuditFinding
import com.example.ui.AuditMindViewModel
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun FindingsScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val findings by viewModel.allFindings.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showPdfDialog by remember { mutableStateOf(false) }

    val filteredFindings = findings.filter { item ->
        val matchesYear = when (selectedFilter) {
            "2026" -> item.auditYear == 2026
            "2025" -> item.auditYear == 2025
            "2024" -> item.auditYear == 2024
            "RECURRING" -> item.isRecurring
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.id.contains(searchQuery, ignoreCase = true) ||
                item.controlName.contains(searchQuery, ignoreCase = true)
        matchesYear && matchesSearch
    }

    if (showPdfDialog) {
        val defaultYear = when (selectedFilter) {
            "2024" -> 2024
            "2025" -> 2025
            else -> 2026
        }
        com.example.ui.components.AuditPdfExportDialog(
            viewModel = viewModel,
            initialYear = defaultYear,
            onDismiss = { showPdfDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        color = TechCyanDark.copy(alpha = 0.3f),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "Findings",
                            tint = TechCyanLight,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AUDIT FINDINGS REPOSITORY",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Audit Records (${findings.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = { showPdfDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(16.dp), tint = TechCyanLight)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by ID, title, control, or keyword...", color = Slate400) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyanLight,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        // Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All (${findings.size})",
                    "2026" to "2026 (10)",
                    "2025" to "2025 (12)",
                    "2024" to "2024 (14)",
                    "RECURRING" to "Recurring (9)"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (key == "RECURRING") CriticalRedBg else TechCyanDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Findings List
        items(filteredFindings, key = { it.id }) { finding ->
            FindingCard(
                finding = finding,
                onWhyClick = { viewModel.openWhyModal(finding.controlId) }
            )
        }
    }
}

@Composable
fun FindingCard(
    finding: AuditFinding,
    onWhyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (finding.isRecurring) CriticalRed.copy(alpha = 0.6f) else Slate700
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ID, Year, Severity, Recurring
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TechCyanDark
                    ) {
                        Text(
                            text = finding.id,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Slate700
                    ) {
                        Text(
                            text = "${finding.auditYear} Audit",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate200,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (finding.isRecurring) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CriticalRedBg
                        ) {
                            Text(
                                text = "RECURRING (${finding.recurrenceCount})",
                                style = MaterialTheme.typography.labelSmall,
                                color = CriticalRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    SeverityBadge(severity = finding.severity)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = finding.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = finding.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate200,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Control: ${finding.controlName} (${finding.controlId})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Owner: ${finding.owner}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }

            if (finding.riskAcceptanceReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Risk Acceptance: ${finding.riskAcceptanceReason}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onWhyClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("WHY? View Institutional Reasoning", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
