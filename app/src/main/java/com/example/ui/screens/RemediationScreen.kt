package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RemediationCommitment
import com.example.data.model.RemediationStatus
import com.example.ui.AuditMindViewModel
import com.example.ui.theme.*

@Composable
fun RemediationScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val remediations by viewModel.allRemediations.collectAsStateWithLifecycle()

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
                    color = TechCyanDark.copy(alpha = 0.3f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanLight))
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Remediations",
                        tint = TechCyanLight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "REMEDIATION COMMITMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Action Tracking & Deadline Governance (${remediations.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        items(remediations, key = { it.id }) { rem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (rem.isOverdue) CriticalRed else Slate700
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
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TechCyanDark
                            ) {
                                Text(
                                    text = "${rem.id} • Control ${rem.controlId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (rem.status) {
                                RemediationStatus.OVERDUE -> CriticalRedBg
                                RemediationStatus.INCOMPLETE -> Color(0xFF431407)
                                RemediationStatus.IN_PROGRESS -> TechCyanDark.copy(alpha = 0.3f)
                                RemediationStatus.VERIFIED -> SuccessEmeraldBg
                                RemediationStatus.WAITING_EVIDENCE -> WarningAmberBg
                            }
                        ) {
                            Text(
                                text = rem.status.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (rem.status) {
                                    RemediationStatus.OVERDUE -> CriticalRed
                                    RemediationStatus.INCOMPLETE -> HighOrange
                                    RemediationStatus.IN_PROGRESS -> TechCyanLight
                                    RemediationStatus.VERIFIED -> SuccessEmerald
                                    RemediationStatus.WAITING_EVIDENCE -> WarningAmber
                                },
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rem.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = rem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Completion: ${rem.completionPercentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Deadline: ${rem.deadline}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (rem.isOverdue) CriticalRed else Slate200,
                                fontWeight = if (rem.isOverdue) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Slate700, RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(rem.completionPercentage / 100f)
                                    .height(6.dp)
                                    .background(
                                        if (rem.isOverdue) CriticalRed else SuccessEmerald,
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Owner: ${rem.owner} (Committed: ${rem.committedDate})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )

                    if (rem.verificationNotes != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Auditor Verification: ${rem.verificationNotes}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (rem.isOverdue) Color(0xFFFCA5A5) else Color(0xFFA7F3D0),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
