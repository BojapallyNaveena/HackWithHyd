package com.example.ui.components

import androidx.compose.foundation.background
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
import com.example.data.model.AuditFinding
import com.example.data.model.ComplianceControl
import com.example.data.model.RemediationCommitment
import com.example.ui.theme.*

@Composable
fun RiskRadarDashboardView(
    recurringFindings: List<AuditFinding>,
    overdueRemediations: List<RemediationCommitment>,
    controls: List<ComplianceControl>,
    onWhyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedQuadrant by remember { mutableStateOf("ALL") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CriticalRed.copy(alpha = 0.5f)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CriticalRedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Radar,
                            contentDescription = "Radar",
                            tint = CriticalRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RISK RADAR DASHBOARD",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Multi-Year Risk Patterns & Recurring Failure Radar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CriticalRedBg
                ) {
                    Text(
                        text = "4 Systemic Clusters",
                        style = MaterialTheme.typography.labelSmall,
                        color = CriticalRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Year Recurrence Progression Indicator
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("2024 Audit", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 10.sp)
                        Text("14 Initial Findings", style = MaterialTheme.typography.bodySmall, color = Slate200, fontWeight = FontWeight.Bold)
                        Text("Baseline established", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 9.sp)
                    }
                    Text("→", color = Slate400, fontSize = 14.sp)
                    Column {
                        Text("2025 Audit", style = MaterialTheme.typography.labelSmall, color = WarningAmber, fontSize = 10.sp)
                        Text("4 Recurrences", style = MaterialTheme.typography.bodySmall, color = WarningAmber, fontWeight = FontWeight.Bold)
                        Text("Waivers granted", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 9.sp)
                    }
                    Text("→", color = Slate400, fontSize = 14.sp)
                    Column {
                        Text("2026 Audit", style = MaterialTheme.typography.labelSmall, color = CriticalRed, fontSize = 10.sp)
                        Text("4 Triple Recurrences", style = MaterialTheme.typography.bodySmall, color = CriticalRed, fontWeight = FontWeight.Bold)
                        Text("Pre-exam risk alert", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quadrant Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All Risks",
                    "RECURRING" to "Recurring (${recurringFindings.size})",
                    "OVERDUE" to "Overdue (${overdueRemediations.size})",
                    "CONTROLS" to "Failed Controls"
                ).forEach { (quadrant, label) ->
                    FilterChip(
                        selected = selectedQuadrant == quadrant,
                        onClick = { selectedQuadrant = quadrant },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (quadrant == "RECURRING") CriticalRedBg else TechCyanDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Radar Quadrant Details
            if (selectedQuadrant == "ALL" || selectedQuadrant == "RECURRING") {
                RecurringRiskCard(
                    category = "ACCESS CONTROL (C-17)",
                    title = "Database Bastion MFA Bypass",
                    findingsHistory = "2024 (F-104) → 2025 (F-204) → 2026 (F-301)",
                    whyFlagged = "Compensating manual review ceased Oct 2024. CyberArk deployment stalled on AIX. 89 unauthenticated root sessions recorded in 2026.",
                    onWhyClick = { onWhyClick("C-17") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecurringRiskCard(
                    category = "IDENTITY & ACCESS (C-03)",
                    title = "Orphaned Contractor Accounts",
                    findingsHistory = "2024 (F-102) → 2025 (F-202) → 2026 (F-302)",
                    whyFlagged = "Workday-Okta automated de-provisioning was postponed. 35 departed contractors hold active VPN security entitlements.",
                    onWhyClick = { onWhyClick("C-03") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecurringRiskCard(
                    category = "VENDOR RISK (C-05)",
                    title = "19 Uncertified Tier-1 Vendors",
                    findingsHistory = "2024 (F-108: 8) → 2025 (F-208: 14) → 2026 (F-304: 19)",
                    whyFlagged = "Vendor portal canceled due to budget cuts. Executive procurement waiver expired without remediation.",
                    onWhyClick = { onWhyClick("C-05") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecurringRiskCard(
                    category = "DATA RETENTION (C-09)",
                    title = "6.9M Customer PII Over-Retention",
                    findingsHistory = "2024 (F-112: 4.2M) → 2025 (F-210: 5.8M) → 2026 (F-307: 6.9M)",
                    whyFlagged = "Purge cron crashed on foreign key locks (ORA-02292). Database refactoring postponed to 2027.",
                    onWhyClick = { onWhyClick("C-09") }
                )
            }

            if (selectedQuadrant == "ALL" || selectedQuadrant == "OVERDUE") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "OVERDUE REMEDIATION ACTIONS (${overdueRemediations.size}):",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                overdueRemediations.take(3).forEach { rem ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${rem.id} • ${rem.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Owner: ${rem.owner} • Progress: ${rem.completionPercentage}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate400,
                                    fontSize = 10.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CriticalRedBg
                            ) {
                                Text(
                                    text = "Deadline: ${rem.deadline}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            if (selectedQuadrant == "ALL" || selectedQuadrant == "CONTROLS") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "REPEATED CONTROL FAILURES (MULTI-YEAR AUDIT HISTORY):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TechCyanLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val failingControls = controls.filter { it.failureHistoryYears.size >= 2 }
                failingControls.forEach { ctrl ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${ctrl.id} (${ctrl.code}) • ${ctrl.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Failed Audits: ${ctrl.failureHistoryYears.joinToString(", ")} • ${ctrl.department}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { onWhyClick(ctrl.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("WHY?", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurringRiskCard(
    category: String,
    title: String,
    findingsHistory: String,
    whyFlagged: String,
    onWhyClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy900,
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CriticalRed.copy(alpha = 0.4f))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = TechCyanLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CriticalRedBg
                ) {
                    Text(
                        text = "3-YEAR RECURRENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CriticalRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Audit Sequence: $findingsHistory",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "WHY FLAGGED (EVIDENCE): $whyFlagged",
                style = MaterialTheme.typography.bodySmall,
                color = Slate200,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onWhyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reconstruct Historical Reasoning", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
