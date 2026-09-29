package com.example.ui.components

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.export.PdfExportResult
import com.example.ui.AuditMindViewModel
import com.example.ui.theme.*

@Composable
fun AuditPdfExportDialog(
    viewModel: AuditMindViewModel,
    initialYear: Int = 2026,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialYear) }
    val isExporting by viewModel.isExportingPdf.collectAsStateWithLifecycle()
    val lastExport by viewModel.lastPdfExport.collectAsStateWithLifecycle()
    var exportSuccessResult by remember { mutableStateOf<PdfExportResult?>(null) }

    Dialog(onDismissRequest = { if (!isExporting) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanPrimary))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                            color = TechCyanDark.copy(alpha = 0.4f)
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = TechCyanLight,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "STRUCTURED PDF EXPORT",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Export Audit Report",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, enabled = !isExporting) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Generate a formal executive compliance PDF report for external auditors, the Board of Directors, or OCC examiners. Includes findings, overdue remediations, and memory justifications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate300,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECT AUDIT CYCLE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2026, 2025, 2024).forEach { year ->
                        FilterChip(
                            selected = selectedYear == year,
                            onClick = { selectedYear = year },
                            label = { Text("Audit $year", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TechCyanPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scope summary box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Included in this Export ($selectedYear):", style = MaterialTheme.typography.labelSmall, color = SuccessEmerald, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Verified Audit Findings & Severity Classification", style = MaterialTheme.typography.bodySmall, color = Slate300, fontSize = 11.sp)
                        Text("• Overdue Remediation Commitments & Assigned Owners", style = MaterialTheme.typography.bodySmall, color = Slate300, fontSize = 11.sp)
                        Text("• Executive Risk Acceptance Decisions & Compensating Controls", style = MaterialTheme.typography.bodySmall, color = Slate300, fontSize = 11.sp)
                        Text("• Hindsight Institutional Memory Grounding & Citations", style = MaterialTheme.typography.bodySmall, color = Slate300, fontSize = 11.sp)
                    }
                }

                if (exportSuccessResult != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessEmeraldBg,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SuccessEmerald)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PDF Export Ready!", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("File: ${exportSuccessResult!!.file.name}", style = MaterialTheme.typography.bodySmall, color = Slate200, fontSize = 11.sp)
                            Text("Pages: ${exportSuccessResult!!.pageCount} • Size: ${exportSuccessResult!!.fileSizeFormatted}", style = MaterialTheme.typography.labelSmall, color = TechCyanLight, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                if (exportSuccessResult != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.shareAuditPdf(exportSuccessResult!!) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { exportSuccessResult = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("New Export", fontSize = 12.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.exportAuditPdf(selectedYear) { result ->
                                exportSuccessResult = result
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isExporting,
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Compiling PDF Document...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Structured PDF ($selectedYear)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
