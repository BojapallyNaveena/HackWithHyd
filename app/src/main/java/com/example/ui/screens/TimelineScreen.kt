package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Psychology
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
import com.example.ui.components.AuditTimelineView
import com.example.ui.theme.*

@Composable
fun TimelineScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTargetId by viewModel.selectedTimelineControlId.collectAsStateWithLifecycle()
    val selectedTargetType by viewModel.selectedTimelineTargetType.collectAsStateWithLifecycle()
    val timelineNodes by viewModel.timelineNodes.collectAsStateWithLifecycle()
    val allControls by viewModel.allControls.collectAsStateWithLifecycle()
    val recurringFindings by viewModel.recurringFindings.collectAsStateWithLifecycle()

    var viewMode by remember { mutableStateOf(selectedTargetType) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TechCyanDark.copy(alpha = 0.3f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
                ) {
                    Icon(
                        Icons.Default.Timeline,
                        contentDescription = "Timeline",
                        tint = TechCyanLight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AUDIT MEMORY TIMELINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Multi-Year Risk Evolution & Recurrence Trace",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "Trace any compliance control or recurring finding across the 2024, 2025, and 2026 audit cycles. Tap any node to inspect evidence provenance and management rationale.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                lineHeight = 18.sp
            )
        }

        // View Mode Switch (Controls vs Findings)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate800, RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = {
                        viewMode = "CONTROL"
                        viewModel.selectTimelineControl("C-17")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewMode == "CONTROL") TechCyanDark else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "Inspect Controls (${allControls.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewMode == "CONTROL") Color.White else Slate400
                    )
                }

                Button(
                    onClick = {
                        viewMode = "FINDING"
                        viewModel.selectTimelineFinding("F-104")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewMode == "FINDING") TechCyanDark else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "Inspect Findings (${recurringFindings.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewMode == "FINDING") Color.White else Slate400
                    )
                }
            }
        }

        // Horizontal Target Chips Selector
        item {
            Column {
                Text(
                    text = if (viewMode == "CONTROL") "SELECT COMPLIANCE CONTROL:" else "SELECT RECURRING AUDIT FINDING:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (viewMode == "CONTROL") {
                        items(allControls) { ctrl ->
                            FilterChip(
                                selected = selectedTargetId == ctrl.id,
                                onClick = { viewModel.selectTimelineControl(ctrl.id) },
                                label = { Text("${ctrl.id} • ${ctrl.code}", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TechCyanDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    } else {
                        items(recurringFindings) { finding ->
                            FilterChip(
                                selected = selectedTargetId == finding.id,
                                onClick = { viewModel.selectTimelineFinding(finding.id) },
                                label = { Text("${finding.id} (${finding.auditYear})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CriticalRedBg,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // The Interactive Timeline View Component
        item {
            val title = if (viewMode == "CONTROL") {
                allControls.firstOrNull { it.id == selectedTargetId }?.let { "${it.id}: ${it.name}" } ?: selectedTargetId
            } else {
                recurringFindings.firstOrNull { it.id == selectedTargetId }?.let { "${it.id}: ${it.title}" } ?: selectedTargetId
            }

            AuditTimelineView(
                nodes = timelineNodes,
                controlName = title,
                onWhyClick = { viewModel.openWhyModal(selectedTargetId) }
            )
        }
    }
}
