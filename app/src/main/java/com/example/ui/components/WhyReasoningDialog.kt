package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AuditDecision
import com.example.data.model.AuditFinding
import com.example.data.model.DecisionReasoningChain
import com.example.data.model.RemediationCommitment
import com.example.ui.theme.*

@Composable
fun WhyReasoningDialog(
    targetName: String,
    finding: AuditFinding?,
    decision: AuditDecision?,
    remediation: RemediationCommitment?,
    aiSynthesis: String,
    reasoningChain: DecisionReasoningChain? = null,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent))
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = "Why Analysis",
                                tint = GoldAccent,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GoldAccent
                                ) {
                                    Text(
                                        text = "WHY? RECONSTRUCTION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Navy900,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HINDSIGHT REASONING CHAIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TechCyanLight,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = targetName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                HorizontalDivider(color = Slate700, modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (reasoningChain != null && reasoningChain.reasoningSteps.isNotEmpty()) {
                        // Rich Step-by-Step Reasoning Reconstruction
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate800,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "RECONSTRUCTED CAUSAL CHAIN FOR THIS DECISION:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TechCyanLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reasoningChain.executiveSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate200,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // Each Chronological Reasoning Step
                        reasoningChain.reasoningSteps.forEach { step ->
                            ReasoningStepCard(step = step)
                        }

                        // Root Cause Analysis Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MemoryPurpleBg.copy(alpha = 0.5f)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = "Root Cause",
                                        tint = Color(0xFFC4B5FD),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI ROOT CAUSE SYNTHESIS & FAILED ASSUMPTIONS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFC4B5FD),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = reasoningChain.rootCauseHypothesis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Regulatory Verdict
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CriticalRedBg.copy(alpha = 0.4f)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CriticalRed))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Gavel,
                                        contentDescription = "Verdict",
                                        tint = CriticalRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "REGULATORY SUPERVISORY VERDICT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CriticalRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reasoningChain.regulatoryVerdict,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Standard Fact Item Cards fallback
                        FactItemCard(
                            title = "1. Original Audit Finding (Historical Fact)",
                            badgeText = finding?.auditYear?.toString() ?: "2024",
                            content = finding?.description ?: "Initial control gap identified during comprehensive IT audit."
                        )

                        FactItemCard(
                            title = "2. Risk Acceptance Decision & Rationale (Historical Fact)",
                            badgeText = decision?.decisionType ?: "Risk Acceptance",
                            content = decision?.rationale ?: finding?.riskAcceptanceReason ?: "Accepted temporarily due to ongoing system migration."
                        )

                        FactItemCard(
                            title = "3. Compensating Control Offered (Historical Fact)",
                            badgeText = "Compensating",
                            content = decision?.compensatingControl ?: finding?.compensatingControl ?: "Manual review assigned to operational staff."
                        )

                        FactItemCard(
                            title = "4. Promised Remediation & Deadline (Historical Fact)",
                            badgeText = "Deadline: ${remediation?.deadline ?: "Passed"}",
                            content = "Committed by: ${remediation?.owner ?: "Security Lead"}\nTask: ${remediation?.title ?: "Deploy MFA"}\nStatus: ${if (remediation?.isOverdue == true) "OVERDUE (35% complete)" else "Active"}"
                        )

                        FactItemCard(
                            title = "5. Current Operational Reality (Field Evidence)",
                            badgeText = "Current Status",
                            badgeColor = CriticalRed,
                            content = decision?.currentRealStatus ?: "Compensating controls failed, remediation missed, and issue recurred in subsequent audits."
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MemoryPurpleBg.copy(alpha = 0.5f)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = "AI Analysis",
                                        tint = Color(0xFFC4B5FD),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI ROOT CAUSE RECONSTRUCTION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFC4B5FD),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = aiSynthesis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate200,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark)
                ) {
                    Text("Close Reasoning Chain", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ReasoningStepCard(step: com.example.data.model.DecisionReasoningStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (step.isCriticalAlert) CriticalRedBg.copy(alpha = 0.35f) else Slate800
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (step.isCriticalAlert) CriticalRed.copy(alpha = 0.6f) else Slate700
            )
        )
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
                        color = if (step.isCriticalAlert) CriticalRed else TechCyanDark
                    ) {
                        Text(
                            text = "Phase ${step.stepIndex}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[${step.year}] ${step.phase}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Slate700
                ) {
                    Text(
                        text = step.actor,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate300,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate200,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (step.isCriticalAlert) CriticalRedBg else Slate900
            ) {
                Text(
                    text = "CLASSIFICATION: ${step.factClassification}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (step.isCriticalAlert) CriticalRed else TechCyanLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun FactItemCard(
    title: String,
    badgeText: String,
    content: String,
    badgeColor: Color = TechCyanLight
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FactCheck,
                        contentDescription = "Fact",
                        tint = badgeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = Slate200,
                lineHeight = 16.sp
            )
        }
    }
}
