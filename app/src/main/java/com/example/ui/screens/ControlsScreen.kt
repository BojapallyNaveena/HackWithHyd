package com.example.ui.screens

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
import com.example.data.model.ComplianceControl
import com.example.data.model.ControlHealth
import com.example.ui.AuditMindViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun ControlsScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val controls by viewModel.allControls.collectAsStateWithLifecycle()

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
                        Icons.Default.VerifiedUser,
                        contentDescription = "Controls",
                        tint = TechCyanLight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "COMPLIANCE CONTROLS CATALOG",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Organizational Controls & Failure History (${controls.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        items(controls, key = { it.id }) { control ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        when (control.healthStatus) {
                            ControlHealth.CRITICAL_RECURRING -> CriticalRed
                            ControlHealth.FAILING -> HighOrange
                            ControlHealth.PARTIAL -> WarningAmber
                            ControlHealth.HEALTHY -> SuccessEmerald
                        }
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
                                    text = "${control.id} (${control.code})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = control.department,
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (control.healthStatus) {
                                ControlHealth.CRITICAL_RECURRING -> CriticalRedBg
                                ControlHealth.FAILING -> Color(0xFF431407)
                                ControlHealth.PARTIAL -> WarningAmberBg
                                ControlHealth.HEALTHY -> SuccessEmeraldBg
                            }
                        ) {
                            Text(
                                text = control.healthStatus.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (control.healthStatus) {
                                    ControlHealth.CRITICAL_RECURRING -> CriticalRed
                                    ControlHealth.FAILING -> HighOrange
                                    ControlHealth.PARTIAL -> WarningAmber
                                    ControlHealth.HEALTHY -> SuccessEmerald
                                },
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = control.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = control.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Failure History Years Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Audit Failures:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (control.failureHistoryYears.isEmpty()) {
                                Text(
                                    text = "None (Fully Compliant)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            } else {
                                control.failureHistoryYears.forEach { yr ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CriticalRedBg,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = "$yr ✕",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CriticalRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Policy: ${control.policyRef}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.selectTimelineControl(control.id)
                                viewModel.navigateTo(Screen.TIMELINE)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Timeline", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.openWhyModal(control.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WHY? History", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
