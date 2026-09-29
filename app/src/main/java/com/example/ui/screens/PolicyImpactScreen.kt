package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PolicyDocument
import com.example.ui.AuditMindViewModel
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun PolicyImpactScreen(
    viewModel: AuditMindViewModel,
    modifier: Modifier = Modifier
) {
    val result by viewModel.policyImpactResult.collectAsStateWithLifecycle()
    val uploadedPolicies by viewModel.uploadedPolicies.collectAsStateWithLifecycle()
    val isThinking by viewModel.isAgentThinking.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("UPLOAD") }

    // Form inputs for uploading new or updated policy
    var policyTitle by remember { mutableStateOf("Zero-Trust Privileged Access & Multi-Factor Enforcement") }
    var policyCode by remember { mutableStateOf("POL-SEC-04 v3.0") }
    var version by remember { mutableStateOf("3.0") }
    var department by remember { mutableStateOf("IT Security") }
    var effectiveDate by remember { mutableStateOf("2026-10-01") }
    var policyRequirementText by remember {
        mutableStateOf("Mandatory hardware-backed MFA (FIDO2) required for all root bastion sessions into Core Banking databases. Maximum 60-day limit on temporary risk acceptances. Eliminates manual log review exemptions.")
    }

    var uploadSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MemoryPurpleBg,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                ) {
                    Icon(
                        Icons.Default.CompareArrows,
                        contentDescription = "Policy Impact",
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "POLICY UPLOAD & IMPACT ANALYZER",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechCyanLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hindsight Institutional Memory Forecaster",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "Upload updated compliance policies. AuditMind leverages Hindsight recall & reflect to determine immediate impact on historical controls, past audit findings, and prioritized review areas for compliance officers.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                lineHeight = 18.sp
            )
        }

        // Mode Navigation Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate800, RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { activeTab = "UPLOAD" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "UPLOAD") TechCyanDark else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload & Analyze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = "POLICIES" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "POLICIES") TechCyanDark else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Uploaded (${uploadedPolicies.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (activeTab == "UPLOAD") {
            // Preset Templates
            item {
                Column {
                    Text(
                        text = "OR SELECT A PRESET REGULATORY UPDATE TEMPLATE:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                policyTitle = "Zero-Trust Privileged Access & Bastion MFA Policy"
                                policyCode = "POL-SEC-04 v3.0"
                                version = "3.0"
                                department = "IT Security"
                                effectiveDate = "2026-10-01"
                                policyRequirementText = "Mandatory hardware-backed MFA (FIDO2) required for all root bastion sessions into Core Banking databases. Maximum 60-day limit on temporary risk acceptances. Eliminates manual log review exemptions."
                            },
                            label = { Text("Bastion MFA v3.0", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                policyTitle = "Bi-Annual User Access & Contractor Recertification"
                                policyCode = "POL-IAM-02 v2.0"
                                version = "2.0"
                                department = "Identity & Access Management"
                                effectiveDate = "2026-11-01"
                                policyRequirementText = "Contractor access recertification tightened from 12 months to semi-annual (every 6 months). Automated SCIM de-provisioning required within 24 hours of HR offboarding."
                            },
                            label = { Text("IAM Recertification", fontSize = 11.sp) }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                policyTitle = "Continuous Third-Party Vendor Risk Monitoring"
                                policyCode = "POL-VRM-07 v4.0"
                                version = "4.0"
                                department = "Vendor Risk Management"
                                effectiveDate = "2026-12-01"
                                policyRequirementText = "Mandatory annual SOC 2 Type II reports for all Tier-1 and Tier-2 cloud vendors. Procurement waivers prohibited for vendors processing customer financial data."
                            },
                            label = { Text("Vendor SOC 2 Mandate", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                policyTitle = "Strict 7-Year Transaction Archival & Automatic Purge"
                                policyCode = "POL-DAT-01 v3.0"
                                version = "3.0"
                                department = "Data Governance"
                                effectiveDate = "2026-10-15"
                                policyRequirementText = "Enforce automated cascade deletion and anonymization of all customer transaction records older than 7 years in compliance with GDPR Art 17 and GLBA."
                            },
                            label = { Text("Data Purge Policy", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Upload Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "UPLOAD NEW OR UPDATED COMPLIANCE POLICY",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = policyTitle,
                            onValueChange = { policyTitle = it },
                            label = { Text("Policy Title", color = Slate400) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechCyanLight,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = policyCode,
                                onValueChange = { policyCode = it },
                                label = { Text("Code & Version", color = Slate400) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TechCyanLight,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it },
                                label = { Text("Department", color = Slate400) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TechCyanLight,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = policyRequirementText,
                            onValueChange = { policyRequirementText = it },
                            label = { Text("Policy Requirements / Clause Text", color = Slate400) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechCyanLight,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (policyTitle.isNotBlank() && policyRequirementText.isNotBlank()) {
                                    viewModel.uploadAndAnalyzePolicy(
                                        title = policyTitle,
                                        code = policyCode,
                                        version = version,
                                        department = department,
                                        effectiveDate = effectiveDate,
                                        policyText = policyRequirementText
                                    )
                                    uploadSuccessMessage = "Policy '$policyCode' successfully ingested into Hindsight and evaluated against 3-year audit history!"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload & Run Hindsight Impact Reflection", fontWeight = FontWeight.Bold)
                        }

                        if (uploadSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ $uploadSuccessMessage",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Uploaded Policies List Tab
            items(uploadedPolicies, key = { it.id }) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TechCyanDark
                            ) {
                                Text(
                                    text = doc.code,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Effective: ${doc.effectiveDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = doc.rawText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.uploadAndAnalyzePolicy(
                                    title = doc.title,
                                    code = doc.code,
                                    version = doc.version,
                                    department = doc.department,
                                    effectiveDate = doc.effectiveDate,
                                    policyText = doc.rawText
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-analyze Historical Impact", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (isThinking) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = TechCyanLight,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Hindsight recalling intersecting controls & evaluating historical audit findings...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TechCyanLight,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // Hindsight Policy Impact Assessment Results
        if (result != null) {
            val res = result!!

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HINDSIGHT IMPACT ASSESSMENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CriticalRedBg
                            ) {
                                Text(
                                    text = "HIGH IMPACT DETECTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = res.impactAssessment,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate800,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Impacted Controls", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 10.sp)
                                    Text("${res.affectedControlsCount} Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TechCyanLight)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate800,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Past Findings Impacted", style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 10.sp)
                                    Text("${res.affectedHistoricalFindingsCount} Findings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CriticalRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Areas for Immediate Review by Compliance Officers
                        Text(
                            text = "KEY AREAS FOR IMMEDIATE COMPLIANCE OFFICER REVIEW:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        res.actionItems.forEach { action ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Text("•", color = GoldAccent, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(action, style = MaterialTheme.typography.bodySmall, color = Slate200, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }

            // Hindsight Reflect & Institutional Memory Synthesis
            if (!res.reflectionInsight.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MemoryPurple))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = MemoryPurple, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HINDSIGHT REFLECTION & HISTORICAL SYNTHESIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MemoryPurple,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.reflectionInsight,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate200,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Hindsight Recalled Memories
            if (res.recalledMemories.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TechCyanDark))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RECALLED HISTORICAL MEMORIES (${res.recalledMemories.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechCyanLight,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = TechCyanDark.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "Hindsight Recall",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechCyanLight,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            res.recalledMemories.take(4).forEach { mem ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Slate800,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${mem.temporalYear} • ${mem.entities.firstOrNull() ?: "Compliance"}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = GoldAccent,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "${(mem.confidence * 100).toInt()}% match",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Slate400,
                                                fontSize = 9.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = mem.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate300,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Affected Historical Controls Details
            item {
                Text(
                    text = "HISTORICAL CONTROLS DIRECTLY IMPACTED (${res.affectedControls.size}):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(res.affectedControls) { control ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CriticalRed.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${control.id} (${control.code})",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CriticalRedBg
                            ) {
                                Text(
                                    text = "3-Year Failures: ${control.failureHistoryYears.joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = control.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = control.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Past Audit Findings That Intersect
            item {
                Text(
                    text = "HISTORICAL FINDINGS THAT WOULD FAIL UNDER UPDATED POLICY (${res.affectedFindings.size}):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(res.affectedFindings.take(6)) { finding ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${finding.id} • ${finding.auditYear} Audit",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            SeverityBadge(severity = finding.severity)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = finding.title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
