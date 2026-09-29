package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemoryEvidenceNode
import com.example.ui.theme.*

@Composable
fun AuditTimelineView(
    nodes: List<MemoryEvidenceNode>,
    controlName: String,
    onWhyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedYearFilter by remember { mutableStateOf<Int?>(null) }
    var expandedNodeIndex by remember { mutableStateOf<Int?>(null) }

    val filteredNodes = if (selectedYearFilter == null) {
        nodes
    } else {
        nodes.filter { it.year == selectedYearFilter }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "INTERACTIVE AUDIT MEMORY TIMELINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = controlName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MemoryPurpleBg,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                ) {
                    Text(
                        text = "2024 → 2026 Trace",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC4B5FD),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Year Filter Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedYearFilter == null,
                    onClick = { selectedYearFilter = null },
                    label = { Text("Full Evolution (${nodes.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyanDark,
                        selectedLabelColor = Color.White
                    )
                )
                listOf(2024, 2025, 2026).forEach { yr ->
                    val yrCount = nodes.count { it.year == yr }
                    FilterChip(
                        selected = selectedYearFilter == yr,
                        onClick = { selectedYearFilter = yr },
                        label = { Text("$yr ($yrCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (yr == 2026) CriticalRedBg else TechCyanDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredNodes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events found for this filter.", color = Slate400, fontSize = 12.sp)
                }
            } else {
                filteredNodes.forEachIndexed { index, node ->
                    val isLast = index == filteredNodes.size - 1
                    val isExpanded = expandedNodeIndex == index
                    val nodeColor = when {
                        node.isAlert -> CriticalRed
                        node.type == "DECISION" -> GoldAccent
                        node.type == "REMEDIATION" -> TechCyanLight
                        else -> Slate400
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedNodeIndex = if (isExpanded) null else index
                            },
                        verticalAlignment = Alignment.Top
                    ) {
                        // Node Icon and connecting stem
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(nodeColor.copy(alpha = 0.2f))
                                    .border(2.dp, nodeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (node.isAlert) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "Alert",
                                        tint = CriticalRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (node.type == "REMEDIATION") {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = "Remediation",
                                        tint = TechCyanLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (node.type == "DECISION") {
                                    Icon(
                                        Icons.Default.Gavel,
                                        contentDescription = "Decision",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "Node",
                                        tint = nodeColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            if (!isLast) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(if (isExpanded) 110.dp else 52.dp)
                                        .background(Slate700)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Node Content Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isExpanded) Navy900 else Slate800,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isExpanded) nodeColor else Slate700
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = if (isLast) 0.dp else 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = nodeColor.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "${node.year}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = nodeColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = node.type,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400,
                                            fontSize = 9.sp
                                        )
                                    }

                                    if (node.type == "DECISION") {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Button(
                                                onClick = onWhyClick,
                                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Psychology,
                                                    contentDescription = "Why",
                                                    tint = Navy900,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "WHY?",
                                                    color = Navy900,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = Slate400,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = Slate400,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = node.stepTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (node.isAlert) Color(0xFFFCA5A5) else Color.White
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = node.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate200,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Authority / Owner: ${node.entityOrOwner}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate400,
                                    fontSize = 10.sp
                                )

                                // Expanded details with provenance
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    ) {
                                        Divider(color = Slate700, modifier = Modifier.padding(vertical = 6.dp))
                                        Text(
                                            text = "INSTITUTIONAL MEMORY DETAIL:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TechCyanLight,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = when (node.type) {
                                                "FINDING" -> "Documented in Annual IT Audit report. Associated risk logged into regulatory register."
                                                "DECISION" -> "Executive waiver approved by Risk Oversight Committee on assumption of cloud cutover."
                                                "REMEDIATION" -> "Remediation commitment scheduled; required engineering resources and tool integration."
                                                "RECURRENCE" -> "Flagged as systemic recurrence due to expired risk acceptance and missing verification."
                                                else -> "Captured in persistent Hindsight audit memory."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate200,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = onWhyClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("WHY? View Full Institutional Reasoning Reconstruction", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
