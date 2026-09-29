package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.components.RecurringRiskItemCard
import com.example.ui.components.RiskRadarDashboardView
import com.example.ui.theme.*

@Composable
fun RiskRadarScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val recurringFindings by viewModel.recurringFindings.collectAsStateWithLifecycle()
    val overdueRemediations by viewModel.overdueRemediations.collectAsStateWithLifecycle()
    val allControls by viewModel.allControls.collectAsStateWithLifecycle()
    val allDecisions by viewModel.allDecisions.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("ALL") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CriticalRedBg,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CriticalRed))
                ) {
                    Icon(
                        Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = CriticalRed,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "RECURRING RISK RADAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Autonomous Historical Pattern Detection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Dedicated Visual Risk Radar Component
        item {
            RiskRadarDashboardView(
                recurringFindings = recurringFindings,
                overdueRemediations = overdueRemediations,
                controls = allControls,
                onWhyClick = { controlId -> viewModel.openWhyModal(controlId) }
            )
        }

        item {
            Text(
                text = "INDIVIDUAL RECURRENCE & DEFICIT DETAIL RECORDS:",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Radar Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == "ALL",
                    onClick = { selectedTab = "ALL" },
                    label = { Text("All Alerts (${recurringFindings.size + overdueRemediations.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyanDark,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedTab == "RECURRING",
                    onClick = { selectedTab = "RECURRING" },
                    label = { Text("Recurring (${recurringFindings.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CriticalRedBg,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedTab == "OVERDUE",
                    onClick = { selectedTab = "OVERDUE" },
                    label = { Text("Overdue (${overdueRemediations.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarningAmberBg,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedTab == "DECISIONS",
                    onClick = { selectedTab = "DECISIONS" },
                    label = { Text("Decisions & Waivers (${allDecisions.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Radar Items with Evidence Disclosure
        if (selectedTab == "ALL" || selectedTab == "RECURRING") {
            item {
                RecurringRiskItemCard(
                    category = "ACCESS CONTROL (C-17)",
                    title = "Core Banking Database Bastion MFA Bypass",
                    firstIdentified = 2024,
                    recurringCount = 3,
                    whyFlagged = "Appeared in 2024 (F-104), 2025 (F-204), and 2026 (F-301). Previous remediation R-22 incomplete (35%). Compensating manual reviews ceased Oct 2024. Current 2026 logs show 89 direct unauthenticated root logins.",
                    onWhyClick = { viewModel.openWhyModal("C-17") }
                )
            }

            item {
                RecurringRiskItemCard(
                    category = "ACCESS CONTROL & IAM (C-03)",
                    title = "Orphaned Contractor Active Directory Accounts",
                    firstIdentified = 2024,
                    recurringCount = 3,
                    whyFlagged = "Identified across 3 consecutive audits (F-102, F-202, F-302). Workday-to-Okta automated de-provisioning was postponed. 35 departed contractors currently retain active VPN security profiles.",
                    onWhyClick = { viewModel.openWhyModal("C-03") }
                )
            }

            item {
                RecurringRiskItemCard(
                    category = "VENDOR RISK MANAGEMENT (C-05)",
                    title = "19 Critical Tier-1 Vendors Lacking SOC 2 Compliance",
                    firstIdentified = 2024,
                    recurringCount = 3,
                    whyFlagged = "Grew from 8 unreviewed vendors (2024) to 14 (2025) to 19 (2026). Vendor portal was canceled due to procurement budget cuts; executive waiver #PW-2025-09 expired without replacement.",
                    onWhyClick = { viewModel.openWhyModal("C-05") }
                )
            }

            item {
                RecurringRiskItemCard(
                    category = "DATA RETENTION & PRIVACY (C-09)",
                    title = "6.9M Customer PII Records Violating 7-Year Ceiling",
                    firstIdentified = 2024,
                    recurringCount = 3,
                    whyFlagged = "Purge cron crashed continuously on foreign key constraints (ORA-02292). Management postponed database refactoring to 2027, creating compounding GDPR Article 17 and GLBA exposure.",
                    onWhyClick = { viewModel.openWhyModal("C-09") }
                )
            }
        }

        if (selectedTab == "ALL" || selectedTab == "OVERDUE") {
            item {
                Text(
                    text = "OVERDUE REMEDIATION ACTIONS WITH HISTORICAL IMPACT",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(overdueRemediations.size) { index ->
                val rem = overdueRemediations[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WarningAmber))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTION ${rem.id} • CONTROL ${rem.controlId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CriticalRedBg
                            ) {
                                Text(
                                    text = "OVERDUE (${rem.deadline})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = rem.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Committed by: ${rem.owner}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = rem.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200,
                            lineHeight = 16.sp
                        )

                        if (rem.verificationNotes != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Audit Verification: ${rem.verificationNotes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedTab == "ALL" || selectedTab == "DECISIONS") {
            item {
                Text(
                    text = "HISTORICAL RISK ACCEPTANCES & EXECUTIVE DECISIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(allDecisions.size) { index ->
                val dec = allDecisions[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent.copy(alpha = 0.4f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GoldAccent.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${dec.id} • ${dec.year}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dec.decisionType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate300,
                                    fontSize = 10.sp
                                )
                            }

                            // Prominent 'WHY?' button right next to historical decision
                            Button(
                                onClick = { viewModel.reconstructWhyForDecision(dec) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("WHY?", color = Navy900, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = dec.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Decision Maker: ${dec.decisionMaker} | Control: ${dec.controlId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Rationale: ${dec.rationale}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CriticalRedBg.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Current Status: ${dec.currentRealStatus}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFCA5A5),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
