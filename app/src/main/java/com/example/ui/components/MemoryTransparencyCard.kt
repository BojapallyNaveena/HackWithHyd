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
import com.example.data.model.HindsightMemory
import com.example.data.model.MemoryEvidenceNode
import com.example.data.model.MemoryType
import com.example.ui.theme.*

@Composable
fun MemoryTransparencyCard(
    memories: List<HindsightMemory>,
    evidenceChain: List<MemoryEvidenceNode>,
    confidence: String = "High (98%)",
    onWhyDecisionClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    var selectedViewTab by remember { mutableStateOf(0) } // 0: Evidence Chain, 1: Memories List

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Memory Grounding Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MemoryPurpleBg,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = "Memory Transparency",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MEMORY TRANSPARENCY: ${memories.size.coerceAtLeast(3)} SOURCES",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC4B5FD),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SuccessEmeraldBg
                    ) {
                        Text(
                            text = "Confidence: $confidence",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA7F3D0),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = Slate400
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Subtitle / Relationship badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Slate800,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Hub,
                                contentDescription = null,
                                tint = TechCyanLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Causal Evidence Graph: Findings ➔ Decisions ➔ Remediations",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate200,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Selector: Evidence Chain Visualizer vs Memories List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedViewTab == 0,
                            onClick = { selectedViewTab = 0 },
                            label = { Text("Evidence Chain Visualizer", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AccountTree,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (selectedViewTab == 0) TechCyanLight else Slate400
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TechCyanDark,
                                selectedLabelColor = Color.White
                            )
                        )

                        FilterChip(
                            selected = selectedViewTab == 1,
                            onClick = { selectedViewTab = 1 },
                            label = { Text("Recalled Memories (${memories.size.coerceAtLeast(3)})", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ListAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (selectedViewTab == 1) Color(0xFFC4B5FD) else Slate400
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MemoryPurpleBg,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedViewTab == 0) {
                        // EVIDENCE CHAIN VISUALIZER: Highlights Relationship between Findings, Remediation, and Decisions
                        EvidenceChainVisualizerSection(
                            evidenceChain = evidenceChain,
                            onWhyDecisionClick = onWhyDecisionClick
                        )
                    } else {
                        // HISTORICAL MEMORIES LIST
                        HistoricalMemoriesListSection(
                            memories = memories,
                            onWhyDecisionClick = onWhyDecisionClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceChainVisualizerSection(
    evidenceChain: List<MemoryEvidenceNode>,
    onWhyDecisionClick: ((String) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Causal Chain Overview Card
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Slate800,
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "HOW INSTITUTIONAL MEMORY EXPLAINS THIS DEFICIT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TechCyanLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A finding identified in the 2024 audit prompted a management risk acceptance decision. In exchange, compensating manual controls and technical remediations were committed. When the remediation stalled, the decision was not revoked, causing recurring high-severity findings in subsequent audits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate200,
                    lineHeight = 16.sp
                )
            }
        }

        // Causal Graph Steps
        // Node 1: Finding
        EvidenceStepNodeCard(
            stepNumber = 1,
            nodeType = "FINDING",
            badgeColor = TechCyanLight,
            title = "Initial Finding Discovered (F-104)",
            year = 2024,
            owner = "Audit Team -> IT Security",
            description = "Oracle Core Banking database bastion lacked multi-factor authentication. Direct interactive access via SSH keys.",
            relationshipBadge = "Prompted Management Risk Review ↓"
        )

        // Node 2: Management Decision (WITH 'WHY?' BUTTON)
        EvidenceStepNodeCard(
            stepNumber = 2,
            nodeType = "MANAGEMENT DECISION",
            badgeColor = GoldAccent,
            title = "Decision DEC-2024-01: Risk Acceptance Approved",
            year = 2024,
            owner = "Marcus Vance (CISO)",
            description = "Risk accepted temporarily due to upcoming Project Horizon cloud decommission planned for Q1 2025. Compensating control: weekly manual log reviews.",
            relationshipBadge = "Legally Bound Remediation Plan ↓",
            decisionId = "DEC-2024-01",
            onWhyClick = onWhyDecisionClick
        )

        // Node 3: Remediation Commitment
        EvidenceStepNodeCard(
            stepNumber = 3,
            nodeType = "REMEDIATION COMMITMENT",
            badgeColor = MemoryPurple,
            title = "Remediation R-22: Deploy CyberArk PAM",
            year = 2025,
            owner = "Infrastructure & IAM Operations",
            description = "Committed automated PAM jump host deployment. Stalled at 35% completion due to AIX kernel panics. Security analyst resigned; manual log reviews ceased.",
            relationshipBadge = "Remediation Failure Caused Recurrence ↓",
            isAlert = true
        )

        // Node 4: Consecutive Recurrence Finding
        EvidenceStepNodeCard(
            stepNumber = 4,
            nodeType = "SYSTEMIC RECURRENCE",
            badgeColor = CriticalRed,
            title = "Finding F-204 & F-301: Unresolved Recurrence",
            year = 2026,
            owner = "AuditMind Continuous Inspection",
            description = "Deficit reappeared for the 3rd consecutive audit cycle. 89 direct root sessions logged without MFA. Elevated to systemic supervisory issue.",
            relationshipBadge = "Unresolved Compliance Exposure",
            isAlert = true
        )
    }
}

@Composable
private fun EvidenceStepNodeCard(
    stepNumber: Int,
    nodeType: String,
    badgeColor: Color,
    title: String,
    year: Int,
    owner: String,
    description: String,
    relationshipBadge: String,
    isAlert: Boolean = false,
    decisionId: String? = null,
    onWhyClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) CriticalRedBg.copy(alpha = 0.4f) else Slate800
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isAlert) CriticalRed.copy(alpha = 0.5f) else Slate700
            )
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.2f))
                            .border(1.5.dp, badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNumber",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = nodeType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "[$year]",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                }

                // If this is a decision node, render the prominent 'WHY?' button right next to it!
                if (decisionId != null && onWhyClick != null) {
                    Button(
                        onClick = { onWhyClick(decisionId) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
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
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Owner: $owner",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate200,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Connector link badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Slate700.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = TechCyanLight,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = relationshipBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoricalMemoriesListSection(
    memories: List<HindsightMemory>,
    onWhyDecisionClick: ((String) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "HINDSIGHT PERSISTENT MEMORIES RECALLED FOR THIS ANSWER:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TechCyanLight,
            fontSize = 10.sp
        )

        memories.take(5).forEach { memory ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (memory.memoryType) {
                                    MemoryType.FINDING -> TechCyanDark
                                    MemoryType.DECISION -> GoldAccent.copy(alpha = 0.2f)
                                    MemoryType.REMEDIATION -> MemoryPurpleBg
                                    else -> Slate700
                                }
                            ) {
                                Text(
                                    text = memory.memoryType.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (memory.memoryType) {
                                        MemoryType.DECISION -> GoldAccent
                                        MemoryType.REMEDIATION -> Color(0xFFC4B5FD)
                                        else -> TechCyanLight
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "${memory.temporalYear} Audit Memory",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        }

                        // If memory is a decision, allow 1-click 'WHY?'
                        if (memory.memoryType == MemoryType.DECISION && onWhyDecisionClick != null) {
                            Button(
                                onClick = { onWhyDecisionClick(memory.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("WHY?", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = memory.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tags: ${memory.tags.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400,
                            fontSize = 9.sp
                        )

                        Text(
                            text = "Confidence: ${(memory.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
