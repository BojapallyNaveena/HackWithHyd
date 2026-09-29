package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AuditMindViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val findings by viewModel.allFindings.collectAsStateWithLifecycle()
    val recurringFindings by viewModel.recurringFindings.collectAsStateWithLifecycle()
    val overdueRemediations by viewModel.overdueRemediations.collectAsStateWithLifecycle()
    val controls by viewModel.allControls.collectAsStateWithLifecycle()

    val criticalOpenCount = findings.count { it.auditYear == 2026 && (it.severity == com.example.data.model.Severity.CRITICAL || it.severity == com.example.data.model.Severity.HIGH) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Landing / Dashboard Hero
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(TechCyanPrimary, MemoryPurple)))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TechCyanDark.copy(alpha = 0.3f),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
                        ) {
                            Text(
                                text = "AUTONOMOUS COMPLIANCE INTELLIGENCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessEmeraldBg
                        ) {
                            Text(
                                text = "Organization: NovaBank",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFA7F3D0),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "AuditMind AI",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "The AI compliance agent that remembers why.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TechCyanLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Turn years of audit history, risk decisions, and remediation promises into persistent institutional memory.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.AI_ANALYST) },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ask AuditMind", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.TIMELINE) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate600),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audit History")
                        }
                    }
                }
            }
        }

        // Critical Demo Experience Card (Without Memory vs With AuditMind Memory)
        item {
            DemoComparisonCard(
                onExploreTimeline = { viewModel.navigateTo(Screen.TIMELINE) }
            )
        }

        // Compliance Overview Metrics
        item {
            Text(
                text = "COMPLIANCE HEALTH OVERVIEW",
                style = MaterialTheme.typography.labelSmall,
                color = TechCyanLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComplianceStatCard(
                    title = "Total Findings",
                    count = "${findings.size}",
                    subtitle = "Across 2024-2026",
                    icon = Icons.Default.Assessment,
                    iconTint = TechCyanLight,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.FINDINGS) }
                )

                ComplianceStatCard(
                    title = "Recurring Risks",
                    count = "${recurringFindings.size}",
                    subtitle = "Persistent patterns",
                    icon = Icons.Default.Warning,
                    iconTint = CriticalRed,
                    bgColor = CriticalRedBg.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.RISK_RADAR) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComplianceStatCard(
                    title = "Overdue Actions",
                    count = "${overdueRemediations.size}",
                    subtitle = "Missed deadlines",
                    icon = Icons.Default.Schedule,
                    iconTint = HighOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.REMEDIATION) }
                )

                ComplianceStatCard(
                    title = "2026 Open High-Risk",
                    count = "$criticalOpenCount",
                    subtitle = "Pre-audit exposure",
                    icon = Icons.Default.Security,
                    iconTint = WarningAmber,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.FINDINGS) }
                )
            }
        }

        // Risk Radar Dashboard Component
        item {
            RiskRadarDashboardView(
                recurringFindings = recurringFindings,
                overdueRemediations = overdueRemediations,
                controls = controls,
                onWhyClick = { controlId -> viewModel.openWhyModal(controlId) }
            )
        }

        // Historical Risk Patterns Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTORICAL RISK PATTERNS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "3-Year Multi-Audit Radar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PatternBar(label = "Access Control (C-17 & C-03)", percentage = 0.95f, alertCount = "3 Recurrences", color = CriticalRed)
                    PatternBar(label = "Vendor Security (C-05)", percentage = 0.80f, alertCount = "19 Unreviewed", color = HighOrange)
                    PatternBar(label = "Data Retention & PII (C-09)", percentage = 0.70f, alertCount = "6.9M Unpurged", color = WarningAmber)
                    PatternBar(label = "Incident Response (C-07)", percentage = 0.40f, alertCount = "Resolved", color = SuccessEmerald)
                    PatternBar(label = "Change Management (C-01)", percentage = 0.35f, alertCount = "Remediated", color = SuccessEmerald)
                }
            }
        }

        // High-Priority Recurring Alert
        item {
            RecurringRiskItemCard(
                category = "Privileged Access Management",
                title = "Control C-17: Legacy Bastion MFA Bypass",
                firstIdentified = 2024,
                recurringCount = 3,
                whyFlagged = "Control failed in 2024 (F-104), 2025 (F-204), and 2026 (F-301). Prior risk acceptances relied on 'Project Horizon' migration which was delayed. Compensating manual log review abandoned.",
                onWhyClick = { viewModel.openWhyModal("C-17") }
            )
        }

        // Fast Action CTA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MemoryPurpleBg.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tomorrow's Audit Preparation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Synthesize 3 years of memories into an actionable field investigation plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.generateInvestigationPlan()
                            viewModel.navigateTo(Screen.AUDIT_PREP)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MemoryPurple),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Prepare Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternBar(
    label: String,
    percentage: Float,
    alertCount: String,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Medium)
            Text(text = alertCount, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Slate700, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .height(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}
